package com.changgou.page.listener;

import com.changgou.page.service.PageService;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 监听生成静态页面队列，接受到消息商品id
 * 可以根据商品id，查询商品的详细数据，然后根据数据加模块生成静态化页面
 * 通过IO流写入到这个服务器的硬盘保存（上线阶段将生成后的页面通过IO流写入到Nginx负载均衡器中，
 * 因为Nginx性能比Tomcat更高，并且生成后端静态页面Nginx作为http服务器可以运行）
 */

@Component
@RabbitListener(queues = "page_create_queue")
public class PageListener {

    @Autowired
    private PageService pageService;

    /**
     * 接受队列中的商品Id，进行生成静态化商品详情页面
     * @param spuId
     */
    @RabbitHandler
    public void messageHandler(String spuId){
        System.out.println("======生成商品详情页面id为：======" +spuId);
        //1.获取商品详情页面所需要的所有数据
        Map<String, Object> dataMap = pageService.findItemData(spuId);
        //2.根据数据生成静态化页面
        pageService.createItemPage(dataMap,spuId);
    }

}
