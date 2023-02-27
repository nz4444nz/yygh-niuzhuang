package com.lening.yygh.hosp.service;

import com.lening.yygh.model.hosp.Hospital;
import com.lening.yygh.vo.hosp.HospitalQueryVo;
import org.springframework.data.domain.Page;


import java.util.List;
import java.util.Map;

public interface HospitalService {
    List<Hospital> findAll();
    /**
     * 上传医院信息
     * @param paramMap
     */
    void save(Map<String, Object> paramMap);

    Hospital getByHoscode(String hoscode);

    String getMySignByHoscode(String hoscode);

    /**
     * 分页查询
     * @param page 当前页码
     * @param limit 每页记录数
     * @param hospitalQueryVo 查询条件
     * @return
     */
    Page<Hospital> selectPage(Integer page, Integer limit, HospitalQueryVo hospitalQueryVo);


    void updateStatus(String id, Integer status);

    Map<String, Object> show(String id);


    /**
     * 根据医院编号获取医院名称接口
     * @param hoscode
     * @return
     */
    String getName(String hoscode);

    List<Hospital> findByHosname(String hosname);

    Map<String, Object> item(String hoscode);






















}
