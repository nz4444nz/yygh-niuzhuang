package com.lening.yygh.hosp.mapper;

import com.lening.yygh.model.hosp.Department;
import com.lening.yygh.model.hosp.Hospital;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface DepartmentRepository extends MongoRepository<Department,String> {
    Department getDepartmentByHoscodeAndDepcode(String hoscode, String depcode);
}
