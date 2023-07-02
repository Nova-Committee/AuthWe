package cn.evole.mods.authwe.auth.storage;

import cn.evole.mods.authwe.Const;
import cn.evole.mods.authwe.auth.IAuth;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Author cnlimiter
 * CreateTime 2023/7/3 2:53
 * Name PlayerCache
 * Description
 */

public class PlayerCache {
    /**
     * Whether player is authenticated.
     */
    @Expose
    @SerializedName("is_authenticated")
    public boolean isAuthenticated = false;
    /**
     * Hashed password of player.
     */
    @Expose
    public String password = "";
    /**
     * Stores how many times the player has tried to log in.
     * Cleared on every successful login and every time the player is kicked for too many incorrect logins.
     */
    @Expose
    @SerializedName("login_tries")
    public AtomicInteger loginTries = new AtomicInteger();
    /**
     * Stores the last time a player was kicked for too many logins (unix ms).
     */
    @Expose
    @SerializedName("last_kicked")
    public long lastKicked = 0;
    /**
     * Last recorded IP of player.
     */
    @Expose
    @SerializedName("last_ip")
    public String lastIp;
    /**
     * Time until session is valid.
     */
    @Expose
    @SerializedName("valid_until")
    public long validUntil;

    /**
     * Player stats before de-authentication.
     */
    @Expose
    @SerializedName("was_in_portal")
    public boolean wasInPortal = false;

    /**
     * Last recorded position before de-authentication.
     */
    public static class LastLocation {
        public ServerLevel dimension;
        public Vec3 position;
        public float yaw;
        public float pitch;
    }

    public final LastLocation lastLocation = new LastLocation();


    private static final Gson gson = new GsonBuilder()
            .excludeFieldsWithoutExposeAnnotation()
            .create();

    /**
     * Creates an empty cache for player (when player doesn't exist in DB).
     *
     * @param player player to create cache for
     */


    public static PlayerCache fromJson(ServerPlayer player, String fakeUuid) {
        Const.LOGGER.debug(String.format("Creating cache for %s %s", player != null ? player.getGameProfile().getName() : null, fakeUuid));

        String json = IAuth.DB.getUserData(fakeUuid);
        PlayerCache playerCache;
        if (!json.isEmpty()) {
            // Parsing data from DB
            playerCache = gson.fromJson(json, PlayerCache.class);
        } else
            playerCache = new PlayerCache();
        if (player != null) {
            // Setting position cache
            playerCache.lastLocation.dimension = player.serverLevel().getLevel();
            playerCache.lastLocation.position = player.position();
            playerCache.lastLocation.yaw = player.getYRot();
            playerCache.lastLocation.pitch = player.getXRot();

            playerCache.wasInPortal = false;
        }

        return playerCache;
    }

    public String toJson() {
        return gson.toJson(this);
    }

    public static boolean isAuthenticated(String uuid) {
        PlayerCache playerCache = IAuth.playerCacheMap.get(uuid);
        return (playerCache != null && playerCache.isAuthenticated);
    }
}
