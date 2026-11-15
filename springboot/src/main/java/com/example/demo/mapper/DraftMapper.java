package com.example.demo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.demo.entity.Draft;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

public interface DraftMapper extends BaseMapper<Draft> {
    @Update("UPDATE draft SET title=#{title}, content=#{content}, tags=#{tags} WHERE user_id=#{user_id}")
    Integer updateByUserId(@Param("title") String title, @Param("content") String content,
                           @Param("tags") String tags, @Param("user_id") Integer userId);

    @Select("select * from draft where user_id = #{user_id}")
    Draft selectByUserId(@Param("user_id")Integer id);
}
