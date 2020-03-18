package io.jadepool.saas.sdk;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.net.URL;


public class APIRequest {
    public static final String USER_AGENT = APIConfig.USER_AGENT;

    protected APIContext context;
    protected String endpoint;
    protected String method;
    protected HashMap<String, String> headers;

    public APIRequest(APIContext context, String endpoint, String method) {
        this(context, endpoint, method, null);
    }

    public APIRequest(APIContext context, String endpoint, String method, HashMap<String, String> headers) {
        this.context = context;
        this.endpoint = endpoint;
        this.method = method;
        this.headers = headers;
    }

    public APIResult execute() throws APIException {
        return execute(new HashMap<>());
    }

    public APIResult execute(Map<String, Object> params) throws APIException {
        String apiUrl = getApiUrl();
        ResponseWrapper response;
        try {
            context.log("========Start of API Call========");
            if ("GET".equals(method)) response = sendGet(apiUrl, params, context);
            else if ("POST".equals(method)) response = sendPost(apiUrl, params, context);
            else if ("DELETE".equals(method)) response = sendDelete(apiUrl, params, context);
            else throw new IllegalArgumentException("Unsupported http method. Currently only GET, POST, and DELETE are supported");
            context.log("Response:");
            context.log(response.getBody());
            context.log("========End of API Call========");
        } catch(IOException e) {
            throw new APIException.FailedRequestException(e);
        }
        return APIResult.parse(context, response.getBody());
    }

    public ResponseWrapper sendGet(String apiUrl, Map<String, Object> allParams, APIContext context) throws APIException, IOException {
        allParams = prepareParams(allParams);
        URL url = new URL(constructUrlString(apiUrl, allParams));
        context.log("Request:");
        context.log("GET: " + url.toString());
        HttpURLConnection con = (HttpURLConnection) url.openConnection();

        con.setRequestMethod("GET");
        con.setRequestProperty("User-Agent", USER_AGENT);
        con.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            con.setRequestProperty(entry.getKey(), entry.getValue());
        }

        return readResponse(con);
    }

    public ResponseWrapper sendPost(String apiUrl, Map<String, Object> allParams, APIContext context) throws APIException, IOException {
        allParams = prepareParams(allParams);

        URL url = new URL(apiUrl);
        context.log("Post: " + url.toString());
        HttpURLConnection con = (HttpURLConnection) url.openConnection();

        con.setRequestMethod("POST");
        con.setRequestProperty("User-Agent", USER_AGENT);
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            con.setRequestProperty(entry.getKey(), entry.getValue());
        }

        con.setRequestProperty("Content-Type","application/json");
        con.setDoOutput(true);


        Map<String, Object> myMap = new HashMap<String, Object>();
        for (Map.Entry<String, Object> entry : allParams.entrySet()) {
            myMap.put(entry.getKey(), entry.getValue());
        }

        Gson gson = new GsonBuilder().create();
        String content = gson.toJson(myMap);

        con.setRequestProperty("Content-Length", "" + content.length());

        DataOutputStream wr = new DataOutputStream(con.getOutputStream());
        writeStringInUTF8Bytes(wr, content + "\r\n");

        wr.flush();
        wr.close();

        return readResponse(con);
    }

    public ResponseWrapper sendDelete(String apiUrl, Map<String, Object> allParams, APIContext context) throws APIException, IOException {
        allParams = prepareParams(allParams);

        URL url = new URL(constructUrlString(apiUrl, allParams));
        context.log("Delete: " + url.toString());
        HttpURLConnection con = (HttpURLConnection) url.openConnection();

        con.setRequestMethod("DELETE");
        con.setRequestProperty("User-Agent", USER_AGENT);
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            con.setRequestProperty(entry.getKey(), entry.getValue());
        }

        return readResponse(con);
    }

    private static void writeStringInUTF8Bytes(DataOutputStream wr, String input) throws IOException {
        wr.write(input.getBytes(StandardCharsets.UTF_8));
    }

    private String getApiUrl() {
        String endpointBas = context.getEndpointBase();
        return endpointBas + endpoint;
    }

    private Map<String, Object> prepareParams(Map<String, Object> allParams) {
        long timestamp = System.currentTimeMillis() / 1000;
        allParams.put("timestamp", timestamp);
        allParams.put("nonce", context.genNonce(timestamp));
        return sign(allParams);
    }

    private Map<String, Object> sign(Map<String, Object> allParams) {
        allParams.put("sign", Utils.hmacSHA256(Utils.serialize(allParams), context.getAppSecret()));
        return allParams;
    }

    public static String constructUrlString(String apiUrl, Map<String, Object> allParams) throws IOException {
        StringBuilder urlString = new StringBuilder(apiUrl);
        boolean firstEntry = true;
        for (Map.Entry<String, Object> entry : allParams.entrySet()) {
            urlString.append(firstEntry ? "?" : "&").append(URLEncoder.encode(entry.getKey(), StandardCharsets.UTF_8)).append("=").append(URLEncoder.encode(Utils.convertToString(entry.getValue()), StandardCharsets.UTF_8));
            firstEntry = false;
        }
        return urlString.toString();
    }

    private static ResponseWrapper readResponse(HttpURLConnection con) throws APIException {
        try {
            String header = Utils.convertToString(con.getHeaderFields());
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            return new ResponseWrapper(response.toString(), header);
        } catch(Exception e) {
            throw new APIException.FailedRequestException(
                    Utils.convertToString(con.getHeaderFields()), "", e
            );
        }
    }

    public static class ResponseWrapper {
        private String body;
        private String header;

        public ResponseWrapper(String body, String header) {
            this.body = body;
            this.header = header;
        }

        public String getBody() {
            return this.body;
        }

        public String getHeader() {
            return this.header;
        }
    }
}
