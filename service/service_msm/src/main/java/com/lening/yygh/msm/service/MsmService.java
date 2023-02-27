package com.lening.yygh.msm.service;

import com.lening.yygh.vo.msm.MsmVo;

public interface MsmService {
    //发送验证码
    boolean send(String phone, String code);

    //mq使用发送短信
    boolean send(MsmVo msmVo);
}
