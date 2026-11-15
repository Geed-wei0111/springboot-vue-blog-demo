package com.example.demo.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.demo.entity.Favorite;

import java.util.List;

/**
 * @author Geed
 */
public interface FavoriteService extends IService<Favorite> {
    /**
     * 通过用户的ID获取该用户收藏过的博客IDs，并按照ID降序
     * @param userId 用户id
     * @return list
     */
    List<Object> listBlogidByUseridOrderById(Integer userId);
}