package io.jadepool.saas.sdk;

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
}

