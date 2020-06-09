package com.changgou.controller;


import com.changgou.entity.Result;
import com.changgou.entity.StatusCode;
import com.changgou.util.FastDFSClient;
import com.changgou.util.FastDFSFile;
import org.apache.commons.lang.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/file")
public class FileController {

    @PostMapping
    public Result fileUpload(MultipartFile file){
        try {
            String filename = file.getOriginalFilename();
            if (StringUtils.isEmpty(filename)){
                throw new RuntimeException("文件不存在");
            }
            String ext = filename.substring(filename.lastIndexOf("."));
            byte[] contents = file.getBytes();
            FastDFSFile fastDFSFile = new FastDFSFile(filename,contents,ext);
            String[] path = FastDFSClient.upload(fastDFSFile);
            String url = FastDFSClient.getTrackerUrl()+path[0]+"/"+path[1];
            return new Result(true,StatusCode.OK,"上传文件成功",path);

        } catch (IOException e) {
            e.printStackTrace();
            return new Result(false, StatusCode.ERROR,"上传文件失败");
        }

    }
}
