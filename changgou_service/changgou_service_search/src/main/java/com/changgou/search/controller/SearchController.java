package com.changgou.search.controller;

import com.changgou.search.service.EsManagerService;
import com.changgou.search.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/search")
public class SearchController {

    @Autowired
    private EsManagerService esManagerService;

    @Autowired
    private SearchService searchService;

    /**
     * 处理接收的参数中的特殊符号
     * %2B代表加号，这里在接受参数的时候，对于加号有可能会自动转换成空格，造成查询不到规格对应的数据
     * @param searchMap
     * @return
     */
    public void paramHandler(Map<String,String> searchMap){
        if (searchMap !=null && searchMap.size() > 0){
            for (String key : searchMap.keySet()) {
                if (key.startsWith("spec_")){
                    searchMap.put(key,searchMap.get(key).replace("+","%2B"));
                    searchMap.put(key,searchMap.get(key).replace(" ","%2B"));
                }
            }
        }
    }




    @GetMapping
    public Map search(@RequestParam Map<String,String> searchMap){
        //特殊符号处理
        paramHandler(searchMap);
        Map resultMap = searchService.search(searchMap);
        return resultMap;
    }
}
