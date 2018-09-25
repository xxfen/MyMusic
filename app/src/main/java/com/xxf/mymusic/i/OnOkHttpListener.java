package com.xxf.mymusic.i;

/**
 * author：xxf
 */
public interface OnOkHttpListener {
//    void onTokenError();

    void onSuccess(String response);

    void onFailure(String error);

//    void onMsg(String msg);
//
//    void onEmpty();

}
