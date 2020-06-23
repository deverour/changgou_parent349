package com.changgou.search.controller;

import com.changgou.entity.Page;
import com.changgou.search.pojo.SkuInfo;
import com.changgou.search.service.EsManagerService;
import com.changgou.search.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;


import java.util.Map;
import java.util.Set;

@Controller
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
    private void paramHandler(Map<String,String> searchMap){
        if (searchMap !=null && searchMap.size() > 0){
            for (String key : searchMap.keySet()) {
                if (key.startsWith("spec_")){
                    searchMap.put(key,searchMap.get(key).replace("+","%2B"));
                    searchMap.put(key,searchMap.get(key).replace(" ","%2B"));
                }
            }
        }
    }

    /**
     * 高级搜索
     * @param searchMap
     * @return
     */
    @GetMapping
    @ResponseBody
    public Map search(@RequestParam Map<String,String> searchMap){
        //特殊符号处理
        paramHandler(searchMap);
        Map resultMap = searchService.search(searchMap);
        return resultMap;
    }

    /**
     * 搜索并返回到搜索结果列表页面
     * @param searchMap
     * @return
     */
    @GetMapping("/list")
    public String list(@RequestParam Map<String,String> searchMap, Model model){
        paramHandler(searchMap);
        //搜索并返回结果数据
        Map resultMap = searchService.search(searchMap);
        model.addAttribute("result",resultMap);
        model.addAttribute("searchMap",searchMap);

        Page<SkuInfo> page = new Page<SkuInfo>(
                Long.parseLong(String.valueOf(resultMap.get("total"))) ,
                Integer.parseInt(String.valueOf(resultMap.get("pageNum"))) ,
                Page.pageSize);

        StringBuilder url = new StringBuilder("/search/list");
        if (searchMap != null && searchMap.size()>0){
            url.append("?");
            for (String paramKey : searchMap.keySet()) {
                if (!"sortRule".equals(paramKey) && !"sortField".equals(paramKey) && !"pageNum".equals(paramKey) ){
                    url.append(paramKey).append("=").append(searchMap.get(paramKey)).append("&");
                }
            }
            String urlStr = url.toString();
            urlStr = url.substring(0, url.length() - 1);
            model.addAttribute("url",urlStr);
        }else {
            model.addAttribute("url",url);
        }

        return "search";
    }
}
