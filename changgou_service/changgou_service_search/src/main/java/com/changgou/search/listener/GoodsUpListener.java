package com.changgou.search.listener;

import com.changgou.search.service.EsManagerService;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;

@Component
@RabbitListener(queues = "search_add_queue")
public class GoodsUpListener {
    @Autowired
    private EsManagerService esManagerService;
    @RabbitHandler
    public void messageHandler(String spuId){
        System.out.println("====需要上架，导入数据到索引库的商品id为：====="+spuId);
        esManagerService.importDataToEs(spuId);
    }
}
