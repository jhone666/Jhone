package com.jhone.demo.http;

import com.jhone.demo.utils.JsonUtil;
import com.yolanda.nohttp.Headers;
import com.yolanda.nohttp.Logger;
import com.yolanda.nohttp.RequestMethod;
import com.yolanda.nohttp.RestRequest;
import com.yolanda.nohttp.StringRequest;

import javax.net.ssl.SSLContext;


/**
 * Created by jhone on 2016/3/29.
 * Description
 */
public class SzjlRequestHttps extends RestRequest<String> {


   public enum Certificate{
        /**
         * 使用证书
         */
        YES,
        /**
         * 不使用证书
         */
        NO;
    }

    /**
     *
     * @param url
     * @param requestMethod 请求方法
     * @param certificate  是否需要使用证书请求 ，USECERTIFICATE  UNUSECERTIFICATE
     */
    public SzjlRequestHttps(String url, RequestMethod requestMethod, Certificate certificate) {
        super(url, requestMethod);
        if (certificate== Certificate.YES) {// 证书请求
            SSLContext sslContext = SSLContextUtil.getSSLContext();
            if (sslContext != null)
                this.setSSLSocketFactory(sslContext.getSocketFactory());
            this.setHostnameVerifier(SSLContextUtil.HOSTNAME_VERIFIER);//由于证书上的问题，目前忽略证书访问
        } else if (certificate== Certificate.NO) {//免证书请求
            SSLContext sslContext = SSLContextUtil.getDefaultSLLContext();
            if (sslContext != null)
                this.setSSLSocketFactory(sslContext.getSocketFactory());
            this.setHostnameVerifier(SSLContextUtil.HOSTNAME_VERIFIER);
        }
    }
    @Override
    public String parseResponse(String url, Headers responseHeaders, byte[] responseBody) {
        String jsonStr = StringRequest.parseResponseString(url, responseHeaders, responseBody);
        jsonStr= JsonUtil.ReplaceStr(jsonStr);
        Logger.d("SZLC-http--->"+jsonStr);
        return  jsonStr;
    }

    /**
     * 定义请求类型
     * @return
     */
    @Override
    public String getAccept() {
        return  "application/json;q=1";
//      return "text/html,application/xhtml+xml,application/xml;*/*;q=0.9";//字符串方式，看项目中能不能直接json解析出来吧
    }
}
