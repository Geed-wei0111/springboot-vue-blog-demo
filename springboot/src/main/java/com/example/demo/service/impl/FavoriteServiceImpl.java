package com.example.demo.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.demo.entity.Favorite;
import com.example.demo.entity.Log;
import com.example.demo.mapper.FavoriteMapper;
import com.example.demo.mapper.LogMapper;
import com.example.demo.service.FavoriteService;
import com.example.demo.service.LogService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Geed
 */
@Service
public class FavoriteServiceImpl extends ServiceImpl<FavoriteMapper, Favorite> implements FavoriteService {
    @Override
    public List<Object> listBlogidByUseridOrderById(Integer userId) {
        return this.listObjs(Wrappers.<Favorite>lambdaQuery()
                .select(Favorite::getBlogId)
                .eq(Favorite::getUserId, userId)
                .orderByDesc(Favorite::getId));
    }
}