package com.changgou;

import com.changgou.util.IdWorker;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Bean;
import tk.mybatis.spring.annotation.MapperScan;

import javax.validation.Valid;

@SpringBootApplication
@EnableEurekaClient
@MapperScan(basePackages = {"com.changgou.goods.dao"})
public class GoodsApplication {

    //机器号
    @Value("${workerId}")
    private Long workerId;

    //随机数种子
    @Value("${datacenterId}")
    private Long datacenterId;

    public static void main(String[] args) {
        SpringApplication.run( GoodsApplication.class);
    }


    //初始化分布式Id生成器雪花算法，交给spring管理
    @Bean
    public IdWorker initIdWorker(){
        return new IdWorker(workerId,datacenterId);
    }
}
