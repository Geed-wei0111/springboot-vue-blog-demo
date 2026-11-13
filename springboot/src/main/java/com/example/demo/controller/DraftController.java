package com.example.demo.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.example.demo.common.Result;
import com.example.demo.entity.Draft;
import com.example.demo.service.DraftService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.net.URLDecoder;
import java.util.Objects;
import java.util.StringJoiner;

import static com.example.demo.common.TokenUtils.getUser;

/**
 * @author Geed
 */
@RestController
@RequestMapping("/draft")
public class DraftController {
    @Resource
    DraftService draftService;

    /**
     * 保存草稿，如果数据库里有就更新，不然插入数据库
     * @param draft 草稿
     * @param arr 标签数组
     * @return rst
     */
    @PostMapping
    public Result<?> save(@RequestBody Draft draft, @RequestParam String[] arr){
        StringJoiner tagsStr = new StringJoiner(",");
        if (arr.length  != 0){
            for (int i = 0;i < arr.length;i++){
                String temp = arr[i];
                arr[i] = URLDecoder.decode(temp);
                tagsStr.add(arr[i]);
            }
            draft.setTags(tagsStr.toString());
        }else{
            draft.setTags("");
        }

        LambdaQueryWrapper<Draft> wrapper = Wrappers.lambdaQuery();
        if (draftService.count(wrapper.eq(Draft::getUserId, draft.getUserId()))>0){
            draftService.updateByUserId(draft.getTitle(), draft.getContent(), draft.getTags(), draft.getUserId());
        }
        else{
            draftService.save(draft);
        }
        return Result.success();
    }

    @GetMapping
    public Result<?> getDraft(){
        Integer id = Objects.requireNonNull(getUser()).getId();
        LambdaQueryWrapper<Draft> wrapper = Wrappers.lambdaQuery();
        if (draftService.count(wrapper.eq(Draft::getUserId, id))>0){
            Draft draft = draftService.selectByUserId(id);
            return Result.success(draft);
        }else{
            return Result.error("-1","没有草稿");
        }
    }
}