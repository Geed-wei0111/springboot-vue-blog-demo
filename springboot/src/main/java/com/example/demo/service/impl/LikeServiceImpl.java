package com.example.demo.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.demo.entity.Like;
import com.example.demo.mapper.LikeMapper;
import com.example.demo.service.LikeService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Geed
 */
@Service
public class LikeServiceImpl extends ServiceImpl<LikeMapper, Like> implements LikeService {
    @Override
    public List<Object> listBlogidByUseridOrderById(Integer userId){
        return this.listObjs(Wrappers.<Like>lambdaQuery()
                .select(Like::getBlogId)
                .eq(Like::getUserId, userId)
                .orderByDesc(Like::getId)
        );
    }
}