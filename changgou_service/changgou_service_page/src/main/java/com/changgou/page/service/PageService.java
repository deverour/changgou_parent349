package com.changgou.page.service;

import java.util.Map;

public interface PageService {
    /**
     * 根据商品id，查询商品静态页面所需要的所有数据
     * 包干商品数据，库存集合数据，分类数据，图片数据等
     * @param spuId
     * @return
     */
    public Map<String,Object> findItemData(String spuId);

    /**
     * 根据数据和模板生成商品详情静态化页面
     * @param dataMap 页面中需要的数据
     * @param spuId 商品id，作为生成的文件名使用
     */
    public void createItemPage(Map<String,Object> dataMap,String spuId);

}
