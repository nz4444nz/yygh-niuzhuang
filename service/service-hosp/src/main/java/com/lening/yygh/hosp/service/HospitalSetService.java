package com.lening.yygh.hosp.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.lening.yygh.model.hosp.HospitalSet;
import com.lening.yygh.vo.hosp.HospitalSetQueryVo;
import com.lening.yygh.vo.order.SignInfoVo;

public interface HospitalSetService extends IService<HospitalSet> {
    Page<HospitalSet> findAllConnAndPage(long current, long limit, HospitalSetQueryVo hospitalSetQueryVo);

    //获取医院签名信息
    SignInfoVo getSignInfoVo(String hoscode);

}
