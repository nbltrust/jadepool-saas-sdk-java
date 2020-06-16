package io.jadepool.saas.sdk;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.HashMap;

public class Company extends API {
    public Company(APIContext context) {
        this.context = context;
    }

    public APIResult getFundingWallets() throws APIException {
        return new APIRequest(context, "/api/v1/funding/balances", "GET", new HashMap<String, String>() {{
            put("X-Company-Key", context.getAppKey());
        }}).execute();
    }

    public APIResult fundingTransfer(String from, String to, String coinType, String value) throws APIException {
        return fundingTransferWithMemo(from, to, coinType, value, "");
    }

    public APIResult fundingTransferWithMemo(String from, String to, String coinType, String value, String memo) throws APIException {
        return new APIRequest(context, "/api/v1/funding/transfer", "POST", new HashMap<String, String>() {{
            put("X-Company-Key", context.getAppKey());
        }}).execute(new HashMap<String, Object>() {{
            put("from", from);
            put("to", to);
            put("value", value);
            put("assetName", coinType);
            put("memo", memo);
        }});
    }

    public APIResult getFundingRecords(int page, int amount) throws APIException {
        return filterFundingRecords(page, amount, "DESC", "", "", "", "", "created_at");
    }

    public APIResult filterFundingRecords(int page, int amount, String sort, String coins, String froms, String toes, String coinType, String orderBy) throws APIException {
        return new APIRequest(context, "/api/v1/funding/records", "GET", new HashMap<String, String>() {{
            put("X-Company-Key", context.getAppKey());
        }}).execute(new HashMap<String, Object>() {{
            put("page", page);
            put("amount", amount);
            put("sort", sort);
            put("coins", coins);
            put("froms", froms);
            put("toes", toes);
            put("type", coinType);
            put("orderBy", orderBy);
        }});
    }

    public APIResult createWallet(String name, String password, String webHook, String description) throws Exception {
        byte[] iv = new byte[16];
        SecureRandom random = new SecureRandom();
        random.nextBytes(iv);

        String encryptedPassword = Utils.aesEncrypt(password, context.getAppSecret(), iv);

        APIResult ret = new APIRequest(context, "/api/v1/app", "POST", new HashMap<String, String>() {{
            put("X-Company-Key", context.getAppKey());
        }}).execute(new HashMap<String, Object>() {{
            put("name", name);
            put("password", encryptedPassword);
            put("description", description);
            put("webHook", webHook);
            put("aesIV", Base64.getEncoder().encodeToString(iv));
        }});
        if (ret.getCode() != 0 || !ret.checkSign()) {
            return ret;
        }
        JsonElement data = ret.getData();
        JsonObject result = data.getAsJsonObject();
        String encryptedAppSecret = result.get("encryptedAppSecret").getAsString();
        String appSecret = Utils.aesDecrypt(encryptedAppSecret, context.getAppSecret(), iv);
        result.addProperty("appSecret", appSecret);
        ret.setData(result);
        ret.resetSign();
        return ret;
    }

    public APIResult getWalletKeys(String appID) throws Exception {
        byte[] iv = new byte[16];
        SecureRandom random = new SecureRandom();
        random.nextBytes(iv);

        APIResult ret = new APIRequest(context, "/api/v1/app/" + appID + "/keys", "GET", new HashMap<String, String>() {{
            put("X-Company-Key", context.getAppKey());
        }}).execute(new HashMap<String, Object>() {{
            put("aesIV", Base64.getEncoder().encodeToString(iv));
        }});
        if (ret.getCode() != 0 || !ret.checkSign()) {
            return ret;
        }
        JsonElement data = ret.getData();
        JsonObject result = data.getAsJsonObject();
        JsonArray keys = result.get("keys").getAsJsonArray();
        for (JsonElement k : keys) {
            JsonObject ko = k.getAsJsonObject();
            String encryptedAppSecret = ko.get("encryptedAppSecret").getAsString();
            String appSecret = Utils.aesDecrypt(encryptedAppSecret, context.getAppSecret(), iv);
            ko.addProperty("appSecret", appSecret);
        }
        ret.setData(result);
        ret.resetSign();
        return ret;
    }

    public APIResult updateWalletKey(String appKey, boolean enable) throws APIException {
        return new APIRequest(context, "/api/v1/appKey/" + appKey, "PUT", new HashMap<String, String>() {{
            put("X-Company-Key", context.getAppKey());
        }}).execute(new HashMap<String, Object>() {{
            put("enable", enable);
        }});
    }
}

