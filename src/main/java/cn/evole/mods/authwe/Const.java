package cn.evole.mods.authwe;

//#if MC >= 11700
import org.apache.http.client.config.RequestConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
//#else
//$$ import org.apache.logging.log4j.Logger;
//$$ import org.apache.logging.log4j.LogManager;
//#endif

/**
 * Description:
 * Author: cnlimiter
 * Date: 2022/10/1 16:58
 * Version: 1.0
 */
public class Const {
    public static final String MODID = "authwe";
    //#if MC >= 11700
    public static final Logger LOGGER = LoggerFactory.getLogger("AuthWe");
    //#else
    //$$ public static final Logger LOGGER = LogManager.getLogger("McBot");
    //#endif
    public static boolean isShutdown = false;

    public static final RequestConfig REQUEST_CONFIG = RequestConfig
            .custom()
            .setConnectionRequestTimeout(30_000)
            .setConnectTimeout(30_000)
            .setSocketTimeout(30_000)
            .build();

}
