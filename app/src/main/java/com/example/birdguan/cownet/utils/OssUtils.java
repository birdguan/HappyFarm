package com.example.birdguan.cownet.utils;

import android.util.Log;

import com.alibaba.sdk.android.oss.ClientException;
import com.alibaba.sdk.android.oss.OSS;
import com.alibaba.sdk.android.oss.OSSClient;
import com.alibaba.sdk.android.oss.ServiceException;
import com.alibaba.sdk.android.oss.callback.OSSCompletedCallback;
import com.alibaba.sdk.android.oss.callback.OSSProgressCallback;
import com.alibaba.sdk.android.oss.common.auth.OSSCredentialProvider;
import com.alibaba.sdk.android.oss.common.auth.OSSFederationCredentialProvider;
import com.alibaba.sdk.android.oss.common.auth.OSSFederationToken;
import com.alibaba.sdk.android.oss.internal.OSSAsyncTask;
import com.alibaba.sdk.android.oss.model.GetObjectRequest;
import com.alibaba.sdk.android.oss.model.GetObjectResult;
import com.alibaba.sdk.android.oss.model.ObjectMetadata;
import com.alibaba.sdk.android.oss.model.PutObjectRequest;
import com.alibaba.sdk.android.oss.model.PutObjectResult;
import com.example.birdguan.cownet.MyApplication;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

import okhttp3.Response;

/**
 * Created by Gwg on 2018-3-12.
 */

public class OssUtils {
    static final String endpoint = "http://oss.zfjsoft.com";
    static final String bucketName = "hb-qlw";
    static final String stsServer = "http://zfjsoft.com:8086/OSSCredential";

