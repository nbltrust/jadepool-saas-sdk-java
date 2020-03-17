package examples;

import io.jadepool.saas.sdk.*;

public class ExampleApp {
    public static void main(String[] args) {
        String endpoint = "https://openapi.jadepool.io";
        String appKey = "";
        String appSecret = "";
        APIContext appContext = new APIContext(endpoint, appKey, appSecret);
        App appTest = new App(appContext);

        // get a new address
        try {
            APIResult result = appTest.createAddress("ETH");
            if (checkResult(result)) {
                return;
            }

            System.out.println("response data:");
            System.out.println(result.getData());
        } catch (APIException e) {
            e.printStackTrace();
            return;
        }

        // withdraw
        try {
            String id = String.format("%d", System.currentTimeMillis() / 1000);
            APIResult result = appTest.withdraw(id, "ETH", "0xF0706B7Cab38EA42538f4D8C279B6F57ad1d4072", "0.05");
            if (checkResult(result)) {
                return;
            }

            System.out.println("response data:");
            System.out.println(result.getData());
        } catch (APIException e) {
            e.printStackTrace();
        }
    }

    private static boolean checkResult(APIResult result) {
        if (result.getCode() != 0) {
            System.out.println(result.getMessage());
            return true;
        }
        if (!result.checkSign()) {
            System.out.println("WARNING: check sign failed!");
        }
        return false;
    }
}
