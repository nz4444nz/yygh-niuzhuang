package com.lening.yygh.order.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lening.yygh.model.order.OrderInfo;
import com.lening.yygh.vo.order.OrderCountQueryVo;
import com.lening.yygh.vo.order.OrderCountVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
@Mapper
public interface OrderMapper extends BaseMapper<OrderInfo> {
    List<OrderCountVo> selectOrderCount(@Param("vo") OrderCountQueryVo orderCountQueryVo);
}

