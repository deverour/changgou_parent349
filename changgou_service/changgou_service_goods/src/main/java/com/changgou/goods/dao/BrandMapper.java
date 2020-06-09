package com.changgou.goods.dao;

import com.changgou.goods.pojo.Brand;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface BrandMapper extends Mapper<Brand> {

    @Select("SELECT * FROM tb_brand WHERE id IN(" +
            "SELECT brand_id FROM tb_category_brand WHERE category_id IN (" +
            "SELECT id FROM tb_category WHERE NAME =#{categoryName}))")
    public List<Brand> findListByCategoryName(@Param("categoryName") String categoryName);
}
