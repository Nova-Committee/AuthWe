package cn.evole.mods.authwe.mixin;

import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

/**
 * Author cnlimiter
 * CreateTime 2023/7/3 0:20
 * Name YggdrasilAuthenticationServiceAccessor
 * Description
 */

@Mixin(YggdrasilAuthenticationService.class)
public interface YggdrasilAuthenticationServiceAccessor {
    /**
     * Returns the client token.
     *
     * @return client token
     */
    @Accessor(remap = false)
    @Nullable String getClientToken();

    /**
     * Sets the client token.
     *
     * @param clientToken new client token
     */
    @Accessor(remap = false)
    @Mutable
    void setClientToken(String clientToken);
}
