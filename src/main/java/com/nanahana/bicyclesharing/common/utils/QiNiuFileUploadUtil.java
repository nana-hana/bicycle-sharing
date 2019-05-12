package com.nanahana.bicyclesharing.common.utils;

import com.google.gson.Gson;
import com.nanahana.bicyclesharing.common.constant.Constant;
import com.qiniu.common.Zone;
import com.qiniu.http.Response;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.UploadManager;
import com.qiniu.storage.model.DefaultPutRet;
import com.qiniu.util.Auth;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

import static com.nanahana.bicyclesharing.common.constant.Constant.QINIU_ACCESS_KEY;
import static com.nanahana.bicyclesharing.common.constant.Constant.QINIU_SECRET_KEY;

/**
 * @Author nana
 * @Date 2019/5/11 20:13
 * @Description 七牛文件上传工具
 */
public class QiNiuFileUploadUtil {
    /**
     * 向七牛上传需要上传的文件
     *
     * @param file 需要上传的文件
     * @return 返回上传成功的文件哈希值
     * @throws IOException io异常
     */
    public static String uploadHeadImg(MultipartFile file) throws IOException {
        Configuration cfg = new Configuration(Zone.zone2());
        UploadManager uploadManager = new UploadManager(cfg);
        Auth auth = Auth.create(QINIU_ACCESS_KEY, QINIU_SECRET_KEY);
        String upToken = auth.uploadToken(Constant.QINIU_HEAD_IMG_BUCKET_NAME);
        Response response = uploadManager.put(file.getBytes(), null, upToken);
        DefaultPutRet putRet = new Gson().fromJson(response.bodyString(), DefaultPutRet.class);
        return putRet.key;
    }
//    public static void main(String[] args) throws IOException {
//        uploadHeadImg(new File("C:\\Users\\Administrator\\Desktop\\FncDEhwErCW8DdZOIAY0cz-sc1TV"),
//            "FncDEhwErCW8DdZOIAY0cz-sc1TV");
//    }
}
