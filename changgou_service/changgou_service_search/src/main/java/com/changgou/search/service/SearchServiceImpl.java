package com.changgou.search.service;

import com.alibaba.fastjson.JSON;
import com.changgou.search.pojo.SkuInfo;
import org.apache.lucene.queryparser.xml.builders.BooleanQueryBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.SearchResultMapper;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.aggregation.impl.AggregatedPageImpl;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.swing.text.Highlighter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SearchServiceImpl implements SearchService{

    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;

    public final  static  Integer PAGE_SIZE = 20;


    @Override
    public Map search(Map<String, String> searchMap) {
        if (searchMap == null || searchMap.size()==0){
            return null;
        }
        Map<String,Object> resultmap = new HashMap<>();
        /**
         * 获取查询条件
         */
        //获取查询的关键字
        String keywords = searchMap.get("keywords");

        //获取当前页
        String pageNum = searchMap.get("pageNum");
        if (StringUtils.isEmpty(pageNum)){
            searchMap.put("pageNum","1");
            pageNum = "1";
        }

        /**
         * 封装查询对象
         */

        //创建顶级条件查询对象
        NativeSearchQueryBuilder nativeSearchQueryBuilder = new NativeSearchQueryBuilder();
        //创建组合条件查询对象（多种查询条件）
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        /**
         * 根据关键字查询
         */
        if (!StringUtils.isEmpty(keywords)){
            //应该，是or或者的意思
            //boolQueryBuilder.should();
            //非，not的意思
            //boolQueryBuilder.mustNot();
            //必须，and的意思
            //QueryBuilders.matchQuery是将查询的关键字根据指定的分词器切分词后，将切分出来的词一个一个查询
            boolQueryBuilder.must(QueryBuilders.matchQuery("name",keywords).operator(Operator.AND));
            //将组合查询对象放入顶级查询对象中
            nativeSearchQueryBuilder.withQuery(boolQueryBuilder);
        }


        /**
         * 根据品牌过滤查询
         */

        /**
         * 根据规格过滤查询
         */

        /**
         * 根据价格过滤查询
         */

        /**
         *分页查询
         * PageRequest对象，第一参数是从第几页开始查询，第二个参数是每页查询多少条数据
         * 第一个参数从第几页查询，默认起始是从第0页开始查询
         */
        nativeSearchQueryBuilder.withPageable(new PageRequest(Integer.parseInt(pageNum)-1,PAGE_SIZE));

        /**
         * 高亮查询
         */
        HighlightBuilder.Field highLightField = new HighlightBuilder.Field("name").preTags("<em style\"color:red\">").postTags("</em>");
        nativeSearchQueryBuilder.withHighlightFields(highLightField);
        /**
         * 排序查询
         */

        /**
         * 根据品牌聚合查询
         */

        /**
         * 根据规格聚合查询
         */

        /**
         * 查询并返回结果集(包含高亮)
         */
        //AggregatedPage<SkuInfo> skuInfos = elasticsearchTemplate.queryForPage(nativeSearchQueryBuilder.build(), SkuInfo.class);
        AggregatedPage<SkuInfo> skuInfos = elasticsearchTemplate.queryForPage(nativeSearchQueryBuilder.build(), SkuInfo.class, new SearchResultMapper() {
            //在这里重新组合查询结果，将高亮名称获取放入到结果集中
            @Override
            public <T> AggregatedPage<T> mapResults(SearchResponse searchResponse, Class<T> aClass, Pageable pageable) {
                List<T> resultList = new ArrayList<>();
                //从查询响应中获取查询结果对象
                SearchHits hits = searchResponse.getHits();
                if (hits != null){
                    //查询到的总记录数
                    //long totalHits = hits.getTotalHits();
                    //查询到的结果集
                    SearchHit[] hitArray = hits.getHits();
                    if (hitArray.length >0) {
                        for (SearchHit searchHit : hitArray) {
                            String skuInfoJsonStr = searchHit.getSourceAsString();
                            //将skuInfo的json字符串转换成skuInfo对象
                            SkuInfo skuInfo = JSON.parseObject(skuInfoJsonStr, SkuInfo.class);
                            /**
                             * 获取高亮名称
                             * 高亮名称如果能获取到，则获取出来放入skuInfo对象中的name属性中
                             * 如果获取不到高亮名称则使用SkuInfo对象中原有的不带高亮的名称
                             */
                            if (searchHit.getHighlightFields() != null && searchHit.getHighlightFields().size()>0){
                                Text[] names = searchHit.getHighlightFields().get("name").fragments();
                                skuInfo.setName(names[0].toString());
                            }


                            //将这个对象放入返回的结果集中
                            resultList.add((T) skuInfo);
                            System.out.println("===========" + skuInfoJsonStr);
                        }
                    }
                }

                return new AggregatedPageImpl<T>(resultList,pageable,hits.getTotalHits(),searchResponse.getAggregations());
            }
        });
        /**
         * 获取根据品牌聚合的结果集
         */

        /**
         * 获取根据规格聚合的结果集
         */

        /**
         * 封装查询结果后返回
         */
         //当前页
         resultmap.put("pageNum",pageNum);
         //查询返回的结果集
         resultmap.put("rows",skuInfos.getContent());
         //总页数
         resultmap.put("totalPage",skuInfos.getTotalPages());
         //查询到的总条数
         resultmap.put("total",skuInfos.getTotalElements());





        return resultmap;
    }


}
































