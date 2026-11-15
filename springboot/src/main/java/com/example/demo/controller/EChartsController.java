package com.example.demo.controller;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.Quarter;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.example.demo.common.Result;
import com.example.demo.entity.News;
import com.example.demo.service.NewsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 图表统计控制器
 * @author geed
 */
@RestController
@RequestMapping("/echarts")
public class EChartsController {
    @Resource
    NewsService newsService;

    /**
     * 获取各个季度的博客图表
     * @return count*4
     */
    @GetMapping("/blogs")
    public Result<?> countBlogByDate(){
        int q1 = 0;
        int q2 = 0;
        int q3 = 0;
        int q4 = 0;
        List<Object> dates = newsService.listObjs(Wrappers.<News>lambdaQuery().select(News::getDate));

        for (Date date : dates.stream().map(o -> (Date)o).collect(Collectors.toList())) {
            Quarter quarter = DateUtil.quarterEnum(date);
            switch (quarter){
                case Q1:q1+=1;break;
                case Q2:q2+=1;break;
                case Q3:q3+=1;break;
                case Q4:q4+=1;break;
                default:break;
            }
        }
        return Result.success(CollUtil.newArrayList(q1, q2, q3, q4));
    }
}