package com.lening.yygh.controller;

import com.lening.yygh.common.result.Result;
import com.lening.yygh.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/oss/file")
@CrossOrigin
public class FileApiController {
    @Autowired
    private FileService fileService;
    //上传文件到阿里云oss
    @PostMapping("fileUpload")
    public Result fileUpload(MultipartFile file) {
        //获取上传文件
        String url = fileService.upload(file);
        return Result.ok(url);
    }
}
