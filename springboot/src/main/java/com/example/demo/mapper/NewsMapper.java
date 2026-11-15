package com.example.demo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.demo.entity.News;
import lombok.Data;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * @author Geed
 */
public interface NewsMapper extends BaseMapper<News> {
    @Data
    class Archive{
        private Integer year;
        private Integer month;
        private Integer count;
    }
    @Select("SELECT YEAR(date) as year ,MONTH(date) as month ,COUNT(*) as count from news GROUP BY year,month")
    List<Archive> selectArchive();

    @Select("Select * from news order by id desc limit 5")
    List<News> selectLatestBlogs();

    @Select("SELECT news.* from recommend,news WHERE `like`!=0 AND news.id = blog_id ORDER BY `like` DESC LIMIT 5")
    List<News> selectMostLikes();

    /**
     * 获取收藏量 TOP5 的博客信息
     * @return list
     */
    @Select("SELECT news.* from news,(SELECT blog_id,COUNT(*) as count FROM `favorites` GROUP BY blog_id ORDER BY count DESC LIMIT 5) as favors where news.id = favors.blog_id;")
    List<News> selectMostCollect();
}