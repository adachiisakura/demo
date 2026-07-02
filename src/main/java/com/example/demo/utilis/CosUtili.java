package com.example.demo.utilis;

import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.auth.BasicCOSCredentials;
import com.qcloud.cos.auth.COSCredentials;
import com.qcloud.cos.http.HttpProtocol;
import com.qcloud.cos.model.ObjectMetadata;
import com.qcloud.cos.model.PutObjectResult;
import com.qcloud.cos.region.Region;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

@Data
@AllArgsConstructor
public class CosUtili {
    private String secretId;
    private String secretKey;
    private String region;
    private String bucketname;
    private String rootSrc;

    public String upload(MultipartFile file) throws IOException {
        COSCredentials cred = new BasicCOSCredentials(secretId,secretKey);
        Region region = new Region(this.region);
        ClientConfig clientConfig = new ClientConfig(region);
        clientConfig.setHttpProtocol(HttpProtocol.https);
        COSClient cosClient = new COSClient(cred, clientConfig);

        // 获取上传的文件的输入流
        InputStream inputStream = file.getInputStream();

        // 避免文件覆盖，获取文件的原始名称，如123.jpg,然后通过截取获得文件的后缀，也就是文件的类型
        String originalFilename = file.getOriginalFilename();
        System.out.println(originalFilename);
        //获取文件的类型
        String fileType = originalFilename.substring(originalFilename.lastIndexOf("."));
        //使用UUID工具  创建唯一名称，放置文件重名被覆盖，在拼接上上命令获取的文件类型
        String fileName = UUID.randomUUID().toString() + fileType;
        // 指定文件上传到 COS 上的路径，即对象键。最终文件会传到存储桶名字中的images文件夹下的fileName名字
        String key = rootSrc + fileName;
        // 创建上传Object的Metadata
        ObjectMetadata objectMetadata = new ObjectMetadata();
        // - 使用输入流存储，需要设置请求长度
        objectMetadata.setContentLength(inputStream.available());
        // - 设置缓存
        objectMetadata.setCacheControl("no-cache");
        // - 设置Content-Type
        objectMetadata.setContentType(fileType);

        PutObjectResult putObjectResult = cosClient.putObject(bucketname, key,inputStream,objectMetadata);

        cosClient.shutdown();
        // 创建文件的网络访问路径
        return key;
    }

    public void deleteObject(String key) {
        COSCredentials cred = new BasicCOSCredentials(secretId, secretKey);
        Region region = new Region(this.region);
        ClientConfig clientConfig = new ClientConfig(region);
        clientConfig.setHttpProtocol(HttpProtocol.https);
        COSClient cosClient = new COSClient(cred, clientConfig);
        cosClient.deleteObject(bucketname, key);
        cosClient.shutdown();
    }
}
