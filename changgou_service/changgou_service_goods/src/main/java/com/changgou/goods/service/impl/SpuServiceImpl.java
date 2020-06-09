package com.changgou.goods.service.impl;

import com.alibaba.fastjson.JSON;
import com.changgou.goods.dao.*;
import com.changgou.goods.pojo.*;
import com.changgou.goods.service.SpuService;
import com.changgou.util.IdWorker;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class SpuServiceImpl implements SpuService {

    @Autowired
    private IdWorker idWorker;

    @Autowired
    private SkuMapper skuMapper;
    @Autowired
    private SpuMapper spuMapper;

    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private BrandMapper brandMapper;

    @Autowired
    private CategoryBrandMapper categoryBrandMapper;
    /**
     * 查询全部列表
     * @return
     */
    @Override
    public List<Spu> findAll() {
        return spuMapper.selectAll();
    }

    /**
     * 根据ID查询
     * @param id
     * @return
     */
    @Override
    public Spu findById(String id){
        return  spuMapper.selectByPrimaryKey(id);
    }

    @Override
    public Goods findGoodsById(String id) {
        Spu spu = spuMapper.selectByPrimaryKey(id);
        Example example = new Example(Sku.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("spuId",id);
        List<Sku> skuList = skuMapper.selectByExample(example);
        Goods goods = new Goods();
        goods.setSpu(spu);
        goods.setSkuList(skuList);
        return goods;


    }


    /**
     * 增加
     * @param spu
     */
    @Override
    public void add(Spu spu){
        spuMapper.insert(spu);
    }

    @Override
    public void addGoods(Goods goods) {
        Spu spu = goods.getSpu();
        spu.setId(String.valueOf(idWorker.nextId()));
        spu.setIsDelete("0");//删除状态，默认为未删除
        spu.setIsMarketable("0");//上架状态
        spu.setStatus("0");//审核状态
        spuMapper.insertSelective(spu);
        CategoryBrand categoryBrand = new CategoryBrand();
        categoryBrand.setBrandId(spu.getBrandId());
        categoryBrand.setCategoryId(spu.getCategory3Id());

        int count = categoryBrandMapper.selectCount(categoryBrand);
        if (count == 0){
            categoryBrandMapper.insertSelective(categoryBrand);
        }
        insertSkuList(goods);


    }

    /**
     * 添加库存集合数据
     * @param goods
     */
    private void insertSkuList(Goods goods){
        Spu spu = goods.getSpu();
        List<Sku> skuList = goods.getSkuList();
        if (skuList != null){
            for (Sku sku : skuList){
                sku.setId(String.valueOf(idWorker.nextId()));//库存Id
                sku.setCategoryId(spu.getCategory3Id());//分类id
                Category category = categoryMapper.selectByPrimaryKey(spu.getCategory3Id());
                sku.setCategoryName(category.getName());//分类名称
                if (StringUtils.isEmpty(sku.getSpec())){
                    sku.setSpec("{}");//规格
                }

                sku.setSpuId(spu.getId());//商品Id
                sku.setStatus("0");//审核状态
                sku.setCreateTime(new Date());//创建时间
                sku.setUpdateTime(new Date());//修改时间
                String images = sku.getImages();
                if(!StringUtils.isEmpty(images)){
                    String[] imageArray = images.split(",");
                    if (imageArray.length>0){
                        sku.setImage(imageArray[0]);//示例图片

                    }
                }
                Brand brand = brandMapper.selectByPrimaryKey(spu.getBrandId());
                sku.setBrandName(brand.getName());
                //库存名称=商品名称+规格
                String title = spu.getName();
                String specJsonStr = sku.getSpec();
                //将JSON转换成单个java对象
                //将JSON转换成集合对象
                //JSON.parseArray();
                Map<String,String> specMap = JSON.parseObject(specJsonStr, Map.class);
                for (String specValue : specMap.values()){
                    title += " " + specValue;
                }
                sku.setName(title);

                skuMapper.insertSelective(sku);

            }
        }
    }

    /**
     * 修改
     * @param spu
     */
    @Override
    public void update(Spu spu){
        spuMapper.updateByPrimaryKey(spu);
    }

    @Override
    public void updateGoods(Goods goods) {
        spuMapper.updateByPrimaryKeySelective(goods.getSpu());
        Example example = new Example(Sku.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("spuId",goods.getSpu().getId());
        skuMapper.deleteByExample(example);

        insertSkuList(goods);


    }

    /**
     * 删除
     * @param id
     */
    @Override
    public void delete(String id){
        spuMapper.deleteByPrimaryKey(id);
        Example example = new Example(Sku.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("spuId",id);
        skuMapper.deleteByExample(example);
    }


    /**
     * 条件查询
     * @param searchMap
     * @return
     */
    @Override
    public List<Spu> findList(Map<String, Object> searchMap){
        Example example = createExample(searchMap);
        return spuMapper.selectByExample(example);
    }

    /**
     * 分页查询
     * @param page
     * @param size
     * @return
     */
    @Override
    public Page<Spu> findPage(int page, int size){
        PageHelper.startPage(page,size);
        return (Page<Spu>)spuMapper.selectAll();
    }

    /**
     * 条件+分页查询
     * @param searchMap 查询条件
     * @param page 页码
     * @param size 页大小
     * @return 分页结果
     */
    @Override
    public Page<Spu> findPage(Map<String,Object> searchMap, int page, int size){
        PageHelper.startPage(page,size);
        Example example = createExample(searchMap);
        return (Page<Spu>)spuMapper.selectByExample(example);
    }

    @Override
    public void audit(String spuId) {
        Spu spu = spuMapper.selectByPrimaryKey(spuId);
        if (!"0".equals(spu.getIsMarketable())){
            throw new RuntimeException("这个商品已上架,不能进行审核");
        }
        if (!"0".equals(spu.getIsDelete())){
            throw new RuntimeException("这个商品已删除，不能进行深");
        }
        spu.setStatus("1");
        spuMapper.updateByPrimaryKeySelective(spu);
        Sku sku = new Sku();
        sku.setStatus("1");
        Example example = new Example(Sku.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("spuId",spuId);

        skuMapper.updateByExampleSelective(sku,example);
    }

    @Override
    public void put(String spuId) {
        Spu spu = spuMapper.selectByPrimaryKey(spuId);
        if(!"0".equals(spu.getIsDelete())){
            throw new RuntimeException("您的商品已删除,不可以上架");
        }
        if(!"1".equals(spu.getStatus())){
            throw new RuntimeException("你的商品为审核,不可以上架");
        }
        spu.setIsMarketable("1");
        spuMapper.updateByPrimaryKeySelective(spu);
    }

    @Override
    public void pull(String spuId) {
        Spu spu = new Spu();
        spu.setId(spuId);
        spu.setIsMarketable("0");
        spuMapper.updateByPrimaryKeySelective(spu);
    }

    @Override
    public void del(String spuId) {
        Spu spu = spuMapper.selectByPrimaryKey(spuId);
        if ("1".equals(spu.getIsMarketable())){
            throw new RuntimeException("该商品已上架，不能删除");
        }
        spu.setIsDelete("1");
        spuMapper.updateByPrimaryKeySelective(spu);
    }

    @Override
    public void restore(String spuId) {
        Spu spu = new Spu();
        spu.setId(spuId);
        spu.setIsDelete("0");//设置为未删除
        spu.setStatus("0");//设置为未审核
        spu.setIsMarketable("0");//设置为未上架
        spuMapper.updateByPrimaryKeySelective(spu);
    }

    /**
     * 构建查询对象
     * @param searchMap
     * @return
     */
    private Example createExample(Map<String, Object> searchMap){
        Example example=new Example(Spu.class);
        Example.Criteria criteria = example.createCriteria();
        if(searchMap!=null){
            // 主键
            if(searchMap.get("id")!=null && !"".equals(searchMap.get("id"))){
                criteria.andEqualTo("id",searchMap.get("id"));
           	}
            // 货号
            if(searchMap.get("sn")!=null && !"".equals(searchMap.get("sn"))){
                criteria.andEqualTo("sn",searchMap.get("sn"));
           	}
            // SPU名
            if(searchMap.get("name")!=null && !"".equals(searchMap.get("name"))){
                criteria.andLike("name","%"+searchMap.get("name")+"%");
           	}
            // 副标题
            if(searchMap.get("caption")!=null && !"".equals(searchMap.get("caption"))){
                criteria.andLike("caption","%"+searchMap.get("caption")+"%");
           	}
            // 图片
            if(searchMap.get("image")!=null && !"".equals(searchMap.get("image"))){
                criteria.andLike("image","%"+searchMap.get("image")+"%");
           	}
            // 图片列表
            if(searchMap.get("images")!=null && !"".equals(searchMap.get("images"))){
                criteria.andLike("images","%"+searchMap.get("images")+"%");
           	}
            // 售后服务
            if(searchMap.get("saleService")!=null && !"".equals(searchMap.get("saleService"))){
                criteria.andLike("saleService","%"+searchMap.get("saleService")+"%");
           	}
            // 介绍
            if(searchMap.get("introduction")!=null && !"".equals(searchMap.get("introduction"))){
                criteria.andLike("introduction","%"+searchMap.get("introduction")+"%");
           	}
            // 规格列表
            if(searchMap.get("specItems")!=null && !"".equals(searchMap.get("specItems"))){
                criteria.andLike("specItems","%"+searchMap.get("specItems")+"%");
           	}
            // 参数列表
            if(searchMap.get("paraItems")!=null && !"".equals(searchMap.get("paraItems"))){
                criteria.andLike("paraItems","%"+searchMap.get("paraItems")+"%");
           	}
            // 是否上架
            if(searchMap.get("isMarketable")!=null && !"".equals(searchMap.get("isMarketable"))){
                criteria.andEqualTo("isMarketable",searchMap.get("isMarketable"));
           	}
            // 是否启用规格
            if(searchMap.get("isEnableSpec")!=null && !"".equals(searchMap.get("isEnableSpec"))){
                criteria.andEqualTo("isEnableSpec", searchMap.get("isEnableSpec"));
           	}
            // 是否删除
            if(searchMap.get("isDelete")!=null && !"".equals(searchMap.get("isDelete"))){
                criteria.andEqualTo("isDelete",searchMap.get("isDelete"));
           	}
            // 审核状态
            if(searchMap.get("status")!=null && !"".equals(searchMap.get("status"))){
                criteria.andEqualTo("status",searchMap.get("status"));
           	}

            // 品牌ID
            if(searchMap.get("brandId")!=null ){
                criteria.andEqualTo("brandId",searchMap.get("brandId"));
            }
            // 一级分类
            if(searchMap.get("category1Id")!=null ){
                criteria.andEqualTo("category1Id",searchMap.get("category1Id"));
            }
            // 二级分类
            if(searchMap.get("category2Id")!=null ){
                criteria.andEqualTo("category2Id",searchMap.get("category2Id"));
            }
            // 三级分类
            if(searchMap.get("category3Id")!=null ){
                criteria.andEqualTo("category3Id",searchMap.get("category3Id"));
            }
            // 模板ID
            if(searchMap.get("templateId")!=null ){
                criteria.andEqualTo("templateId",searchMap.get("templateId"));
            }
            // 运费模板id
            if(searchMap.get("freightId")!=null ){
                criteria.andEqualTo("freightId",searchMap.get("freightId"));
            }
            // 销量
            if(searchMap.get("saleNum")!=null ){
                criteria.andEqualTo("saleNum",searchMap.get("saleNum"));
            }
            // 评论数
            if(searchMap.get("commentNum")!=null ){
                criteria.andEqualTo("commentNum",searchMap.get("commentNum"));
            }

        }
        return example;
    }

}
