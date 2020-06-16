package io.jadepool.saas.sdk;

import com.google.gson.*;
import org.apache.commons.codec.binary.Hex;

import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.lang.reflect.Modifier;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
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

    public static String aesEncrypt(String plainText, String key, byte[] iv) throws Exception {
        byte[] clean = plainText.getBytes();

        int ivSize = 16;
        IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);

        // Hashing key.
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        digest.update(key.getBytes(StandardCharsets.UTF_8));
        byte[] keyBytes = new byte[32];
        System.arraycopy(digest.digest(), 0, keyBytes, 0, keyBytes.length);
        SecretKeySpec secretKeySpec = new SecretKeySpec(keyBytes, "AES");

        // Encrypt.
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivParameterSpec);
        byte[] encrypted = cipher.doFinal(clean);

        return Base64.getEncoder().encodeToString(encrypted);
    }

    public static String aesDecrypt(String encrypted, String key, byte[] iv) throws Exception {
        int keySize = 32;

        IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);

        byte[] encryptedBytes = Base64.getDecoder().decode(encrypted);

        // Hash key.
        byte[] keyBytes = new byte[keySize];
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        md.update(key.getBytes());
        System.arraycopy(md.digest(), 0, keyBytes, 0, keyBytes.length);
        SecretKeySpec secretKeySpec = new SecretKeySpec(keyBytes, "AES");

        // Decrypt.
        Cipher cipherDecrypt = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipherDecrypt.init(Cipher.DECRYPT_MODE, secretKeySpec, ivParameterSpec);
        byte[] decrypted = cipherDecrypt.doFinal(encryptedBytes);

        return new String(decrypted);
    }
}
