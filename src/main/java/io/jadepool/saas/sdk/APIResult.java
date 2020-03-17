package io.jadepool.saas.sdk;

import com.google.gson.JsonElement;
import com.google.gson.Gson;

public class APIResult {
    private int code;
    private String message;
    private String sign;
    private JsonElement data;

    protected APIContext context;

    public static APIResult parse(APIContext context, String json) {
        try {
            Gson gson = new Gson();
            APIResult ret = gson.fromJson(json, APIResult.class);
            ret.context = context;
            return ret;
        } catch (Exception e) {
            return null;
        }
    }

    public int getCode() {
        return code;
    }

    public boolean checkSign() {
        context.log(Utils.serializeJSON(data));
        context.log(Utils.hmacSHA256(Utils.serializeJSON(data), context.getAppSecret()));
        return sign.equals(Utils.hmacSHA256(Utils.serializeJSON(data), context.getAppSecret()));
    }

    public String getMessage() {
        return message;
    }

    public JsonElement getData() {
        return data;
    }
}
