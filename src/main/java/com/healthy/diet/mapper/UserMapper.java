package com.healthy.diet.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.healthy.diet.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {
}
