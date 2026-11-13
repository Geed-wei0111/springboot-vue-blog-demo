package com.example.demo.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.demo.entity.Visit;
import com.example.demo.mapper.VisitMapper;
import com.example.demo.service.VisitService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Geed
 */
@Service
public class VisitServiceImpl extends ServiceImpl<VisitMapper, Visit> implements VisitService {
    @Override
    public List<Object> listBlogidByUseridOrderById(Integer userId) {
        return this.listObjs(Wrappers.<Visit>lambdaQuery()
                .select(Visit::getBlogId)
                .eq(Visit::getUserId, userId)
                .orderByDesc(Visit::getId));
    }
}