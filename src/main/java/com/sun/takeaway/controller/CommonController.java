package com.sun.takeaway.controller;

import cn.hutool.core.lang.UUID;
import com.sun.takeaway.common.CommonResult;
import com.sun.takeaway.common.ErrorCode;
import com.sun.takeaway.exception.BusinessException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * @author sun
 */
@RestController
@RequestMapping("/common")
public class CommonController {

    @Value("${upload.basePath}")
    private String basePath;

    /**
     * 上传（只需要在 Controller 中声明一个 MultipartFie 类型的参数，即可接收到上传的文件。）
     */
    @PostMapping("/upload")
    public CommonResult<String> upload(@RequestPart("file") MultipartFile file) {
        String suffix = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));
        String fileName = UUID.fastUUID().toString(true) + suffix;
        File ultimateFile = new File(basePath + fileName);

        File basePath = new File(this.basePath);
        if (!basePath.exists()) {
            basePath.mkdirs();
        }
        try {
            // file 是一个临时文件，需要转存到指定位置，否则本次请求完成后会将该临时文件删除。
            // file.transferTo(ultimateFile) 方法就是将该临时文件转存到指定 ultimateFile 位置。
            file.transferTo(ultimateFile);
        } catch (IOException e) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR);
        }
        return CommonResult.success(fileName);
    }

    /**
     * 下载（下载本质上就是：将文件以流的形式写回给 Client 的过程。）
     */
    @GetMapping("/download")
    public void download(@RequestParam("name") String name, HttpServletResponse response) {
        response.setContentType("image/jpeg");
        try {
            FileInputStream in = new FileInputStream(basePath + name);
            ServletOutputStream out = response.getOutputStream();
            int readLen;
            byte[] buf = new byte[1024];
            while ((readLen = in.read(buf)) != -1) {
                out.write(buf, 0, readLen);
                out.flush();
            }
            in.close();
            out.close();
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR);
        }
    }
}
