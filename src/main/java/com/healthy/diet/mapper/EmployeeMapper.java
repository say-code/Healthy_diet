package com.healthy.diet.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.healthy.diet.entity.Employee;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface EmployeeMapper extends BaseMapper<Employee> {
}
