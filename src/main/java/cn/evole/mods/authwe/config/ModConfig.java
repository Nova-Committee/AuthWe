package cn.evole.mods.authwe.config;

import cn.evole.mods.authwe.api.MicrosoftUtils;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;

/**
 * Description:
 * Author: cnlimiter
 * Date: 2022/5/17 15:04
 * Version: 1.0
 */
public class ModConfig {

    public static final ForgeConfigSpec CLIENT;

    public static final ForgeConfigSpec.EnumValue<MicrosoftUtils.MicrosoftPrompt> prompt;
    public static final ForgeConfigSpec.IntValue port;
    public static final ForgeConfigSpec.ConfigValue<String> clientId;
    public static final ForgeConfigSpec.ConfigValue<String> authorizeUrl;
    public static final ForgeConfigSpec.ConfigValue<String> tokenUrl;
    public static final ForgeConfigSpec.ConfigValue<String> xboxAuthUrl;
    public static final ForgeConfigSpec.ConfigValue<String> xboxXstsUrl;
    public static final ForgeConfigSpec.ConfigValue<String> mcAuthUrl;
    public static final ForgeConfigSpec.ConfigValue<String> mcProfileUrl;
    public static final ForgeConfigSpec.ConfigValue<String> mojangLastUsername;
    public static final ForgeConfigSpec.ConfigValue<String> offlineLastUsername;

    //SERVER
    static {
        final var common = new ForgeConfigSpec.Builder();
        common.comment("Login via Microsoft").push("Microsoft");
        prompt= buildMicrosoftPrompt(common, "prompt", MicrosoftUtils.MicrosoftPrompt.DEFAULT, "Indicates the type of user interaction that is required");
        port = buildInt(common, "port", 25585, 0, Integer.MAX_VALUE, "The port from which to listen for OAuth2 callbacks");
        clientId = buildString(common, "clientId", MicrosoftUtils.CLIENT_ID, "OAuth2 client id");
        authorizeUrl= buildString(common, "authorizeUrl", MicrosoftUtils.AUTHORIZE_URL, "OAuth2 authorization url");
        tokenUrl= buildString(common, "tokenUrl", MicrosoftUtils.TOKEN_URL, "OAuth2 access token url");
        xboxAuthUrl= buildString(common, "xboxAuthUrl", MicrosoftUtils.XBOX_AUTH_URL, "Xbox authentication url");
        xboxXstsUrl= buildString(common, "xboxXstsUrl", MicrosoftUtils.XBOX_XSTS_URL, "Xbox XSTS authorization url");
        mcAuthUrl= buildString(common, "mcAuthUrl", MicrosoftUtils.MC_AUTH_URL, "Minecraft authentication url");
        mcProfileUrl= buildString(common, "mcProfileUrl", MicrosoftUtils.MC_PROFILE_URL, "Minecraft profile url");
        common.pop();
        common.comment("Login via Mojang (or legacy)").push("Mojang");
        mojangLastUsername = buildString(common, "mojangLastUsername", "", "Last used username");
        common.pop();
        common.comment("Login Offline").push("Offline");
        offlineLastUsername = buildString(common, "offlineLastUsername", "", "Last used username");
        common.pop();

        CLIENT = common.build();
    }

    public static void register() {
        ModLoadingContext.get().registerConfig(net.minecraftforge.fml.config.ModConfig.Type.CLIENT, CLIENT);
    }


    private static ForgeConfigSpec.BooleanValue buildBoolean(ForgeConfigSpec.Builder builder, String name, boolean defaultValue, String comment) {
        return builder.comment(comment).translation(name).define(name, defaultValue);
    }

    private static ForgeConfigSpec.IntValue buildInt(ForgeConfigSpec.Builder builder, String name, int defaultValue, int min, int max, String comment) {
        return builder.comment(comment).translation(name).defineInRange(name, defaultValue, min, max);
    }

    private static ForgeConfigSpec.DoubleValue buildDouble(ForgeConfigSpec.Builder builder, String name, double defaultValue, double min, double max, String comment) {
        return builder.comment(comment).translation(name).defineInRange(name, defaultValue, min, max);
    }
    private static ForgeConfigSpec.EnumValue<MicrosoftUtils.MicrosoftPrompt> buildMicrosoftPrompt(ForgeConfigSpec.Builder builder, String name, MicrosoftUtils.MicrosoftPrompt defaultValue, String comment) {
        return builder.comment(comment).translation(name).defineEnum(name, defaultValue);
    }

    private static ForgeConfigSpec.ConfigValue<String> buildString(ForgeConfigSpec.Builder builder, String name, String defaultValue, String comment) {
        return builder.comment(comment).translation(name).define(name, defaultValue);
    }
}
