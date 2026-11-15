package com.example.demo.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.demo.entity.Draft;
import com.example.demo.mapper.DraftMapper;
import com.example.demo.service.DraftService;
import org.springframework.stereotype.Service;

/**
 * @author Geed
 */
@Service
public class DraftServiceImpl extends ServiceImpl<DraftMapper, Draft> implements DraftService {
    @Override
    public Integer updateByUserId(String title, String content, String tags, Integer userId) {
        return this.getBaseMapper().updateByUserId(title, content, tags, userId);
    }

    @Override
    public Draft selectByUserId(Integer id) {
        return this.getBaseMapper().selectByUserId(id);
    }
}