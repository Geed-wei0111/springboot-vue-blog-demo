package com.example.demo.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.example.demo.common.Result;
import com.example.demo.entity.Tags;
import com.example.demo.service.TagsService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author Geed
 */
@RestController
@RequestMapping("/tags")
public class TagsController {
    @Resource
    TagsService tagsService;

    /**
     * 查询所有数据库中的标签
     * @return tags(string)
     */
    @GetMapping
    public Result<?> getAllTags() {
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.select("DISTINCT tag");
        List list = tagsService.listObjs(queryWrapper);
        return Result.success(list);
    }

    /**
     * 查询所有数据库中的标签
     * @return tags(map)
     */
    @GetMapping("/list")
    public Result<?> getTagsList() {
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.select("DISTINCT tag");
        List list = tagsService.listMaps(queryWrapper);
        return Result.success(list);
    }

    /**
     * 获取最新的5个标签，用于显示在主页
     * @return tags
     */
    @GetMapping("/new")
    public Result<?> getNewTags(){
        //TODO rewrite func
        return Result.success(tagsService.selectNewTags());
    }

    /**
     * 删除标签功能
     * 先检查数据库中的博客是否含有该标签
     * 有就不能删
     * 没有就删除
     * @param tag 标签
     * @return res
     */
    @PostMapping("/del")
    public Result<?> deleteTag(@RequestBody String tag){
        //不管关联，直接删除
        boolean remove = tagsService.remove(Wrappers.<Tags>lambdaQuery().eq(Tags::getTag, tag));
        if (remove){
            return Result.success();
        }else{
            return Result.error("-1","标签有关联，不能删除");
        }
    }
}