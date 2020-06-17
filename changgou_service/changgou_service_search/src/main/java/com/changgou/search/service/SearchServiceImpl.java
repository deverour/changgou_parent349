package com.changgou.search.service;

import com.alibaba.fastjson.JSON;
import com.changgou.search.pojo.SkuInfo;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.Operator;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.SearchResultMapper;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.aggregation.impl.AggregatedPageImpl;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SearchServiceImpl implements SearchService{

    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;


    @Override
    public Map search(Map<String, String> searchMap) {
        Map<String,Object> resultMap = new HashMap<>();
        //有条件才查询Es
        if (searchMap != null) {
            //组合条件对象
            BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
            //0:关键词
            if (!StringUtils.isEmpty(searchMap.get("keywords"))){
                boolQueryBuilder.must(QueryBuilders.matchQuery("name",searchMap.get("keywords")).operator(Operator.AND));
            }
            //1：条件 品牌
            if (!StringUtils.isEmpty(searchMap.get("brand"))){
                boolQueryBuilder.filter(QueryBuilders.termQuery("brandName",searchMap.get("brand")));
            }
            //4. 原生搜索实现类
            NativeSearchQueryBuilder nativeSearchQueryBuilder = new NativeSearchQueryBuilder();
            nativeSearchQueryBuilder.withQuery(boolQueryBuilder);

            //6.品牌聚合(分组)查询
            String skuBrand = "skuBrand";


            //10: 执行查询, 返回结果对象
            AggregatedPage<SkuInfo> aggregatedPage = elasticsearchTemplate.queryForPage(nativeSearchQueryBuilder.build(), SkuInfo.class, new SearchResultMapper() {
                @Override
                public <T> AggregatedPage<T> mapResults(SearchResponse searchResponse, Class<T> aClass, Pageable pageable) {
                    List<T> list = new ArrayList<>();
                    SearchHits hits = searchResponse.getHits();
                    if (null != hits) {
                        for (SearchHit hit : hits) {
                            SkuInfo skuInfo = JSON.parseObject(hit.getSourceAsString(), SkuInfo.class);
                            list.add((T) skuInfo);
                        }
                    }
                    return new AggregatedPageImpl<T>(list,pageable,hits.getTotalHits(),searchResponse.getAggregations());
                }
            });
            //11. 总条数
            resultMap.put("total", aggregatedPage.getTotalElements());
            //12. 总页数
            resultMap.put("totalPages", aggregatedPage.getTotalPages());
            //13. 查询结果集合
            resultMap.put("rows", aggregatedPage.getContent());

            return resultMap;
        }
        return null;
    }
}
