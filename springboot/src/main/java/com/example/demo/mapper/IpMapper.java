package com.example.demo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.demo.entity.RBAC.Ip;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

public interface IpMapper extends BaseMapper<Ip> {

    /**
     * 根据用户id封禁ip
     * @param user_id
     * @param ban
     * @return rst
     */
    @Update({ "update ip set ban = #{ban} where user_id = #{user_id}" })
    int updateByUserid(@Param("user_id")Integer user_id, @Param("ban") Integer ban);

    /**
     * 查询ip是否被封禁过
     * @param ip
     * @return ips
     */
    @Select({"select ban from ip where ip = #{ip}"})
    List<Integer> searchBan(@Param("ip")String ip);
}