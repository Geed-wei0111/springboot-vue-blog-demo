package com.example.demo.controller;

import com.example.demo.common.Result;
import com.example.demo.entity.Favorite;
import com.example.demo.mapper.FavoriteMapper;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author Geed
 */
@RestController
@RequestMapping("/favors")
public class FavoriteController {
    @Resource
    FavoriteMapper favoriteMapper;

    @GetMapping("/list")
    public Result<?> getList(){
        List<Favorite> favorites = favoriteMapper.selectList(null);
        return Result.success(favorites);
    }
}
