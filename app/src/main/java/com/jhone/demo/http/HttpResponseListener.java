/*
 * Copyright © YOLANDA. All Rights Reserved
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.jhone.demo.http;

import android.content.Context;
import android.content.DialogInterface;

import com.jhone.demo.utils.ToastUtils;
import com.jhone.demo.view.WaitDialog;
import com.yolanda.nohttp.Logger;
import com.yolanda.nohttp.OnResponseListener;
import com.yolanda.nohttp.Request;
import com.yolanda.nohttp.Response;
import com.yolanda.nohttp.error.ClientError;
import com.yolanda.nohttp.error.NetworkError;
import com.yolanda.nohttp.error.NotFoundCacheError;
import com.yolanda.nohttp.error.ServerError;
import com.yolanda.nohttp.error.TimeoutError;
import com.yolanda.nohttp.error.URLError;
import com.yolanda.nohttp.error.UnKnownHostError;

/**
 * Created by jhone on 2016/3/18.
 * Description
 */
public class HttpResponseListener<T> implements OnResponseListener<T> {

    /**
     * Dialog.
     */
    private WaitDialog mWaitDialog;

    private Request<?> mRequest;

    /**
     * 结果回调.
     */
    private HttpListener<T> callback;

    /**
     * 是否显示dialog.
     */
    private boolean isLoading;

    /**
     * @param context      context用来实例化dialog.
     * @param request      请求对象.
     * @param httpCallback 回调对象.
     * @param canCancel    是否允许用户取消请求.
     * @param isLoading    是否显示dialog.
     */
    public HttpResponseListener(Context context, Request<?> request, HttpListener<T> httpCallback, boolean canCancel, boolean isLoading) {
        this.mRequest = request;
        if (context != null && isLoading) {
            mWaitDialog = new WaitDialog(context,"正在加载中...");
            mWaitDialog.setCancelable(canCancel);
            mWaitDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    mRequest.cancel(true);
                }
            });
        }
        this.callback = httpCallback;
        this.isLoading = isLoading;
    }

    /**
     * 开始请求, 这里显示一个dialog.
     */
    @Override
    public void onStart(int what) {
        if (isLoading && mWaitDialog != null && !mWaitDialog.isShowing())
            mWaitDialog.show();
    }

    /**
     * 结束请求, 这里关闭dialog.
     */
    @Override
    public void onFinish(int what) {
        if (isLoading && mWaitDialog != null && mWaitDialog.isShowing())
            mWaitDialog.dismiss();
    }

    /**
     * 成功回调.
     */
    @Override
    public void onSucceed(int what, Response<T> response) {
        if (callback != null)
            callback.onSucceed(what, response);
    }

    /**
     * 失败回调.
     */
    @Override
    public void onFailed(int what, String url, Object tag, Exception exception, int responseCode, long networkMillis) {
        if (exception instanceof ClientError) {// 客户端错误
            if (responseCode == 400) {
                Logger.e("服务器未能理解请求 400");
            } else if (responseCode == 403) {
                Logger.e("请求的页面被禁止 403");
            } else if (responseCode == 404) {
                Logger.e("无法找到请求的页面 404");
            } else {
                Logger.e("服务器异常");
            }
        } else if (exception instanceof ServerError) {// 服务器错误
            if (500 == responseCode) {
                Logger.e("服务器遇到不可预知的情况。");
            } else if (501 == responseCode) {
                Logger.e("服务器不支持的请求。");
            } else if (502 == responseCode) {
                Logger.e("服务器从上游服务器收到一个无效的响应。");
            } else if (503 == responseCode) {
                Logger.e("服务器临时过载或当机。");
            } else if (504 == responseCode) {
                Logger.e("网关超时。");
            } else if (505 == responseCode) {
                Logger.e("服务器不支持请求中指明的HTTP协议版本。");
            } else {
                Logger.e("服务器挂了");
            }
        } else if (exception instanceof NetworkError) {// 网络不好
            ToastUtils.showShort("请检查网络。");
        } else if (exception instanceof TimeoutError) {// 请求超时
            ToastUtils.showShort("请求超时");
        } else if (exception instanceof UnKnownHostError) {// 找不到服务器
            ToastUtils.showShort("服务器异常。");
        } else if (exception instanceof URLError) {// URL是错的
            ToastUtils.showShort("地址错误。");
        } else if (exception instanceof NotFoundCacheError) {
            // 这个异常只会在仅仅查找缓存时没有找到缓存时返回
            ToastUtils.showShort("服务器异常。");
        } else {
            ToastUtils.showShort("服务器异常。");
        }
        Logger.e("网络请求错误：" + exception.getMessage());
        if (callback != null)
            callback.onFailed(what, url, tag, exception, responseCode, networkMillis);
    }

}
