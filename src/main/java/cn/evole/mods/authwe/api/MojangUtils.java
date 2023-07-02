package cn.evole.mods.authwe.api;

import cn.evole.mods.authwe.Const;
import com.mojang.authlib.yggdrasil.YggdrasilUserAuthentication;
import com.mojang.util.UUIDTypeAdapter;
import net.minecraft.client.User;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.Executor;

/**
 * Author cnlimiter
 * CreateTime 2023/7/3 0:48
 * Name MojangUtils
 * Description
 */

public class MojangUtils {
    private MojangUtils() {}

    /**
     * Logs into Mojang and returns a new Minecraft session.
     *
     * <p>NB: You must manually interrupt the executor thread if the
     * completable future is cancelled!
     *
     * @param username Mojang username/email
     * @param password Mojang password
     * @param executor executor to run the login task on
     * @return completable future for the new Minecraft session
     * @see SessionUtils#setSession(User)  to apply the new session
     */
    public static CompletableFuture<User> login(
            final String username, final String password, final Executor executor
    )
    {
        return CompletableFuture.supplyAsync(() -> {
            Const.LOGGER.info("Logging into Minecraft with Mojang (or legacy) credentials...");
            try {
                // Fetch the Yggdrasil User Authentication provider
                final YggdrasilUserAuthentication yua = SessionUtils.getAuthProvider();

                // Update the credentials and login
                yua.setUsername(username);
                yua.setPassword(password);
                yua.logIn();

                // Pluck all useful session data
                final String name = yua.getSelectedProfile().getName();
                final String uuid = UUIDTypeAdapter.fromUUID(yua.getSelectedProfile().getId());
                final String token = yua.getAuthenticatedToken();
                final User.Type type = User.Type.byName(yua.getUserType().getName());

                // Logout after fetching what is needed
                yua.logOut();

                // Finally, log success and return
                Const.LOGGER.info("Successfully logged into Minecraft via Mojang (or legacy)! ({})", name);
                return new User(name, uuid, token, Optional.empty(), Optional.empty(), type);
            } catch (Exception e) {
                Const.LOGGER.error("Unable to login to Minecraft via Mojang (or legacy)!", e);
                throw new CompletionException(e);
            }
        }, executor);
    }
}
