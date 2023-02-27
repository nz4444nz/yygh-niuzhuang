package com.lening.yygh.hosp.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lening.yygh.model.hosp.HospitalSet;
import org.apache.ibatis.annotations.Mapper;

@Mapper//mybatis的基础，放假前我们讲的
public interface HospitalSetMapper extends BaseMapper<HospitalSet> {
}

