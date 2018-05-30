package com.uyun.hummer.httpinterface;

import com.uyun.hummer.model.bean.CameraDetailInfo;
import com.uyun.hummer.model.bean.CameraLayoutInfo;
import com.uyun.hummer.model.bean.CameraSaveInfo;
import com.uyun.hummer.model.bean.LogoutInfo;
import com.uyun.hummer.model.bean.PwdTypeInfo;
import com.uyun.hummer.model.bean.TenentInfo;
import com.uyun.hummer.model.bean.UserBean;
import com.uyun.hummer.model.bean.UserDetailsInfo;
import com.uyun.hummer.model.bean.VerifyInfo;
import com.uyun.hummer.model.bean.VersionUrlInfo;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;
import retrofit2.http.Url;
import rx.Observable;

/**
 * Created by Liyun on 2017/3/17.
 */

public interface UserInterface {
    @GET("/chatops")
    Observable<Response<ResponseBody>> getRealHost();
    @Headers({"Content-type:application/json;charset=UTF-8"})
    @POST("/tenant/api/v1/user/login")
    Observable<UserBean> login(@Body RequestBody body);

    @Headers({"Content-type:application/json;charset=UTF-8"})
    @POST("/VIID/saveCamera")
    Observable<CameraSaveInfo> saveCamera(@Query("api_key") String apikey, @Body RequestBody body);

    @GET("/tenant/api/v1/user/details/view")
    Observable<UserDetailsInfo> getUserInfo(@Query("t") String time,@Query("userId") String userId);

    @GET("/tenant/api/v1/user/verify")
    Observable<VerifyInfo> verifyUser();

    @GET("/tenant/api/v1/configuration/get")
    Observable<PwdTypeInfo> getPwdType();

    @GET("/tenant/api/v1/tenant/view")
    Observable<TenentInfo> getTenentInfo(@Query("tenantId") String tenentId);
    @GET("/tenant/api/v1/user/logout?1493110153304&_=1493109761540")
    Observable<LogoutInfo> logout();

    @GET("/chatops/api/v2/chat/environment/version")
    Observable<VersionUrlInfo> getVersion();

    @Headers({"Content-type:application/json;charset=UTF-8"})
    @GET("/VIID/CameraLayout")
    Observable<CameraLayoutInfo> getCameraLayout(@Query("api_key") String apikey,@Query("lnt") String lnt, @Query("lat") String lat);


    @Headers({"Content-type:application/json;charset=UTF-8"})
    @GET("/VIID/Cameras?offset=1&limit=100")
    Observable<CameraDetailInfo> getCameraDetail(@Query("api_key") String apikey, @Query("JD") String jd,@Query("WD") String wd);

    @Headers({"Content-type:application/json;charset=UTF-8"})
    @GET("/VIID/Cameras?offset=1&limit=20")
    Observable<CameraDetailInfo> getCameraName(@Query("api_key") String apikey, @Query("SBMC") String name);

    @GET
    Observable<Response<ResponseBody>> downloadAudioFile(@Url String url);
}
