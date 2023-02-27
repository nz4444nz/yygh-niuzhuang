package com.lening.yygh.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lening.yygh.model.order.PaymentInfo;
import com.lening.yygh.model.order.RefundInfo;

public interface RefundInfoService extends IService<RefundInfo> {
    /**
     * 保存退款记录
     * @param paymentInfo
     */
    RefundInfo saveRefundInfo(PaymentInfo paymentInfo);
}