    static OSSCredentialProvider credentialProvider = new OSSFederationCredentialProvider() {
        @Override
        public OSSFederationToken getFederationToken() {
            Callable<String> callable = new Callable<String>() {
                public String call() throws Exception {
                    Response response = OkHttpManager.getSync(stsServer);
                    if (response.isSuccessful()) {
                        return response.body().string();
                    } else {
                        Log.e("debug", "GetSTSTokenFail" + response);
                        return null;
                    }
                }
            };

            try {
                FutureTask<String> future = new FutureTask<String>(callable);
                new Thread(future).start();

                String stsJson = future.get();
                if (stsJson != null) {
                    JSONObject jsonObjs = new JSONObject(stsJson);
                    String ak = jsonObjs.getString("AccessKeyId");
                    String sk = jsonObjs.getString("AccessKeySecret");
                    String token = jsonObjs.getString("SecurityToken");
                    String expiration = jsonObjs.getString("Expiration");

                    return new OSSFederationToken(ak, sk, token, expiration);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                Log.e("debug", "GetSTSTokenFail" + e.toString());
                e.printStackTrace();
            }

            return null;
        }
    };

    static OSS oss = new OSSClient(MyApplication.getContext(), endpoint, credentialProvider);

    public static void upload(String uploadFilePath) {
        // 构造上传请求
        String objectKey = uploadFilePath.split("/")[uploadFilePath.split("/").length - 1];
        PutObjectRequest put = new PutObjectRequest(bucketName, objectKey, uploadFilePath);
        try {
            PutObjectResult putResult = oss.putObject(put);
            Log.d("debug", "putObject UploadSuccess");
            Log.d("debug", putResult.getETag());
            Log.d("debug", putResult.getRequestId());
        } catch (ClientException e) {
            // 本地异常如网络异常等
            e.printStackTrace();
        } catch (ServiceException e) {
            // 服务异常
            Log.e("debug", e.getRequestId());
            Log.e("debug", e.getErrorCode());
            Log.e("debug", e.getHostId());
            Log.e("debug", e.getRawMessage());
        }
    }

    public static void uploadAsync(String uploadFilePath) {
        // 构造上传请求
        String objectKey = uploadFilePath.split("/")[uploadFilePath.split("/").length - 1];
        PutObjectRequest put = new PutObjectRequest(bucketName, objectKey, uploadFilePath);
        // 异步上传时可以设置进度回调
        put.setProgressCallback(new OSSProgressCallback<PutObjectRequest>() {
            @Override
            public void onProgress(PutObjectRequest request, long currentSize, long totalSize) {
//                Log.d("debug", "currentSize: " + currentSize + " totalSize: " + totalSize);
            }
        });
        OSSAsyncTask task = oss.asyncPutObject(put, new OSSCompletedCallback<PutObjectRequest, PutObjectResult>() {
            @Override
            public void onSuccess(PutObjectRequest request, PutObjectResult result) {
                Log.d("info", "asyncPutObject UploadSuccess:" + request.getUploadData());
            }

            @Override
            public void onFailure(PutObjectRequest request, ClientException clientExcepion, ServiceException serviceException) {
                Log.d("info", "asyncPutObject UploadFailed:" );
                // 请求异常
                if (clientExcepion != null) {
                    // 本地异常如网络异常等
                    clientExcepion.printStackTrace();
                }
                if (serviceException != null) {
                    // 服务异常
                    Log.e("debug", serviceException.getErrorCode());
                    Log.e("debug", serviceException.getRequestId());
                    Log.e("debug", serviceException.getHostId());
                    Log.e("debug", serviceException.getRawMessage());
                }
            }
        });
    }

    public static void download() {
        //构造下载文件请求
        GetObjectRequest get = new GetObjectRequest(bucketName, "attention-60.png");
        //设置下载进度回调
        get.setProgressListener(new OSSProgressCallback<GetObjectRequest>() {
            @Override
            public void onProgress(GetObjectRequest request, long currentSize, long totalSize) {
                Log.d("debug", "getobj_progress: " + currentSize + "  total_size: " + totalSize);
            }
        });
        try {
            // 同步执行下载请求，返回结果
            GetObjectResult getResult = oss.getObject(get);
            Log.d("debug", "Content-Length=" + getResult.getContentLength());
            // 获取文件输入流
            InputStream inputStream = getResult.getObjectContent();
            byte[] buffer = new byte[2048];
            int len;
            while ((len = inputStream.read(buffer)) != -1) {
                // 处理下载的数据，比如图片展示或者写入文件等
            }
            // 下载后可以查看文件元信息
            ObjectMetadata metadata = getResult.getMetadata();
            Log.d("debug", "ContentType=" + metadata.getContentType());
        } catch (ClientException e) {
            // 本地异常如网络异常等
            e.printStackTrace();
        } catch (ServiceException e) {
            // 服务异常
            Log.e("debug", e.getRequestId());
            Log.e("debug", e.getErrorCode());
            Log.e("debug", e.getHostId());
            Log.e("debug", e.getRawMessage());
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            String url = oss.presignConstrainedObjectURL(bucketName, "attention-60.png", 30 * 60);
            Log.d("debug", url);
        } catch (ClientException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void downloadAsync() {
        GetObjectRequest get = new GetObjectRequest(bucketName, "aaaa1111");
        //设置下载进度回调
        get.setProgressListener(new OSSProgressCallback<GetObjectRequest>() {
            @Override
            public void onProgress(GetObjectRequest request, long currentSize, long totalSize) {
                //Log.d("debug", "downloadAsync progress: " + currentSize + "  total_size: " + totalSize);
            }
        });

        OSSAsyncTask task = oss.asyncGetObject(get, new OSSCompletedCallback<GetObjectRequest, GetObjectResult>() {
            @Override
            public void onSuccess(GetObjectRequest request, GetObjectResult result) {
                // 请求成功
                InputStream inputStream = result.getObjectContent();
                byte[] buffer = new byte[2048];
                int len;
                try {
                    while ((len = inputStream.read(buffer)) != -1) {
                        // 处理下载的数据
                        Log.d("debug", "download len= " + len);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(GetObjectRequest request, ClientException clientExcepion, ServiceException serviceException) {
                // 请求异常
                if (clientExcepion != null) {
                    // 本地异常如网络异常等
                    clientExcepion.printStackTrace();
                }
                if (serviceException != null) {
                    // 服务异常
                    Log.e("debug", serviceException.getErrorCode());
                    Log.e("debug", serviceException.getRequestId());
                    Log.e("debug", serviceException.getHostId());
                    Log.e("debug", serviceException.getRawMessage());
                }
            }
        });
    }
}
