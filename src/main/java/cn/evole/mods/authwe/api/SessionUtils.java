package cn.evole.mods.authwe.api;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import cn.evole.mods.authwe.Const;
import cn.evole.mods.authwe.mixin.*;
import com.mojang.authlib.Agent;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.exceptions.AuthenticationException;
import com.mojang.authlib.minecraft.UserApiService;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import com.mojang.authlib.yggdrasil.YggdrasilMinecraftSessionService;
import com.mojang.authlib.yggdrasil.YggdrasilUserAuthentication;
import com.mojang.realmsclient.client.RealmsClient;
import com.mojang.realmsclient.gui.RealmsDataFetcher;
import net.minecraft.client.Minecraft;
import net.minecraft.client.User;
import net.minecraft.client.gui.screens.social.PlayerSocialManager;
import net.minecraft.client.multiplayer.ProfileKeyPairManager;
import net.minecraft.client.multiplayer.chat.report.ReportingContext;

/**
 * Author cnlimiter
 * CreateTime 2023/7/3 0:12
 * Name SessionUtils
 * Description
 */

public class SessionUtils {
    // The access token used for offline sessions
    public static final String OFFLINE_TOKEN = "invalidtoken";
    // The number of milliseconds that a session status is cached for
    public static final long STATUS_TTL = 60_000L; // 60s
    // The time of the last session status check (milliseconds since epoch)
    private static long lastStatusCheck;
    // The last session status value
    private static SessionStatus lastStatus = SessionStatus.UNKNOWN;

    private SessionUtils() {}

    /**
     * Returns the current Minecraft session.
     *
     * @return current Minecraft session instance
     */
    public static User getSession()
    {
        return Minecraft.getInstance().getUser();
    }

    /**
     * Replaces the Minecraft session instance.
     *
     * @param session new Minecraft session
     */
    public static void setSession(User session)
    {
        final Minecraft client = Minecraft.getInstance();

        // Use an accessor mixin to update the 'private final' Minecraft session
        ((MinecraftAccessor) client).setUser(session);
        ((SplashManagerAccessor) client.getSplashManager()).setUser(session);

        // Refresh the session properties
        client.getProfileProperties().clear();
        client.getProfileProperties();

        // Re-create the user API service (ignore offline session)
        UserApiService userApiService = UserApiService.OFFLINE;
        if (!OFFLINE_TOKEN.equals(session.getAccessToken())) {
            try {
                userApiService = getAuthService().createUserApiService(session.getAccessToken());
            } catch (AuthenticationException e) {
                Const.LOGGER.error("Failed to verify authentication for new user API service!", e);
            }
        }
        ((MinecraftAccessor) client).setUserApiService(userApiService);

        // Re-create the social interactions manager
        ((MinecraftAccessor) client).setPlayerSocialManager(
                new PlayerSocialManager(client, userApiService)
        );

        // Re-create the profile keys
        ((MinecraftAccessor) client).setProfileKeyPairManager(
                ProfileKeyPairManager.create(userApiService, session, client.gameDirectory.toPath())
        );

        // Re-create the abuse report context
        ((MinecraftAccessor) client).setReportingContext(
                ReportingContext.create(
                        ((ReportingContextAccessor) (Object) client.getReportingContext()).getEnvironment(),
                        userApiService
                )
        );

        // Necessary for Realms to re-check for a valid session
        RealmsClient realmsClient = RealmsClient.create(client);
        ((MinecraftAccessor) client).setRealmsDataFetcher(new RealmsDataFetcher(realmsClient));
        RealmsMainScreenAccessor.setCheckedClientCompatibility(false);
        RealmsMainScreenAccessor.setRealmsGenericErrorScreen(null);

        // The cached status is now stale
        lastStatus = SessionStatus.UNKNOWN;
        lastStatusCheck = 0;

        Const.LOGGER.info("Minecraft session for {} (uuid={}) has been applied", session.getName(), session.getUuid());
    }

    /**
     * Builds and returns a new offline Minecraft session.
     *
     * @param username custom username
     * @return a new offline Minecraft session
     * @see #setSession(User)  to apply the new session
     */
    public static User offline(String username)
    {
        return new User(
                username,
                UUID.nameUUIDFromBytes(("offline:" + username).getBytes()).toString(),
                OFFLINE_TOKEN,
                Optional.empty(),
                Optional.empty(),
                User.Type.LEGACY
        );
    }

    /**
     * Checks and returns the current Minecraft session status.
     *
     * <p>NB: This is an expensive task as it involves connecting to servers to
     * validate any access tokens, and hence is executed on a separate thread.
     *
     * <p>The session status is cached for about 1 minute for subsequent calls.
     *
     * @return a completable future for the Minecraft session status
     */
    public static CompletableFuture<SessionStatus> getStatus()
    {
        // Check if the status has already been checked recently
        if (System.currentTimeMillis() - lastStatusCheck < STATUS_TTL) {
            return CompletableFuture.completedFuture(lastStatus);
        }

        // Otherwise, return an asynchronous action to check the session status
        return CompletableFuture.supplyAsync(() -> {
            // Fetch the current session
            final User session = getSession();
            final GameProfile profile = session.getGameProfile();
            final String token = session.getAccessToken();
            final String id = UUID.randomUUID().toString();

            // Attempt to join the Minecraft Session Service server
            final YggdrasilMinecraftSessionService sessionService = getSessionService();
            try {
                Const.LOGGER.info("Verifying Minecraft session...");
                sessionService.joinServer(profile, token, id);
                if (sessionService.hasJoinedServer(profile, id, null).isComplete()) {
                    Const.LOGGER.info("The Minecraft session is valid");
                    lastStatus = SessionStatus.VALID;
                } else {
                    Const.LOGGER.warn("The Minecraft session is invalid!");
                    lastStatus = SessionStatus.INVALID;
                }
            } catch (AuthenticationException e) {
                Const.LOGGER.error("Could not validate the Minecraft session!", e);
                lastStatus = SessionStatus.OFFLINE;
            }

            // Update the last status check and return
            lastStatusCheck = System.currentTimeMillis();
            return lastStatus;
        });
    }

    /**
     * Returns the Yggdrasil Minecraft Session Service.
     *
     * @return Yggdrasil Minecraft Session Service instance
     */
    public static YggdrasilMinecraftSessionService getSessionService()
    {
        return (YggdrasilMinecraftSessionService) Minecraft.getInstance().getMinecraftSessionService();
    }

    /**
     * Returns the Yggdrasil Authentication Service.
     *
     * @return Yggdrasil Authentication Service instance
     */
    public static YggdrasilAuthenticationService getAuthService()
    {
        final YggdrasilAuthenticationService authService = getSessionService().getAuthenticationService();

        // Provide a random client token if not set
        if (((YggdrasilAuthenticationServiceAccessor) authService).getClientToken() == null) {
            ((YggdrasilAuthenticationServiceAccessor) authService).setClientToken(UUID.randomUUID().toString());
        }

        return authService;
    }

    /**
     * Returns the Yggdrasil User Authentication provider.
     *
     * @return Yggdrasil User Authentication instance
     */
    public static YggdrasilUserAuthentication getAuthProvider()
    {
        return (YggdrasilUserAuthentication) getAuthService().createUserAuthentication(Agent.MINECRAFT);
    }

    /**
     * The status of a Minecraft session.
     *
     * @see #getStatus() for the current session status
     */
    public enum SessionStatus
    {
        VALID, INVALID, OFFLINE, UNKNOWN
    }
}
