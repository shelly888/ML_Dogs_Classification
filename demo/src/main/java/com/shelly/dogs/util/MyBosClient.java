package com.shelly.dogs.util;

import android.util.Log;

import com.baidubce.auth.DefaultBceCredentials;
import com.baidubce.services.bos.BosClient;
import com.baidubce.services.bos.BosClientConfiguration;
import com.baidubce.services.bos.model.BosObject;
import com.baidubce.services.bos.model.BosObjectSummary;
import com.baidubce.services.bos.model.CreateBucketResponse;
import com.baidubce.services.bos.model.ListObjectsResponse;
import com.baidubce.services.bos.model.ObjectMetadata;
import com.baidubce.services.bos.model.PutObjectResponse;
import com.baidubce.util.BLog;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class MyBosClient {
    private static final String BUCKET_NAME = "shelly-dog-classification";
    private static final String ACCESS_KEY_ID = "7cdaf1754569417f89c83a333aa250e8";
    private static final String SECRET_ACCESS_KEY = "5fbf9f65c3ee46dca6a0713a23bcc0ef";
//    private static final String END_POINT = "shelly-dog-classification.gz.bcebos.com";
    private static final String END_POINT = "http://gz.bcebos.com";

    BosClient client;
    private MyBosClient() {
        BLog.enableLog();
        BosClientConfiguration config = new BosClientConfiguration();
        config.setCredentials(new DefaultBceCredentials(ACCESS_KEY_ID, SECRET_ACCESS_KEY));   //您的AK/SK
        config.setEndpoint(END_POINT);    //传入Bucket所在区域域名
        client = new BosClient(config);

    }
    public static MyBosClient getBosClient() {
        return new MyBosClient();
    }

    public void sendToBos(String p) {
        Log.d("xie", "sendToBos........");
        final String path = p;
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    //创建Bucket
                    Log.d("xie", "..............");
                     CreateBucketResponse response = client.createBucket(BUCKET_NAME); //新建一个Bucket并指定Bucket名称
                    Log.d("xie", "name = " + response.getName());
                    System.out.println(response.getLocation());
                    System.out.println(response.getName());

                    String key = null;

                    //查看Object
                    ListObjectsResponse list = client.listObjects(BUCKET_NAME);
                    for (BosObjectSummary objectSummary : list.getContents()) {
                        System.out.println("ObjectKey: " + objectSummary.getKey());
                        key = objectSummary.getKey();
                    }

                    //上传Object
                    File file = new File(path);//上传文件的目录
                    PutObjectResponse putObjectFromFileResponse = client.putObject(BUCKET_NAME, key,  file);
                    System.out.println(putObjectFromFileResponse.getETag());

                    // 获取Object
                    BosObject object = client.getObject(BUCKET_NAME, key);
                    // 获取ObjectMeta
                    ObjectMetadata meta = object.getObjectMetadata();
                    // 获取Object的输入流
                    InputStream objectContent = object.getObjectContent();
                    // 处理Object
                    FileOutputStream fos=new FileOutputStream(path);//下载文件的目录/文件名
                    byte[] buffer=new byte[2048];
                    int count=0;
                    while ((count=objectContent.read(buffer))>=0) {
                        fos.write(buffer,0,count);
                    }

                    // 关闭流
                    objectContent.close();
                    fos.close();
                    System.out.println(meta.getETag());
                    System.out.println(meta.getContentLength());

                }catch (com.baidubce.BceServiceException e) {
                    System.out.println("xie Error ErrorCode: " + e.getErrorCode());
                    System.out.println("xie Error RequestId: " + e.getRequestId());
                    System.out.println("xie Error StatusCode: " + e.getStatusCode());
                    System.out.println("xie Error Message: " + e.getMessage());
                    System.out.println("xie Error ErrorType: " + e.getErrorType());
                } catch (com.baidubce.BceClientException  e) {
                    System.out.println("xie Error Message: " + e.getMessage());
                    e.printStackTrace();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    System.out.println("xie Error Message: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
