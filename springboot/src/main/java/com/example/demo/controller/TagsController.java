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
        QueryWrapper<Tags> queryWrapper = Wrappers.query();
        queryWrapper.select("DISTINCT tag");
        List list = tagsService.listMaps(queryWrapper);
        System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!"+list);
        return Result.success(list);
    }

    /**
     * 获取最新的5个标签，用于显示在主页
     * @return tags
     */
    @GetMapping("/new")
    public Result<?> getNewTags(){
        //
        return Result.success(tagsService.selectNewTags());
    }

    /**
     * 删除标签功能
     * 先检查所有博客中是否含有该标签
     * 有就不能删
     * 没有就删除
     * @param tag 标签
     * @return rst
     */
    @PostMapping("/del")
    public Result<?> deleteTag(@RequestBody String tag){
        //无关联可以删除，有关联不能删除
        //删除标签时，排除掉博客id不为空的标签，避免删除有博客关联的标签
        boolean remove = tagsService.remove(
            Wrappers.<Tags>lambdaQuery()
            .eq(Tags::getTag, tag)
            .isNull(Tags::getBlogId)
        );
        //查询该标签是否还有博客关联
        long count = tagsService.count(
            Wrappers.<Tags>lambdaQuery()
            .eq(Tags::getTag, tag)
        );
        if (remove && count == 0){//删除成功并且没有关联
            return Result.success();
        }else if (remove && count > 0){//删除成功但是还有关联
            return Result.error("-1","标签有部分关联，请先删除关联的博客");
        }
        else{
            return Result.error("-1","标签有关联，不能删除");
        }
    }
    /**
     * 测试用：添加标签功能
     * @param entity
     * @return rst
     */
    @PostMapping("/add")
    public Result<?> addTag(@RequestBody String entity) {
        boolean saved = tagsService.save(new Tags(entity));
        if (saved) {
            return Result.success();
        } else {
            return Result.error("-1", "标签添加失败");
        }
    }
}