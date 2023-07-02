package cn.evole.mods.authwe.mixin;

import net.minecraft.client.User;
import net.minecraft.client.resources.SplashManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

/**
 * Author cnlimiter
 * CreateTime 2023/7/3 0:24
 * Name SplashManagerAccessor
 * Description
 */

@Mixin(SplashManager.class)
public interface SplashManagerAccessor
{
    /**
     * Sets the Minecraft session.
     *
     * @param session new Minecraft session
     */
    @Accessor
    @Mutable
    void setUser(User session);
}