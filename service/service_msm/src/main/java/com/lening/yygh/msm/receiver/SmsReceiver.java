package com.lening.yygh.msm.receiver;

import com.lening.common.rabbit.constant.MqConst;
import com.lening.yygh.msm.service.MsmService;
import com.lening.yygh.vo.msm.MsmVo;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SmsReceiver {
    @Autowired
    private MsmService msmService;
    //接收mq的消息是否调用send方法
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = MqConst.QUEUE_MSM_ITEM, durable = "true"),
            exchange = @Exchange(value = MqConst.EXCHANGE_DIRECT_MSM),
            key = {MqConst.ROUTING_MSM_ITEM}
    ))
    //只要Rabbitservice（参数）只要参数有MqConst.QUEUE_MSM_ITEM，MqConst.EXCHANGE_DIRECT_MSM，MqConst.ROUTING_MSM_ITEM这个方法就会执行
    public void send(MsmVo msmVo, Message message, Channel channel) {
        msmService.send(msmVo);
    }

}

