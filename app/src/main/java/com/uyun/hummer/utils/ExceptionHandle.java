package com.uyun.hummer.utils;

import com.google.gson.JsonParseException;
import com.uyun.hummer.R;
import com.uyun.hummer.UyunApplication;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.ConnectException;
import java.net.SocketTimeoutException;

import retrofit2.adapter.rxjava.HttpException;

/**
 * Created by Liyun on 2017/5/2.
 */

public class ExceptionHandle {
    private static final int BADREQUEST = 400;
    private static final int UNAUTHORIZED = 401;
    private static final int FORBIDDEN = 403;
    private static final int NOT_FOUND = 404;
    private static final int REQUEST_TIMEOUT = 408;
    private static final int INTERNAL_SERVER_ERROR = 500;
    private static final int BAD_GATEWAY = 502;
    private static final int SERVICE_UNAVAILABLE = 503;
    private static final int GATEWAY_TIMEOUT = 504;

    public static ResponeThrowable handleException(Throwable e) {
        ResponeThrowable ex;
        if (e instanceof HttpException) {
            HttpException httpException = (HttpException) e;
            ex = new ResponeThrowable(e, ERROR.HTTP_ERROR);
            switch (httpException.code()) {
                case BADREQUEST:
                   // break;
                case UNAUTHORIZED:
                case FORBIDDEN:
                case NOT_FOUND:
                case REQUEST_TIMEOUT:
                case GATEWAY_TIMEOUT:
                case INTERNAL_SERVER_ERROR:
                case BAD_GATEWAY:
                case SERVICE_UNAVAILABLE:
                default:
                    String msg = getErrorMsg((HttpException)e);
                    if(msg.equals("验证码错误")) {
                        ex.message = UyunApplication.context.getResources().getString(R.string.confilm_error);
                    }else if(msg.equals("用户名或密码错误")){
                        ex.message = UyunApplication.context.getResources().getString(R.string.account_or_psd_error);
                    }else{
                        ex.message = msg;
                    }
                    break;
            }
            return ex;
        } else if (e instanceof ServerException) {
            ServerException resultException = (ServerException) e;
            ex = new ResponeThrowable(resultException, resultException.code);
            ex.message = resultException.message;
            return ex;
        } else if (e instanceof JsonParseException
                || e instanceof JSONException
                /*|| e instanceof ParseException*/) {
            ex = new ResponeThrowable(e, ERROR.PARSE_ERROR);
            ex.message = UyunApplication.context.getResources().getString(R.string.parse_error);
            return ex;
        } else if (e instanceof ConnectException) {
            ex = new ResponeThrowable(e, ERROR.NETWORD_ERROR);
            ex.message = UyunApplication.context.getResources().getString(R.string.connect_error);
            return ex;
        } else if (e instanceof javax.net.ssl.SSLHandshakeException) {
            ex = new ResponeThrowable(e, ERROR.SSL_ERROR);
            ex.message = UyunApplication.context.getResources().getString(R.string.certificate_validation_failed);
            return ex;
        }else if(e instanceof java.net.UnknownHostException){
            ex = new ResponeThrowable(e, ERROR.NET_ERROR);
            ex.message = UyunApplication.context.getResources().getString(R.string.net_error);
            return ex;
        }else if(e instanceof SocketTimeoutException){
            ex = new ResponeThrowable(e, ERROR.NET_ERROR);
            ex.message = UyunApplication.context.getResources().getString(R.string.cannot_connect_to_server);
            return ex;
        } else {
            ex = new ResponeThrowable(e, ERROR.UNKNOWN);
            ex.message = UyunApplication.context.getResources().getString(R.string.server_error);
            return ex;
        }
    }
    public static String getErrorMsg(HttpException e){
        try {
            String errResponse = e.response().errorBody().string();
            JSONObject obj = new JSONObject(errResponse);
            return obj.getString("message");
        } catch (Exception e1) {
            e1.printStackTrace();
            return UyunApplication.context.getResources().getString(R.string.account_or_psd_error);
        }
    }


    /**
     * 约定异常
     */
    public class ERROR {
        /**
         * 未知错误
         */
        public static final int UNKNOWN = 1000;
        /**
         * 解析错误
         */
        public static final int PARSE_ERROR = 1001;
        /**
         * 网络错误
         */
        public static final int NETWORD_ERROR = 1002;
        /**
         * 协议出错
         */
        public static final int HTTP_ERROR = 1003;
        /**
         * 网络问题
         */
        public static final int NET_ERROR = 1004;

        /**
         * 证书出错
         */
        public static final int SSL_ERROR = 1005;
    }

    public static class ResponeThrowable extends Exception {
        public int code;
        public String message;

        public ResponeThrowable(Throwable throwable, int code) {
            super(throwable);
            this.code = code;
        }
    }

    /**
     * ServerException发生后，将自动转换为ResponeThrowable返回
     */
    class ServerException extends RuntimeException {
        int code;
        String message;
    }

}