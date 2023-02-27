package com.lening.yygh.cmn.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lening.yygh.model.cmn.Dict;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

public interface DictService extends IService<Dict> {
    List<Dict> findChlidData(Long id);

    void exportData(HttpServletResponse response);

    void importData(MultipartFile file);
    /**
     * 根据上级编码与值获取数据字典名称
     * @param dictCode
     * @param value
     * @return
     */
    String getNameByParentDictCodeAndValue(String dictCode, String value);

    List<Dict> findByDictCode(String dictCode);
}
