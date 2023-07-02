package cn.evole.mods.authwe.api.yggdrasil;

import cn.evole.mods.authwe.Const;
import com.google.common.collect.Iterables;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.authlib.Environment;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import com.mojang.authlib.yggdrasil.YggdrasilMinecraftSessionService;
import com.mojang.authlib.yggdrasil.response.MinecraftTexturesPayload;
import com.mojang.util.UUIDTypeAdapter;
import net.minecraft.util.GsonHelper;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;

import java.net.Proxy;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.*;
import java.util.concurrent.CompletionException;

/**
 * Author cnlimiter
 * CreateTime 2023/7/2 13:27
 * Name YggdrasilAuthentication
 * Description
 */

public class YggdrasilAuthentication extends YggdrasilAuthenticationService {
    public YggdrasilAuthentication(Proxy proxy, String serverUrl) {
        super(proxy, new YggServerSession(serverUrl));
    }

    public static class YggdrasilMinecraftSession extends YggdrasilMinecraftSessionService {
        private static final Logger LOGGER = Const.LOGGER;
        private final PublicKey publicKey;
        private final Gson gson = new GsonBuilder().registerTypeAdapter(UUID.class, new UUIDTypeAdapter()).create();

        public YggdrasilMinecraftSession(YggdrasilAuthenticationService service, String serverUrl) {
            super(service, new YggServerSession(serverUrl));
            JsonObject json = new JsonObject();
            try (CloseableHttpClient client = HttpClients.createMinimal()) {
                final HttpGet request = new HttpGet(URI.create("https://" + serverUrl + "/api/yggdrasil"));
                final org.apache.http.HttpResponse res = client.execute(request);

                // Attempt to parse the response body as JSON and extract the profile
                json = GsonHelper.parse(EntityUtils.toString(res.getEntity()));
            } catch (Exception e) {
                Const.LOGGER.error("Unable to fetch Minecraft profile!", e);
                throw new CompletionException(e);
            }

            this.publicKey = getPublicKey(json.get("signaturePublickey").getAsString());
        }

        private static PublicKey getPublicKey(String key) {
            key = key.replace("-----BEGIN PUBLIC KEY-----", "").replace("-----END PUBLIC KEY-----", "");
            try {
                byte[] bykeKey = Base64.getDecoder().decode(key.replace("\n", ""));
                X509EncodedKeySpec spec = new X509EncodedKeySpec(bykeKey);
                KeyFactory factory = KeyFactory.getInstance("RSA");
                return factory.generatePublic(spec);
            } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
                e.printStackTrace();
            }
            return null;
        }


        @Override
        public Map<MinecraftProfileTexture.Type, MinecraftProfileTexture> getTextures(final GameProfile profile, final boolean requireSecure) {
            final Property textureProperty = Iterables.getFirst(profile.getProperties().get("textures"), null);

            if (textureProperty == null)
                return new HashMap<>();

            if (requireSecure) {
                if (!textureProperty.hasSignature()) {
                    LOGGER.error("Signature is missing from textures payload");
                    throw new InsecureTextureException("Signature is missing from textures payload");
                }
                if (!textureProperty.isSignatureValid(publicKey)) {
                    LOGGER.error("Textures payload has been tampered with (signature invalid)");
                    throw new InsecureTextureException("Textures payload has been tampered with (signature invalid)");
                }
            }

            final MinecraftTexturesPayload result;
            try {
                final String json = new String(org.apache.commons.codec.binary.Base64.decodeBase64(textureProperty.getValue()), StandardCharsets.UTF_8);
                result = gson.fromJson(json, MinecraftTexturesPayload.class);
            } catch (final JsonParseException e) {
                LOGGER.error("Could not decode textures payload", e);
                return new HashMap<>();
            }

            if (result == null || result.getTextures() == null)
                return new HashMap<>();

            return result.getTextures();
        }
    }


    public static class YggServerSession implements Environment {
        private final String url;

        public YggServerSession(String serverUrl) {
            this.url = serverUrl;
        }

        @Override
        public String getAuthHost() {
            return "https://" + url + "/api/yggdrasil/authserver";
        }

        @Override
        public String getAccountsHost() {
            return "https://" + url + "/api/yggdrasil/api";
        }

        @Override
        public String getSessionHost() {
            return "https://" + url + "/api/yggdrasil/sessionserver";
        }

        @Override
        public String getServicesHost() {
            return "https://" + url + "/api/yggdrasil/minecraftservices";
        }

        @Override
        public String getName() {
            return "Auth-lib-Injector";
        }

        @Override
        public String asString() {
            return new StringJoiner(", ", "", "")
                    .add("authHost='" + getAuthHost() + "'")
                    .add("accountsHost='" + getAccountsHost() + "'")
                    .add("sessionHost='" + getSessionHost() + "'")
                    .add("servicesHost='" + getServicesHost() + "'")
                    .add("name='" + getName() + "'")
                    .toString();
        }
    }
}


