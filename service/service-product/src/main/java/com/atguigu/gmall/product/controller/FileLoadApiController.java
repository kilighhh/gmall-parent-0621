package com.atguigu.gmall.product.controller;

import com.atguigu.gamll.common.result.Result;
import com.atguigu.gmall.model.product.SpuImage;
import com.atguigu.gmall.model.product.SpuInfo;
import com.atguigu.gmall.product.service.SupInfoService;
import com.atguigu.gmall.product.test.TestFdfs;
import lombok.SneakyThrows;
import org.csource.fastdfs.ClientGlobal;
import org.csource.fastdfs.StorageClient;
import org.csource.fastdfs.TrackerClient;
import org.csource.fastdfs.TrackerServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * @Author Kilig Zong
 * @Date 2020/12/1 10:57
 * @Version 1.0
 */
@RestController
@RequestMapping("admin/product/")
@CrossOrigin
public class FileLoadApiController {
    @Value("${fileUrl}")
    private String fileUrl;
    /***
     * @author Kilig Zong
     * @date 2020/12/1 11:14
     * @description 上传照片的接口
     * @param file
     * @return com.atguigu.gamll.common.result.Result
     **/
    @SneakyThrows
    @RequestMapping("fileUpload")
    public Result fileUpload(@RequestParam("file") MultipartFile file){
        //配置fdfs的全局连接地址
        String path = TestFdfs.class.getClassLoader().getResource("tracker.conf").getPath();
        System.out.println(path);
        ClientGlobal.init(path);
        TrackerClient trackerClient = new TrackerClient();
        //连接tracker
        TrackerServer tracker = trackerClient.getConnection();
        //连接storage
        StorageClient storageClient = new StorageClient(tracker,null);
        //获取文件名

        //获取文件拓展名,就是文件后缀
        String file_ext_name= StringUtils.getFilenameExtension(file.getOriginalFilename());
        //上传文件
        String[] uploadInfos = storageClient.upload_file(file.getBytes(), file_ext_name, null);
        String url=fileUrl;

        for (String uploadInfo : uploadInfos){
            url+="/"+uploadInfo;

        }
        System.out.println(url);
        //返回url
        return Result.ok(url);
    }
}
