package com.lening.yygh.hosp.service;

import com.lening.yygh.model.hosp.Department;
import com.lening.yygh.vo.hosp.DepartmentQueryVo;
import com.lening.yygh.vo.hosp.DepartmentVo;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Map;

public interface DepartmentService {
    Page<Department> selectPage(Integer page, Integer limit, DepartmentQueryVo departmentQueryVo);

    void save(Map<String, Object> paramMap);

    void remove(String hoscode, String depcode);

    List<DepartmentVo> findDeptTree(String hoscode);
    //根据科室编号，和医院编号，查询科室名称
    String getDepName(String hoscode, String depcode);

    Department getDepartment(String hoscode, String depcode);
}
