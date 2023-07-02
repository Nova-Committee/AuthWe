package cn.evole.mods.authwe.auth.storage;

/**
 * Author cnlimiter
 * CreateTime 2023/7/3 2:52
 * Name DBException
 * Description
 */

public class DBException  extends Exception {

    public DBException(String errorMessage, Exception e) {
        super("[AuthWe-Auth] " + errorMessage, e);
    }
}
