package cn.evole.mods.authwe.mixin;

import com.mojang.realmsclient.RealmsMainScreen;
import net.minecraft.client.gui.screens.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

/**
 * Author cnlimiter
 * CreateTime 2023/7/3 0:23
 * Name RealmsMainScreenAccessor
 * Description
 */

@Mixin(RealmsMainScreen.class)
public interface RealmsMainScreenAccessor
{
    /**
     * Sets the 'checked client compatibility' flag.
     *
     * @param checked true if checked
     */
    @Accessor
    @Mutable
    static void setCheckedClientCompatibility(boolean checked) {}

    /**
     * Sets the 'Realms Generic Error' screen.
     *
     * @param screen error screen
     */
    @Accessor
    @Mutable
    static void setRealmsGenericErrorScreen(Screen screen) {}
}
