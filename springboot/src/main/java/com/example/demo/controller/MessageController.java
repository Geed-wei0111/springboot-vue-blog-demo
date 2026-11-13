package com.example.demo.controller;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.example.demo.common.Result;
import com.example.demo.entity.Message;
import com.example.demo.mapper.MessageMapper;
import com.example.demo.service.MessageService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author Geed
 */
@RestController
@RequestMapping("/message")
public class MessageController {
    @Resource
    MessageService messageService;

    /**
     * 接受评论后，保存到数据库中
     * @param message 评论
     * @return rst
     */
    @PostMapping
    public Result<?> receive(@RequestBody Message message){
        if (StrUtil.isNotBlank(message.getContent())){
            messageService.save(message);
            return Result.success();
        }else{
            return Result.error("-1","请先填写评论再发送");
        }
    }

    /**
     * 查询全部评论
     * @return messages
     */
    @GetMapping
    public Result<?> findAll() {
        return Result.success(messageService.list(null));
    }

    /**
     * 查询全部评论，并且打包回复到评论中
     * @param foreignId 外键
     * @return messages with parentMsg
     */
    @GetMapping("/foreign/{foreignId}")
    private Result<?> findByForeign(@PathVariable Integer foreignId) {
        // 根据 foreignId 查询所有的留言数据
        LambdaQueryWrapper<Message> queryWrapper = Wrappers.<Message>lambdaQuery().eq(Message::getForeignId, foreignId).orderByDesc(Message::getId);
        List<Message> list = messageService.list(queryWrapper);
        // 循环所有留言数据
        for (Message message : list) {
            int parentId = message.getParentId();
            // 判断当前的留言是否有父级，如果有，则返回父级留言的信息
            // 原理：遍历所有留言数据，如果id跟当前留言信息的parentId相等，则将其设置为父级评论信息，也就是Message::setParentMessage
            list.stream().filter(c -> c.getId().equals(parentId)).findFirst().ifPresent(message::setParentMessage);
        }
        return Result.success(list);
    }

    @DeleteMapping("/delete/{id}")
    public Result<?> deleteComment(@PathVariable int id){
        Message message = messageService.getById(id);
        if (message != null){
            messageService.removeById(id);
            return Result.success();
        }else {
            return Result.error("-1","该信息不存在");
        }
    }
}