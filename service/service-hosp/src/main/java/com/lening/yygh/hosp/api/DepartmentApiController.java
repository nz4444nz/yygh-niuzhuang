package com.lening.yygh.hosp.api;


import com.lening.yygh.common.exception.YyghException;
import com.lening.yygh.common.helper.HttpRequestHelper;
import com.lening.yygh.common.result.Result;
import com.lening.yygh.common.result.ResultCodeEnum;
import com.lening.yygh.hosp.service.DepartmentService;
import com.lening.yygh.hosp.service.HospitalService;
import com.lening.yygh.model.hosp.Department;
import com.lening.yygh.vo.hosp.DepartmentQueryVo;
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

@Api(tags = "医院管理API接口-部门")
@RestController
@RequestMapping("/admin/hosp/api/department")
public class DepartmentApiController {
    @Resource
    private DepartmentService departmentService;
    @Resource
    private HospitalService hospitalService;

    @ApiOperation(value = "上传科室")
    @PostMapping("saveDepartment")
    public Result saveDepartment(HttpServletRequest request) {
        Map<String, Object> paramMap = HttpRequestHelper.switchMap(request.getParameterMap());
//签名校验

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
        departmentService.save(paramMap);
        return Result.ok();
    }


    @ApiOperation(value = "获取分页列表")
    @PostMapping("list")
    public Result department(HttpServletRequest request) {
        Map<String, Object> paramMap = HttpRequestHelper.switchMap(request.getParameterMap());
//必须参数校验 略
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
        int page = StringUtils.isEmpty(paramMap.get("page")) ? 1 : Integer.parseInt((String)paramMap.get("page"));
        int limit = StringUtils.isEmpty(paramMap.get("limit")) ? 10 : Integer.parseInt((String)paramMap.get("limit"));

        if(StringUtils.isEmpty(hoscode)) {
            throw new YyghException(ResultCodeEnum.PARAM_ERROR);
        }
//签名校验
//        if(!HttpRequestHelper.isSignEquals(paramMap, departmentService.getSignKey(hoscode))) {
//            throw new YyghException(ResultCodeEnum.SIGN_ERROR);
//        }

        DepartmentQueryVo departmentQueryVo = new DepartmentQueryVo();
        departmentQueryVo.setHoscode(hoscode);
        departmentQueryVo.setDepcode(depcode);
        Page<Department> pageModel = departmentService.selectPage(page, limit, departmentQueryVo);
        return Result.ok(pageModel);
    }

    @ApiOperation(value = "删除科室")
    @PostMapping("remove")
    public Result removeDepartment(HttpServletRequest request) {
        Map<String, Object> paramMap = HttpRequestHelper.switchMap(request.getParameterMap());
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

        departmentService.remove(hoscode, depcode);
        return Result.ok();
    }



}
