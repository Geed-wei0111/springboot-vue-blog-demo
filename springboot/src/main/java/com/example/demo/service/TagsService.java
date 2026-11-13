package com.example.demo.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.demo.entity.Tags;

import java.util.List;

/**
 * @author Geed
 */
public interface TagsService extends IService<Tags> {
    List<String> selectNewTags();
    List<String> selectAllTagsFromNews();
    List<String> selectAllTagsFromTag();
    Integer deleteByTag(String tag);

    List<String> getTagsByBlodId(Integer blogId);
}