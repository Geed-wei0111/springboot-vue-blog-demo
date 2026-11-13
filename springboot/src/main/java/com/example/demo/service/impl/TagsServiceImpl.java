package com.example.demo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.demo.entity.Tags;
import com.example.demo.mapper.TagsMapper;
import com.example.demo.service.TagsService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Geed
 */
@Service
public class TagsServiceImpl extends ServiceImpl<TagsMapper, Tags> implements TagsService {
    @Override
    public List<String> selectNewTags() {
        QueryWrapper<Tags> query = Wrappers.query();
        query.select("distinct tag").lambda().orderByDesc(Tags::getId);
        List<Object> list = this.baseMapper.selectObjs(query);
        return list.stream().map(o->(String)o).collect(Collectors.toList());
    }

    @Override
    public List<String> selectAllTagsFromNews() {
        return this.getBaseMapper().selectAllTagsFromNews();
    }

    @Override
    public List<String> selectAllTagsFromTag() {
        return this.getBaseMapper().selectAllTagsFromTag();
    }

    @Override
    public Integer deleteByTag(String tag) {
        return this.getBaseMapper().deleteByTag(tag);
    }

    /**
     * 根据博客ID获取所关联的标签
     * @param blogId 博客id
     * @return 标签s
     */
    @Override
    public List<String> getTagsByBlodId(Integer blogId) {
        List<Object> objects = this.listObjs(Wrappers.<Tags>lambdaQuery().eq(Tags::getBlogId, blogId).select(Tags::getTag));
        return objects.stream().map(o -> (String)o).collect(Collectors.toList());
    }
}