package com.changgou.business.listener;

import okhttp3.*;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RabbitListener(queues = "ad_update_queue")
public class AdListener {

    @RabbitHandler
    public void messageHandler(String position){
        String url = "http://192.168.200.128/ad_update?position="+position;
        OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request.Builder().url(url).build();
        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                System.out.println("===发送失败===");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                System.out.println("===发送成功2==="+response.message());
            }
        });

    }
}
