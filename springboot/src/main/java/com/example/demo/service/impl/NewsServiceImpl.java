package com.example.demo.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.demo.entity.News;
import com.example.demo.mapper.NewsMapper;
import com.example.demo.service.NewsService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Geed
 */
@Service
public class NewsServiceImpl extends ServiceImpl<NewsMapper, News> implements NewsService {
    @Override
    public List<NewsMapper.Archive> selectArchive() {
        return this.getBaseMapper().selectArchive();
    }

    @Override
    public List<News> selectLatestBlogs() {
        return this.getBaseMapper().selectLatestBlogs();
    }

    @Override
    public List<News> selectMostLikes() {
        return this.getBaseMapper().selectMostLikes();
    }

    @Override
    public List<News> selectMostCollect() {
        return this.getBaseMapper().selectMostCollect();
    }
}