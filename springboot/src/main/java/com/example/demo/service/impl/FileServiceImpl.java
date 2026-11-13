package com.example.demo.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.demo.entity.Files;
import com.example.demo.mapper.FilesMapper;
import com.example.demo.service.FilesService;
import org.springframework.stereotype.Service;

/**
 * @author Geed
 */
@Service
public class FileServiceImpl extends ServiceImpl<FilesMapper, Files> implements FilesService {
}