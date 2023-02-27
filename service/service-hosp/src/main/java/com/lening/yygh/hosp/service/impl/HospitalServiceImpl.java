package com.lening.yygh.hosp.service.impl;


import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lening.yygh.cmn.DictFeignClient;
import com.lening.yygh.common.exception.YyghException;
import com.lening.yygh.common.result.ResultCodeEnum;
import com.lening.yygh.common.utils.MD5;
import com.lening.yygh.enums.DictEnum;
import com.lening.yygh.hosp.mapper.HospitalRepository;
import com.lening.yygh.hosp.mapper.HospitalSetMapper;
import com.lening.yygh.hosp.mapper.OrderInfoMapper;
import com.lening.yygh.hosp.mapper.ScheduleMapper;
import com.lening.yygh.hosp.service.HospitalService;
import com.lening.yygh.model.hosp.Hospital;
import com.lening.yygh.model.hosp.HospitalSet;
import com.lening.yygh.model.hosp.Schedule;
import com.lening.yygh.model.order.OrderInfo;
import com.lening.yygh.model.user.Patient;
import com.lening.yygh.vo.hosp.HospitalQueryVo;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 这个类我们是用来操作MongoDB的，不是mybatis-plus的，所以不需要继承ServiceIMPL那个类
 */
@Service
public class HospitalServiceImpl implements HospitalService {

    @Autowired
    private HospitalRepository hospitalRepository;
    @Autowired
    private HospitalSetMapper hospitalSetMapper;
    @Autowired
    private DictFeignClient dictFeignClient;

    @Autowired
    private ScheduleMapper scheduleMapper;

    @Autowired
    private OrderInfoMapper orderInfoMapper;
    @Override
    public List<Hospital> findAll() {
        return hospitalRepository.findAll();
    }


    @Override
    public void save(Map<String, Object> paramMap) {
        /*
            这一句就是把传递过来的json字符串转成我们java的医院信息
            JSONObject.parseObject这方法是把json转成java对象的，
            相当于和我们平时把java转成json的逆向操作
            在转化的过程中，因为json对象里面还套有一个集合，所以一次性是转不过来的
            要对集合进行二次处理，在他的set方法里面就可以处理了。
         */
        Hospital hospital = JSONObject.parseObject(JSONObject.toJSONString(paramMap),Hospital.class);
        /**
         * 开始保存，保存之后，先去使用医院的编号去查询，要是医院已经有了，那就是更新
         * 要是以前没有上传过信息的，就是新增，
         * 这个不是主键查询，所以我们需要去了解MongoDB的条件查询方式，
         * 和jap几乎一样可以直接在接口里面按照他的规则去书写
         */
        //判断是否存在
        Hospital targetHospital = hospitalRepository.getHospitalByHoscode(hospital.getHoscode());
        if(null != targetHospital) {
            //以前有，修改即可，把原来的状态给新的对象，是否上线用原来
            hospital.setStatus(targetHospital.getStatus());
            //医院在平台的创建时间
            hospital.setCreateTime(targetHospital.getCreateTime());
            //更新时间肯定最新的
            hospital.setUpdateTime(new Date());
            //是否删除，更新信息，肯定是没有删除，就算删除了，也是逻辑删除，这次就要改过来
            hospital.setIsDeleted(0);
            hospitalRepository.save(hospital);
        } else {
            //就是新增了
            //0：未上线 1：已上线
            hospital.setStatus(0);
            //为什么这两个时间还需要自己new，不能自动填充呢
            //这里是MongoDB的，不是mybatis-plus
            hospital.setCreateTime(new Date());
            hospital.setUpdateTime(new Date());
            hospital.setIsDeleted(0);
            hospitalRepository.save(hospital);
        }
    }

    @Override
    public Hospital getByHoscode(String hoscode) {
        return hospitalRepository.getHospitalByHoscode(hoscode);
    }


