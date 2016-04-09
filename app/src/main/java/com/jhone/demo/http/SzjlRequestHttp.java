package com.jhone.demo.http;

import com.jhone.demo.utils.JsonUtil;
import com.yolanda.nohttp.Headers;
import com.yolanda.nohttp.Logger;
import com.yolanda.nohttp.RequestMethod;
import com.yolanda.nohttp.RestRequest;
import com.yolanda.nohttp.StringRequest;


/**
 * Created by jhone on 2016/3/18.
 * Description
 */
public class SzjlRequestHttp extends RestRequest<String> {


    /**
     *
     * @param url 请求路径
     * @param requestMethod 请求方式  RequestMethod.GET或者RequestMethod.POST
     */
    public SzjlRequestHttp(String url, RequestMethod requestMethod) {
        super(url, requestMethod);
    }

    /**
     * 可通过构造方法将对应类类型传进来，在这里直接返回对象给外层，那个缺点就是请求前要封装出对应的Bean
     * 考虑当前项目接口文档的缺陷，就不再进行此封装
     * @param url
     * @param responseHeaders
     * @param responseBody
     * @return
     */
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
