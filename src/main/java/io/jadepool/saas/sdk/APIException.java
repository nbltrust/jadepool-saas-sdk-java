package io.jadepool.saas.sdk;

public class APIException extends Exception {
    private String header;

    public APIException () {
        super();
    }

    public APIException (Throwable e) {
        super(e);
    }

    public APIException (String message) {
        super(message);
    }

    public APIException (String message, Throwable e) {
        super(message, e);
    }

    public APIException (String header, String message, Throwable e) {
        super(message, e);
        this.header = header;
    }

    public String getHeader() {
        return this.header;
    }

    public static class FailedRequestException extends APIException {
        public FailedRequestException () {
            super();
        }

        public FailedRequestException (Throwable e) {
            super(e);
        }

        public FailedRequestException (String message) {
            super(message);
        }

        public FailedRequestException (String message, Throwable e) {
            super(message, e);
        }

        public FailedRequestException (String header, String message, Throwable e) {
            super(header, message, e);
        }
    }
}