    @Override
    public String getMySignByHoscode(String hoscode) {
        /**
         * 这个查询是从MongoDB里面查询出来的，我们发现它里面竟然没有sign签名的，
         * 签名的key在平台端，到底在哪里？在关系型数据库，mysql里面，所以需要去myusql中查询
         * Hospital hospital = hospitalRepository.getHospitalByHoscode(hoscode);
         */
        /**
         * 因为医院编号是不是主键，所以需要条件查询
         */
        QueryWrapper<HospitalSet> wrapper = new QueryWrapper<HospitalSet>();
        wrapper.eq("hoscode", hoscode);
        HospitalSet hospitalSet = hospitalSetMapper.selectOne(wrapper);
        System.out.println(hospitalSet);
        if(hospitalSet!=null){
            return MD5.encrypt(hospitalSet.getSignKey());
        }
        return null;
    }

    @Override
    public Page<Hospital> selectPage(Integer page, Integer limit, HospitalQueryVo hospitalQueryVo) {
        Sort sort = Sort.by(Sort.Direction.DESC, "createTime");
//0为第一页
        Pageable pageable = PageRequest.of(page-1, limit, sort);

        Hospital hospital = new Hospital();
        BeanUtils.copyProperties(hospitalQueryVo, hospital);

//创建匹配器，即如何使用查询条件
        ExampleMatcher matcher = ExampleMatcher.matching() //构建对象
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING) //改变默认字符串匹配方式：模糊查询
                .withIgnoreCase(true); //改变默认大小写忽略方式：忽略大小写

        //创建实例
        Example<Hospital> example = Example.of(hospital, matcher);
        Page<Hospital> pages = hospitalRepository.findAll(example, pageable);
        pages.getContent().stream().forEach(item -> {
            this.packHospital(item);
        });

        return pages;
    }

    @Override
    public void updateStatus(String id, Integer status) {
        if(status.intValue() == 0 || status.intValue() == 1) {
            Hospital hospital = hospitalRepository.findById(id).get();
            hospital.setStatus(status);
            hospital.setUpdateTime(new Date());
            hospitalRepository.save(hospital);
        }
    }

    @Override
    public Map<String, Object> show(String id) {
        Map<String, Object> result = new HashMap<>();
        Hospital hospital = this.packHospital(hospitalRepository.findById(id).get());
        result.put("hospital", hospital);

//单独处理更直观
        result.put("bookingRule", hospital.getBookingRule());
//不需要重复返回
        hospital.setBookingRule(null);
        return result;

    }

    @Override
    public String getName(String hoscode) {
        Hospital hospital = hospitalRepository.getHospitalByHoscode(hoscode);
        if(null != hospital) {
            return hospital.getHosname();
        }
        return "";
    }

    @Override
    public List<Hospital> findByHosname(String hosname) {
        return hospitalRepository.findHospitalByHosnameLike(hosname);
    }


    /**
     * 封装数据
     * @param hospital
     * @return
     */
    private Hospital packHospital(Hospital hospital) {
        String hostypeString = dictFeignClient.getName(DictEnum.HOSTYPE.getDictCode(),hospital.getHostype());
        String provinceString = dictFeignClient.getName(hospital.getProvinceCode());
        String cityString = dictFeignClient.getName(hospital.getCityCode());
        String districtString = dictFeignClient.getName(hospital.getDistrictCode());

        hospital.getParam().put("hostypeString", hostypeString);
        hospital.getParam().put("fullAddress", provinceString + cityString + districtString + hospital.getAddress());
        return hospital;
    }

    @Override
    public Map<String, Object> item(String hoscode) {
        Map<String, Object> result = new HashMap<>();
        //医院详情
        Hospital hospital = this.packHospital(this.getByHoscode(hoscode));
        result.put("hospital", hospital);
        //预约规则
        result.put("bookingRule", hospital.getBookingRule());
        //不需要重复返回
        hospital.setBookingRule(null);
        return result;
    }




































    }



