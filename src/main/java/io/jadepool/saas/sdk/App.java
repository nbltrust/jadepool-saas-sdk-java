package io.jadepool.saas.sdk;

import java.util.HashMap;

public class App extends API {
    public App(APIContext context) {
        this.context = context;
    }

    public APIResult getAddress(String coinName) throws APIException {
        return new APIRequest(context, "/api/v1/address/" + coinName, "GET", new HashMap<String, String>() {{
            put("X-App-Key", context.getAppKey());
        }}).execute();
    }

    public APIResult createAddress(String coinName) throws APIException {
        return createAddressWithMode(coinName, "");
    }

    public APIResult createAddressWithMode(String coinName, String mode) throws APIException {
        return new APIRequest(context, "/api/v1/address/" + coinName + "/new", "POST", new HashMap<String, String>() {{
            put("X-App-Key", context.getAppKey());
        }}).execute(new HashMap<String, Object>() {{
            put("mode", mode);
        }});
    }

    public APIResult verifyAddress(String coinName, String address) throws APIException {
        return new APIRequest(context, "/api/v1/address/" + coinName + "/verify", "POST", new HashMap<String, String>() {{
            put("X-App-Key", context.getAppKey());
        }}).execute(new HashMap<String, Object>() {{
            put("address", address);
        }});
    }

    public APIResult getBalances() throws APIException {
        return new APIRequest(context, "/api/v1/app/balances", "GET", new HashMap<String, String>() {{
            put("X-App-Key", context.getAppKey());
        }}).execute();
    }

    public APIResult addAppAsset(String coinName) throws APIException {
        return new APIRequest(context, "/api/v1/app/assets", "POST", new HashMap<String, String>() {{
            put("X-App-Key", context.getAppKey());
        }}).execute(new HashMap<String, Object>() {{
            put("coinName", coinName);
        }});
    }

    public APIResult getBalance(String coinName) throws APIException {
        return new APIRequest(context, "/api/v1/app/balance/" + coinName, "GET", new HashMap<String, String>() {{
            put("X-App-Key", context.getAppKey());
        }}).execute();
    }

    public APIResult getAssets() throws APIException {
        return new APIRequest(context, "/api/v1/app/assets", "GET", new HashMap<String, String>() {{
            put("X-App-Key", context.getAppKey());
        }}).execute();
    }

    public APIResult getAllAssets() throws APIException {
        return new APIRequest(context, "/api/v1/app/allAssets", "GET", new HashMap<String, String>() {{
            put("X-App-Key", context.getAppKey());
        }}).execute();
    }

    public APIResult withdraw(String id, String coinName, String to, String value) throws APIException {
        return withdrawWithMemo(id, coinName, to, value, "");
    }

    public APIResult withdrawWithMemo(String id, String coinName, String to, String value, String memo) throws APIException {
        return new APIRequest(context, "/api/v1/app/" + coinName + "/withdraw", "POST", new HashMap<String, String>() {{
            put("X-App-Key", context.getAppKey());
        }}).execute(new HashMap<String, Object>() {{
            put("to", to);
            put("value", value);
            put("memo", memo);
            put("id", id);
        }});
    }

    public APIResult getOrders(int page, int amount) throws APIException {
        return new APIRequest(context, "/api/v1/app/orders", "GET", new HashMap<String, String>() {{
            put("X-App-Key", context.getAppKey());
        }}).execute(new HashMap<String, Object>() {{
            put("page", page);
            put("amount", amount);
        }});
    }

    public APIResult getOrder(String id) throws APIException {
        return new APIRequest(context, "/api/v1/app/order/" + id, "GET", new HashMap<String, String>() {{
            put("X-App-Key", context.getAppKey());
        }}).execute();
    }

    public APIResult getValidators(String coinName) throws APIException {
        return new APIRequest(context, "/api/v1/staking/" + coinName + "/validators", "GET", new HashMap<String, String>() {{
            put("X-App-Key", context.getAppKey());
        }}).execute();
    }

    public APIResult delegate(String id, String coinName, String value) throws APIException {
        return new APIRequest(context, "/api/v1/staking/" + coinName + "/delegate", "POST", new HashMap<String, String>() {{
            put("X-App-Key", context.getAppKey());
        }}).execute(new HashMap<String, Object>() {{
            put("value", value);
            put("id", id);
        }});
    }

    public APIResult unDelegate(String id, String coinName, String value) throws APIException {
        return new APIRequest(context, "/api/v1/staking/" + coinName + "/undelegate", "POST", new HashMap<String, String>() {{
            put("X-App-Key", context.getAppKey());
        }}).execute(new HashMap<String, Object>() {{
            put("value", value);
            put("id", id);
        }});
    }

    public APIResult addUrgentStakingFunding(String id, String coinName, String value, long expiredAt) throws APIException {
        return new APIRequest(context, "/api/v1/staking/" + coinName + "/funding", "POST", new HashMap<String, String>() {{
            put("X-App-Key", context.getAppKey());
        }}).execute(new HashMap<String, Object>() {{
            put("value", value);
            put("id", id);
            put("expiredAt", expiredAt);
        }});
    }

    public APIResult getStakingInterest(String coinName, String date) throws APIException {
        return new APIRequest(context, "/api/v1/staking/" + coinName + "/interest", "GET", new HashMap<String, String>() {{
            put("X-App-Key", context.getAppKey());
        }}).execute(new HashMap<String, Object>() {{
            put("date", date);
        }});
    }

    public APIResult getMarket(String coinName) throws APIException {
        return new APIRequest(context, "/api/v1/market/" + coinName, "GET", new HashMap<String, String>() {{
            put("X-App-Key", context.getAppKey());
        }}).execute();
    }
}
