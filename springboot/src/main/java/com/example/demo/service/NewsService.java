package com.example.demo.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.demo.entity.News;
import com.example.demo.mapper.NewsMapper;

import java.util.List;

/**
 * @author Geed
 */
public interface NewsService extends IService<News> {
    List<NewsMapper.Archive> selectArchive();
    List<News> selectLatestBlogs();
    List<News> selectMostLikes();
    List<News> selectMostCollect();
}