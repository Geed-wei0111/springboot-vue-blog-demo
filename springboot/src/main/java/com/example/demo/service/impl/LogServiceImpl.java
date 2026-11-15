package com.example.demo.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.demo.entity.Log;
import com.example.demo.mapper.LogMapper;
import com.example.demo.service.LogService;
import org.springframework.stereotype.Service;

/**
 * @author Geed
 */
@Service
public class LogServiceImpl extends ServiceImpl<LogMapper, Log> implements LogService {
}