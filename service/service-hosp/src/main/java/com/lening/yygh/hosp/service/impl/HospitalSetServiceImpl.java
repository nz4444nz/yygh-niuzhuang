package com.lening.yygh.hosp.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lening.yygh.common.exception.YyghException;
import com.lening.yygh.common.result.ResultCodeEnum;
import com.lening.yygh.hosp.mapper.HospitalSetMapper;
import com.lening.yygh.hosp.service.HospitalSetService;
import com.lening.yygh.model.hosp.HospitalSet;
import com.lening.yygh.vo.hosp.HospitalSetQueryVo;
import com.lening.yygh.vo.order.SignInfoVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;

@Service
public class HospitalSetServiceImpl extends ServiceImpl<HospitalSetMapper, HospitalSet> implements HospitalSetService {

    @Resource
    private HospitalSetMapper hospitalSetMapper;

    @Override
    public Page<HospitalSet> findAllConnAndPage(long current, long limit, HospitalSetQueryVo hospitalSetQueryVo) {
        //创建page对象，传递当前页，每页记录数
        Page<HospitalSet> page = new Page<>(current,limit);
        //构建条件
        QueryWrapper<HospitalSet> wrapper = new QueryWrapper<>();
        if(hospitalSetQueryVo!=null){
            String hosname = hospitalSetQueryVo.getHosname();//医院名称
            String hoscode = hospitalSetQueryVo.getHoscode();//医院编号
            if(!StringUtils.isEmpty(hosname)) {
                wrapper.like("hosname",hospitalSetQueryVo.getHosname());
            }
            if(!StringUtils.isEmpty(hoscode)) {
                wrapper.eq("hoscode",hospitalSetQueryVo.getHoscode());
            }
        }
        //调用方法实现分页查询
        Page<HospitalSet> hospitalSetPage = hospitalSetMapper.selectPage(page, wrapper);
        return hospitalSetPage;
    }
    //获取医院签名信息
    @Override
    public SignInfoVo getSignInfoVo(String hoscode) {
        QueryWrapper<HospitalSet> wrapper = new QueryWrapper<>();
        wrapper.eq("hoscode",hoscode);
        HospitalSet hospitalSet = baseMapper.selectOne(wrapper);
        if(null == hospitalSet) {
            throw new YyghException(ResultCodeEnum.HOSPITAL_OPEN);
        }
        SignInfoVo signInfoVo = new SignInfoVo();
        signInfoVo.setApiUrl(hospitalSet.getApiUrl());
        signInfoVo.setSignKey(hospitalSet.getSignKey());
        return signInfoVo;
    }



}
