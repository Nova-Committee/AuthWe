package cn.evole.mods.authwe.mixin;

import net.minecraft.client.multiplayer.chat.report.ReportEnvironment;
import net.minecraft.client.multiplayer.chat.report.ReportingContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/**
 * Author cnlimiter
 * CreateTime 2023/7/3 0:23
 * Name AbuseReportContextAccessor
 * Description
 */

@Mixin(ReportingContext.class)
public interface ReportingContextAccessor
{
    /**
     * Returns the reporter environment.
     *
     * @return environment
     */
    @Accessor
    ReportEnvironment getEnvironment();
}
