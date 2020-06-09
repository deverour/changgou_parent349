package com.changgou.goods.controller;

import com.changgou.goods.pojo.Goods;
import com.changgou.goods.service.GoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/goods")
public class GoodsController {

    @Autowired
    private GoodsService goodsService;

    @GetMapping()
    public List<Goods> findAll(){
        System.out.println("goods/findall");
        List<Goods> goodsList = goodsService.findAll();
        System.out.println("goodsList"+goodsList);
        return goodsList;
    }
}
