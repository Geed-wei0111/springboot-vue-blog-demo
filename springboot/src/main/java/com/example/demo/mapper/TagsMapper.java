package com.example.demo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.demo.entity.Tags;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface TagsMapper extends BaseMapper<Tags> {
    @Select("SELECT tags FROM `news` WHERE LENGTH(tags)!=0")
    List<String> selectAllTagsFromNews();

    @Select("SELECT tag from tags")
    List<String> selectAllTagsFromTag();

    @Delete("Delete from tags where tag = #{tag}")
    Integer deleteByTag(@Param("tag") String tag);
}