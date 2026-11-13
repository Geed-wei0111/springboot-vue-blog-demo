package com.example.demo.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.demo.entity.Draft;

/**
 * @author Geed
 */
public interface DraftService extends IService<Draft> {
    Integer updateByUserId(String title, String content, String tags, Integer userId);
    Draft selectByUserId(Integer id);
}