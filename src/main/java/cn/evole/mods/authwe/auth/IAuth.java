package cn.evole.mods.authwe.auth;

import cn.evole.mods.authwe.auth.storage.DBApi;
import cn.evole.mods.authwe.auth.storage.PlayerCache;

import java.util.HashMap;

/**
 * Author cnlimiter
 * CreateTime 2023/7/3 2:50
 * Name IAuth
 * Description
 */

public class IAuth {
    public static DBApi DB = null;
    public static final HashMap<String, PlayerCache> playerCacheMap = new HashMap<>();

}
