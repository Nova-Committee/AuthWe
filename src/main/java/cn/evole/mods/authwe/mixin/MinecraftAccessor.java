package cn.evole.mods.authwe.mixin;

import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.minecraft.UserApiService;
import com.mojang.realmsclient.gui.RealmsDataFetcher;
import net.minecraft.client.Minecraft;
import net.minecraft.client.User;
import net.minecraft.client.gui.screens.social.PlayerSocialManager;
import net.minecraft.client.multiplayer.ProfileKeyPairManager;
import net.minecraft.client.multiplayer.chat.report.ReportingContext;
import net.minecraft.client.resources.SkinManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.net.Proxy;

/**
 * Author cnlimiter
 * CreateTime 2023/7/3 0:14
 * Name MinecraftAccessor
 * Description
 */

@Mixin(Minecraft.class)
public interface MinecraftAccessor {
    /**
     * Sets the Minecraft session.
     *
     * @param session new Minecraft session
     */
    @Accessor
    @Mutable
    void setUser(User session);

    /**
     * Sets the Minecraft user API service.
     *
     * @param userApiService new Minecraft user API service
     */
    @Accessor
    @Mutable
    void setUserApiService(UserApiService userApiService);

    /**
     * Sets the Minecraft social interactions manager.
     *
     * @param socialInteractionsManager new Minecraft social interactions manager
     */
    @Accessor
    @Mutable
    void setPlayerSocialManager(PlayerSocialManager socialInteractionsManager);

    /**
     * Sets the Minecraft profile keys.
     *
     * @param profileKeys new Minecraft profile keys
     */
    @Accessor
    @Mutable
    void setProfileKeyPairManager(ProfileKeyPairManager profileKeys);

    /**
     * Sets the Minecraft abuse report context.
     *
     * @param abuseReportContext new Minecraft abuse report context
     */
    @Accessor
    @Mutable
    void setReportingContext(ReportingContext abuseReportContext);

    /**
     * Sets the Minecraft Realms periodic checkers.
     *
     * @param realmsPeriodicCheckers new Minecraft Realms periodic checkers
     */
    @Accessor
    @Mutable
    void setRealmsDataFetcher(RealmsDataFetcher realmsPeriodicCheckers);


    @Accessor("proxy")
    Proxy getProxy();


    @Mutable
    @Accessor("minecraftSessionService")
    void setServices(MinecraftSessionService service);


    @Mutable
    @Accessor("skinManager")
    void setSkinManager(SkinManager skinProvider);


}
