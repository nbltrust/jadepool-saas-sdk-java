package io.jadepool.saas.sdk;

import com.google.gson.*;
import org.apache.commons.codec.binary.Hex;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.lang.reflect.Modifier;
import java.util.*;

public class Utils {
    public static String serializeJSON(JsonElement data) {
        Map<String, Object> allParams = new HashMap<>();
        if (data.isJsonArray()) {
            JsonArray arr = data.getAsJsonArray();
            int i = 0;
            for (JsonElement item : arr) {
                allParams.put(String.valueOf(i), serializeJSON(item));
                ++i;
            }
        } else if (data.isJsonObject()) {
            JsonObject obj = data.getAsJsonObject();
            for (Map.Entry<String, JsonElement> entry : obj.entrySet()) {
                if (!entry.getValue().isJsonNull()) {
                    allParams.put(entry.getKey(), serializeJSON(entry.getValue()));
                }
            }
        } else if (data.isJsonNull()) {
            return "";
        } else if (data.isJsonPrimitive()) {
            JsonPrimitive p = data.getAsJsonPrimitive();
            if (p.isString()) {
                return data.getAsString();
            } else {
                return data.toString();
            }
        } else {
            return data.toString();
        }

        return serialize(allParams);
    }

    public static String serialize(Map<String, Object> allParams) {
        TreeMap<String, Object> sorted = new TreeMap<>(allParams);
        StringBuilder urlString = new StringBuilder();
        boolean firstEntry = true;
        for (Map.Entry<String, Object> entry : sorted.entrySet()) {
            urlString.append(firstEntry ? "" : "&").append(entry.getKey()).append("=").append(convertToString(entry.getValue()));
            firstEntry = false;
        }

        return urlString.toString();
    }

    public static String hmacSHA256(String input, String key) {
        Mac sha256_HMAC;
        try {
            sha256_HMAC = Mac.getInstance("HmacSHA256");
            sha256_HMAC.init(new SecretKeySpec(key.getBytes(), "HmacSHA256"));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return new String(Hex.encodeHex(sha256_HMAC.doFinal(input.getBytes())));
    }

    public static String convertToString(Object input) {
        if (input == null) {
            return "null";
        } else if (input instanceof Map) {
            Gson gson = new GsonBuilder()
                    .excludeFieldsWithModifiers(Modifier.STATIC)
                    .excludeFieldsWithModifiers(Modifier.PROTECTED)
                    .disableHtmlEscaping()
                    .create();
            return gson.toJson(input);
        } else if (input instanceof List) {
            Gson gson = new GsonBuilder()
                    .excludeFieldsWithModifiers(Modifier.STATIC)
                    .excludeFieldsWithModifiers(Modifier.PROTECTED)
                    .disableHtmlEscaping()
                    .create();
            return gson.toJson(input);
        } else {
            return input.toString();
        }
    }
}
