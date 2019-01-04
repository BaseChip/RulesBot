package de.thebotdev.rulesbot.util.apis;

import okhttp3.*;

import java.io.IOException;
import java.util.Objects;

import static de.thebotdev.rulesbot.Main.gson;

public class Hastebin {
    private static OkHttpClient client = new OkHttpClient.Builder()
            .build();

    public static String publish(String text) throws IOException {
        Call call = client.newCall(getPublishRequest(text));
        Response response = call.execute();
        return gson.fromJson(Objects.requireNonNull(response.body()).string(), HastebinResponse.class).getKey();
    }

    private static Request getPublishRequest(String text) {
        Request request = new Request.Builder()
                .post(RequestBody.create(MediaType.get("text/plain"), text))
                .url("https://hastebin.com/documents")
                .build();
        return request;
    }

    public static class HastebinResponse {
        private String key;

        public String getKey() {
            return key;
        }
    }


}
