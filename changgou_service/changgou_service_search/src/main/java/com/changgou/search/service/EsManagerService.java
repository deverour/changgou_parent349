package com.changgou.search.service;

import org.springframework.stereotype.Service;


public interface EsManagerService {

    public void createIndexAndMapping();


    public void importDataToEs(String spuId);

    public void importAllToES();
}
