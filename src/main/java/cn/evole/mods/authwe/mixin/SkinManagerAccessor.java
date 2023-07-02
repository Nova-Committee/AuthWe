package cn.evole.mods.authwe.mixin;

import net.minecraft.client.resources.SkinManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.io.File;

/**
 * Author cnlimiter
 * CreateTime 2023/7/3 1:04
 * Name SkinManagerAccessor
 * Description
 */

@Mixin(SkinManager.class)
public interface SkinManagerAccessor {
    @Accessor("skinsDirectory")
    File getSkinCacheDir();
}
