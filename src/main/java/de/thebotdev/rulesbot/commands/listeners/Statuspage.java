package de.thebotdev.rulesbot.commands.listeners;

import de.thebotdev.rulesbot.util.RegisterListener;
import net.dv8tion.jda.core.events.ReadyEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import okhttp3.*;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import static de.thebotdev.rulesbot.Main.config;
import static de.thebotdev.rulesbot.Main.shardManager;
import static org.slf4j.LoggerFactory.getLogger;

@RegisterListener
public class Statuspage extends ListenerAdapter {
    OkHttpClient client = new OkHttpClient.Builder()
            .build();

    @Override
    public void onReady(ReadyEvent event) {
        new Thread(() -> {
            while (true) {
                try {
                    TimeUnit.MINUTES.sleep(1L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                RequestBody body = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("data[value]", String.valueOf(shardManager.getAveragePing()))
                        .addFormDataPart("data[timestamp]", getTimeStamp())
                        .build();
                Request request = new Request.Builder()
                        .url(String.format("https://api.statuspage.io/v1/pages/%s/metrics/%s/data.json", config.getStatuspage().getPage_id(), config.getStatuspage().getMetric_id()))
                        .addHeader("Authorization", config.getStatuspage().getApi_key())
                        .post(body)
                        .build();
                try (Response response = client.newCall(request).execute()) {
                    assert response.body() != null;
                    if (response.code() != 201) {
                        getLogger(String.format("[StatusPage] Got an unexpected response %s", response.body().string()));
                    }
                    getLogger(String.format("[StatusPage] Posted ping to StatusPage! Submitted ping: %s", new JSONObject(response.body().string()).getJSONObject("data").getFloat("value")));
                } catch (IOException e) {
                    getLogger(String.format("[StatusPage] Error while posting ping!\n%s", e));
                }
            }
        }).start();
    }

    private String getTimeStamp() {
        return String.valueOf(System.currentTimeMillis() / 1000L);
    }
}
