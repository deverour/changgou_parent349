package com.changgou.search.listener;

import com.changgou.search.service.EsManagerService;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@RabbitListener(queues = "search_reduce_queue")
public class GoodsDownListener {
    @Autowired
    private EsManagerService esManagerService;
    @RabbitHandler
    public void messageHandler(String spuId){
        System.out.println("====需要下架，从索引库移除的商品id为：====="+spuId);
        esManagerService.reduceFromES(spuId);
    }
}
