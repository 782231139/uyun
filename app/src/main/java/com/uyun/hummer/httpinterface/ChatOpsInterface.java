package com.uyun.hummer.httpinterface;

import com.uyun.hummer.model.bean.CodeInfo;
import com.uyun.hummer.model.bean.LabelInfo;
import com.uyun.hummer.model.bean.LabelUpdateResultInfo;
import com.uyun.hummer.model.bean.LabelWebviewKeyInfo;
import com.uyun.hummer.model.bean.VersionInfo;
import com.uyun.hummer.model.bean.VerticalInfo;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Url;
import rx.Observable;

/**
 * Created by Liyun on 2017/4/25.
 */

public interface ChatOpsInterface {
    @Multipart
    @POST("/chatops/api/v2/chat/upload?fileapi")
    Observable<Response<ResponseBody>> uploadFile(@Part MultipartBody.Part file);
    @GET("/chatops/api/v2/chat/teams")
    Observable<Response<ResponseBody>> getTeams();
    @GET("/tenant/api/v1/base/code/getcode")
    Observable<VerticalInfo> getImageFile();
    @GET
    Observable<Response<ResponseBody>> getImageFile(@Url String url);
    @GET("/tenant/api/v1/configuration/get")
    Observable<CodeInfo> getCodeSwitch();

    @GET("/chatops/api/v2/chat/boards")
    Observable<LabelInfo> getLabelTotalData();
    @POST("/chatops/api/v2/chat/board/update")
    Observable<LabelUpdateResultInfo> updateLabelData(@Body RequestBody requestBody);
    @GET("/tenant/api/v1/user/details/view")
    Observable<LabelWebviewKeyInfo> getLabelWebviewKey();

    @Multipart
    @POST("/chatops/api/v2/chat/voice/upload")
    Observable<Response<ResponseBody>> uploadAudioFile(@Part MultipartBody.Part file);

    @GET
    Observable<Response<ResponseBody>> downloadAudioFile(@Url String url);

    @GET("/chatops/api/v2/chat/environment/version")
    Observable<VersionInfo> getVersion();
}
