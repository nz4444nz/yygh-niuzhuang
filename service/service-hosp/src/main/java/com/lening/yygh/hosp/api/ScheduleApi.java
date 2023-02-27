package com.lening.yygh.hosp.api;


import com.lening.yygh.common.exception.YyghException;
import com.lening.yygh.common.helper.HttpRequestHelper;
import com.lening.yygh.common.result.Result;
import com.lening.yygh.common.result.ResultCodeEnum;
import com.lening.yygh.hosp.service.HospitalService;
import com.lening.yygh.hosp.service.ScheduleService;
import com.lening.yygh.model.hosp.Schedule;
import com.lening.yygh.vo.hosp.ScheduleQueryVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.data.domain.Page;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@RestController
@Api(tags = "排班接口")
@RequestMapping("/admin/hosp/api/schedule")
public class ScheduleApi {
    @Resource
    private ScheduleService scheduleService;
    @Resource
    private HospitalService hospitalService;

    @ApiOperation(value = "删除排班")
    @PostMapping("/remove")
    public Result remove(HttpServletRequest request) {
        Map<String, Object> paramMap = HttpRequestHelper.switchMap(request.getParameterMap());

        String hosScheduleId = (String)paramMap.get("hosScheduleId");
        //------签名开始了--------
        String hoscode = (String) paramMap.get("hoscode");
        String sign = (String) paramMap.get("sign");
        //要是医院编码为空，就抛出一个自定义异常
        if(StringUtils.isEmpty(hoscode)||StringUtils.isEmpty(sign)) {
            throw new YyghException(ResultCodeEnum.PARAM_ERROR);
        }
        String mySign = hospitalService.getMySignByHoscode(hoscode);
        if(!sign.equals(mySign)){
            throw new YyghException(ResultCodeEnum.SIGN_ERROR);
        }
        //------签名结束了-------------
        scheduleService.remove(hoscode,hosScheduleId);
        return Result.ok();
    }



    @ApiOperation(value = "上传排班")
    @PostMapping("/saveSchedule")
    public Result saveSchedule(HttpServletRequest request) {
        Map<String, Object> paramMap = HttpRequestHelper.switchMap(request.getParameterMap());

        //------签名开始了--------
        String hoscode = (String) paramMap.get("hoscode");
        String sign = (String) paramMap.get("sign");
        //要是医院编码为空，就抛出一个自定义异常
        if(StringUtils.isEmpty(hoscode)||StringUtils.isEmpty(sign)) {
            throw new YyghException(ResultCodeEnum.PARAM_ERROR);
        }
        String mySign = hospitalService.getMySignByHoscode(hoscode);
        if(!sign.equals(mySign)){
            throw new YyghException(ResultCodeEnum.SIGN_ERROR);
        }
        //------签名结束了-------------

        scheduleService.save(paramMap);
        return Result.ok();
    }

    @ApiOperation(value = "获取排班分页列表")
    @PostMapping("/list")
    public Result schedule(HttpServletRequest request) {
        Map<String, Object> paramMap = HttpRequestHelper.switchMap(request.getParameterMap());
        //必须参数校验 略

        //非必填
        String depcode = (String)paramMap.get("depcode");

        //------签名开始了--------
        String hoscode = (String) paramMap.get("hoscode");
        String sign = (String) paramMap.get("sign");
        //要是医院编码为空，就抛出一个自定义异常
        if(StringUtils.isEmpty(hoscode)||StringUtils.isEmpty(sign)) {
            throw new YyghException(ResultCodeEnum.PARAM_ERROR);
        }
        String mySign = hospitalService.getMySignByHoscode(hoscode);
        if(!sign.equals(mySign)){
            throw new YyghException(ResultCodeEnum.SIGN_ERROR);
        }
        //------签名结束了-------------



        //这是对页面和大小的初始化处理
        int page = StringUtils.isEmpty(paramMap.get("page")) ? 1 : Integer.parseInt((String)paramMap.get("page"));
        int limit = StringUtils.isEmpty(paramMap.get("limit")) ? 10 : Integer.parseInt((String)paramMap.get("limit"));
        //查询的是医院的科室下面的排班，需要医院编号和科室编号
        ScheduleQueryVo scheduleQueryVo = new ScheduleQueryVo();
        scheduleQueryVo.setHoscode(hoscode);
        scheduleQueryVo.setDepcode(depcode);
        //这个page是spring-data的，jap和MongoDB都是使用他的
        Page<Schedule> pageModel = scheduleService.selectPage(page , limit, scheduleQueryVo);
        return Result.ok(pageModel);
    }
}
