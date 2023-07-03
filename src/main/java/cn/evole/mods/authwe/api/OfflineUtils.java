package cn.evole.mods.authwe.api;

import cn.evole.mods.authwe.Const;
import cn.evole.mods.authwe.api.yggdrasil.YggdrasilAuthentication;
import cn.evole.mods.authwe.mixin.MinecraftAccessor;
import cn.evole.mods.authwe.mixin.SkinManagerAccessor;
import com.mojang.authlib.exceptions.AuthenticationException;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.minecraft.UserApiService;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import net.minecraft.client.Minecraft;
import net.minecraft.client.User;
import net.minecraft.client.gui.screens.social.PlayerSocialManager;
import net.minecraft.client.resources.SkinManager;

import java.io.File;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.Executor;

/**
 * Author cnlimiter
 * CreateTime 2023/7/3 0:48
 * Name YggdrasilUtils
 * Description
 */

public class OfflineUtils {
    private OfflineUtils() {}

    /**
     * Logs into Offline and returns a new Minecraft session.
     *
     * <p>NB: You must manually interrupt the executor thread if the
     * completable future is cancelled!
     *
     * @param username Offline username/email
     * @param uuid Offline uuid
     * @param mcToken Offline token
     * @param executor executor to run the login task on
     * @return completable future for the new Minecraft session
     * @see SessionUtils#setSession(User)  to apply the new session
     */
    public static CompletableFuture<User> login(
              final String username, final String uuid,
             final String mcToken,
             final Executor executor
    )
    {
        return CompletableFuture.supplyAsync(() -> {
            Const.LOGGER.info("Logging into Minecraft with Offline credentials...");
            try {
                // Fetch the Offline User Authentication provider
                User session = new User(username, uuid, mcToken, Optional.empty(), Optional.empty(), User.Type.MOJANG);
                // Finally, log success and return
                Const.LOGGER.info("Successfully logged into Minecraft via Yggdrasil! ({})", username);
                return session;
            } catch (Exception e) {
                Const.LOGGER.error("Unable to login to Minecraft via Yggdrasil!", e);
                throw new CompletionException(e);
            }
        }, executor);
    }

}
