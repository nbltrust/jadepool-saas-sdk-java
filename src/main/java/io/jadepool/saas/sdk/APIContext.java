package io.jadepool.saas.sdk;

import java.io.PrintStream;
import java.util.concurrent.ThreadLocalRandom;

public class APIContext {
    public static final String DEFAULT_API_BASE = APIConfig.DEFAULT_API_BASE;
    private String endpointBase;
    private String appKey;
    private String appSecret;
    private int nonceCount;
    protected boolean isDebug = false;
    protected PrintStream logger = System.out;

    public APIContext(String endpointBase, String appKey, String appSecret) {
        this.endpointBase = endpointBase;
        this.appKey = appKey;
        this.appSecret = appSecret;
    }

    public APIContext(String appKey, String appSecret) {
        this(DEFAULT_API_BASE, appKey, appSecret);
    }

    public String getEndpointBase() {
        return endpointBase;
    }

    public String getAppKey() {
        return appKey;
    }

    public String getAppSecret() {
        return appSecret;
    }

    public String genNonce(long timestamp) {
        return String.format("%d%d%d", ++nonceCount, timestamp, ThreadLocalRandom.current().nextInt());
    }

    public boolean isDebug() {
        return this.isDebug;
    }

    public APIContext enableDebug(boolean isDebug) {
        this.isDebug = isDebug;
        return this;
    }

    public PrintStream getLogger() {
        return this.logger;
    }

    public APIContext setLogger(PrintStream logger) {
        this.logger = logger;
        return this;
    }

    public void log(String s) {
        if (isDebug && logger != null) logger.println(s);
    }
}
