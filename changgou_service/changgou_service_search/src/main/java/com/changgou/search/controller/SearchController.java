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

    //对搜索入参带有特殊符号进行处理
    public void handlerSearchMap(Map<String,String> searchMap){

        if(null != searchMap){
            Set<Map.Entry<String, String>> entries = searchMap.entrySet();
            for (Map.Entry<String, String> entry : entries) {
                if(entry.getKey().startsWith("spec_")){
                    searchMap.put(entry.getKey(),entry.getValue().replace("+","%2B"));
                }
            }
        }

    }

    @GetMapping
    public Map search(@RequestParam Map<String,String> searchMap){
        //特殊符号处理
        handlerSearchMap(searchMap);
        Map resultMap = searchService.search(searchMap);
        return resultMap;
    }
}
