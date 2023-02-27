package com.lening.yygh.hosp.api;

import com.lening.yygh.common.exception.YyghException;
import com.lening.yygh.common.helper.HttpRequestHelper;
import com.lening.yygh.common.result.Result;
import com.lening.yygh.common.result.ResultCodeEnum;
import com.lening.yygh.hosp.service.DepartmentService;
import com.lening.yygh.hosp.service.HospitalService;
import com.lening.yygh.hosp.service.HospitalSetService;
import com.lening.yygh.hosp.service.ScheduleService;
import com.lening.yygh.model.hosp.Hospital;
import com.lening.yygh.vo.hosp.HospitalQueryVo;
import com.lening.yygh.vo.hosp.ScheduleOrderVo;
import com.lening.yygh.vo.order.SignInfoVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@Api(tags = "医院管理API接口")
@RestController
@RequestMapping("/admin/hosp/api/hospital")
@CrossOrigin
public class HospApiController {

    /**
     * 怎么把数据传递过来，我们需要写一个接口
     * 从医院接口端把数据传递过来，
     * 医院接口我们已经开发好了，拿过来直接使用
     * 可以单独创建一个项目，也可以在该项目里面创建一个模块，都行
     */
    @Resource
    private HospitalService hospitalService;
    @Resource
    private DepartmentService departmentService;
    @Autowired
    private ScheduleService scheduleService;
    @Autowired
    private HospitalSetService hospitalSetService;

    @GetMapping("/findAll")
    public Result findAll(){
        List<Hospital> list = hospitalService.findAll();
        for (Hospital hospital : list) {
            System.out.println(hospital);
        }
        return Result.ok(list);
    }
    /**
     * 现在我们相当于接口的对接，是不同的平台的来的数据，通过httpcilent来的数据，就是json数据，
     * 我们需要转成java数据进行保存，我们的先接收
     */
    @ApiOperation(value = "上传医院")
    @PostMapping("/saveHospital")
    public Result saveHospital(HttpServletRequest request) {
        /**
         * 这个是医院和平台约定好的数据方式，传递一个map，里面是字符串数据，字符串里面是json，
         * 拿回来之后我们进行转化，转成对象，相当于把数组里面的对象全部拿出来，但是我们只有一个对象
         * 说明：参数使用Map，减少对象封装，有利于签名校验，后续会体验到
         */
        Map<String, String[]> parameterMap = request.getParameterMap();
        //我们自己写的转化类去转化
        Map<String, Object> paramMap = HttpRequestHelper.switchMap(parameterMap);

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

        //这个是处理医院logo的图片的，图片使用了base64进行加密，这个加密是可逆的
        //传输过程中“+”转换为了“ ”，因此我们要转换回来
        String logoDataString = (String)paramMap.get("logoData");
        if(!StringUtils.isEmpty(logoDataString)) {
            String logoData = logoDataString.replaceAll(" ", "+");
            paramMap.put("logoData", logoData);
        }
        hospitalService.save(paramMap);
        return Result.ok();
    }


    @ApiOperation(value = "获取医院信息")
    @PostMapping("/show")
    public Result hospital(HttpServletRequest request) {
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
        return Result.ok(hospitalService.getByHoscode((String)paramMap.get("hoscode")));
    }

    @ApiOperation(value = "获取分页列表")
    @GetMapping("{page}/{limit}")
    public Result index(
            @PathVariable Integer page,
            @PathVariable Integer limit,
            HospitalQueryVo hospitalQueryVo) {
        //显示上线的医院
        //hospitalQueryVo.setStatus(1);
        Page<Hospital> pageModel = hospitalService.selectPage(page, limit, hospitalQueryVo);
        return Result.ok(pageModel);
    }
    @ApiOperation(value = "根据医院名称获取医院列表")
    @GetMapping("findByHosname/{hosname}")
    public Result findByHosname(
            @ApiParam(name = "hosname", value = "医院名称", required = true)
            @PathVariable String hosname) {
        return Result.ok(hospitalService.findByHosname(hosname));
    }


    @ApiOperation(value = "获取科室列表")
    @GetMapping("department/{hoscode}")
    public Result index(
            @ApiParam(name = "hoscode", value = "医院code", required = true)
            @PathVariable String hoscode) {
        return Result.ok(departmentService.findDeptTree(hoscode));
    }

    @ApiOperation(value = "医院预约挂号详情")
    @GetMapping("findHospDetail/{hoscode}")
    public Result item(
            @ApiParam(name = "hoscode", value = "医院code", required = true)
            @PathVariable String hoscode) {
        return Result.ok(hospitalService.item(hoscode));
    }


    @ApiOperation(value = "获取可预约排班数据")
    @GetMapping("auth/getBookingScheduleRule/{page}/{limit}/{hoscode}/{depcode}")
    public Result getBookingSchedule(
            @ApiParam(name = "page", value = "当前页码", required = true)
            @PathVariable Integer page,
            @ApiParam(name = "limit", value = "每页记录数", required = true)
            @PathVariable Integer limit,
            @ApiParam(name = "hoscode", value = "医院code", required = true)
            @PathVariable String hoscode,
            @ApiParam(name = "depcode", value = "科室code", required = true)
            @PathVariable String depcode) {
        return Result.ok(scheduleService.getBookingScheduleRule(page, limit, hoscode, depcode));
    }

    @ApiOperation(value = "获取排班数据")
    @GetMapping("auth/findScheduleList/{hoscode}/{depcode}/{workDate}")
    public Result findScheduleList(
            @ApiParam(name = "hoscode", value = "医院code", required = true)
            @PathVariable String hoscode,
            @ApiParam(name = "depcode", value = "科室code", required = true)
            @PathVariable String depcode,
            @ApiParam(name = "workDate", value = "排班日期", required = true)
            @PathVariable String workDate) {
        return Result.ok(scheduleService.getDetailSchedule(hoscode, depcode, workDate));
    }
    @ApiOperation(value = "根据排班id获取排班数据")
    @GetMapping("getSchedule/{scheduleId}")
    public Result getSchedule(
            @ApiParam(name = "scheduleId", value = "排班id", required = true)
            @PathVariable String scheduleId) {
        return Result.ok(scheduleService.getById(scheduleId));
    }

    @ApiOperation(value = "根据排班id获取预约下单数据")
    @GetMapping("inner/getScheduleOrderVo/{scheduleId}")
    public ScheduleOrderVo getScheduleOrderVo(
            @ApiParam(name = "scheduleId", value = "排班id", required = true)
            @PathVariable("scheduleId") String scheduleId) {
        return scheduleService.getScheduleOrderVo(scheduleId);
    }
    @ApiOperation(value = "获取医院签名信息")
    @GetMapping("inner/getSignInfoVo/{hoscode}")
    public SignInfoVo getSignInfoVo(
            @ApiParam(name = "hoscode", value = "医院code", required = true)
            @PathVariable("hoscode") String hoscode) {
        return hospitalSetService.getSignInfoVo(hoscode);
    }



}
