package com.changgou.search.service;

import com.alibaba.fastjson.JSON;
import com.changgou.goods.feign.SkuFeign;
import com.changgou.goods.pojo.Sku;
import com.changgou.search.dao.ESDao;
import com.changgou.search.pojo.SkuInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @author ZJ
 */
@Service
public class EsManagerServiceImpl implements EsManagerService {

    @Autowired
    private ESDao esDao;

    @Autowired
    private ElasticsearchTemplate esTemplate;

    @Autowired
    private SkuFeign skuFeign;



    @Override
    public void createIndexAndMapping() {
        //1. 创建索引库
        esTemplate.createIndex("skuinfo");
        //2. 创建mapping也就是索引库内部的结构, 都有哪些属性, 都是哪些类型等
        esTemplate.putMapping(SkuInfo.class);
    }

    @Override
    public void importDataToEs(String spuId) {
        List<Sku> skuList = skuFeign.findListBySpuId(spuId);
        if (null ==skuList){
            throw new RuntimeException("此商品对应的库存数据为空，无数据导入索引库："+spuId);
        }
        String skuJsonstr = JSON.toJSONString(skuList);
        List<SkuInfo> skuInfoList = JSON.parseArray(skuJsonstr, SkuInfo.class);
        for (SkuInfo skuInfo : skuInfoList) {
            Map specMap = JSON.parseObject(skuInfo.getSpec(), Map.class);
            skuInfo.setSpecMap(specMap);
        }

        esDao.saveAll(skuInfoList);
    }

    @Override
    public void importAllToES() {
        List<Sku> skuList = skuFeign.findListBySpuId("all");
        if (null == skuList) {
            throw new RuntimeException("此商品对应的库存数据为空, 无数据导入索引库" );
        }
        String skuJsonStr = JSON.toJSONString(skuList);
        List<SkuInfo> skuInfoList = JSON.parseArray(skuJsonStr, SkuInfo.class);
        for (SkuInfo skuInfo : skuInfoList) {
            Map specMap = JSON.parseObject(skuInfo.getSpec(), Map.class);
            skuInfo.setSpecMap(specMap);
        }
        System.out.println("开始保存到es");

        esDao.saveAll(skuInfoList);
    }

    @Override
    public void reduceFromES(String spuId) {
        List<Sku> skuList = skuFeign.findListBySpuId(spuId);
        /*if (null ==skuList){
            throw new RuntimeException("此商品对应的库存数据为空，无需从索引库移除："+spuId);
        }*/
        String skuJsonstr = JSON.toJSONString(skuList);
        List<SkuInfo> skuInfoList = JSON.parseArray(skuJsonstr, SkuInfo.class);
        for (SkuInfo skuInfo : skuInfoList) {
            Map specMap = JSON.parseObject(skuInfo.getSpec(), Map.class);
            skuInfo.setSpecMap(specMap);
            System.out.println("====================");
            System.out.println(skuInfo);
            esDao.delete(skuInfo);
        }
        System.out.println("=========================================================================================================================");
        System.out.println(skuInfoList);

        esDao.deleteAll(skuInfoList);

    }


}
