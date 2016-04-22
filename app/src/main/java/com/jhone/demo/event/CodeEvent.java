package com.jhone.demo.event;

import com.google.zxing.Result;

/**
 * Created by jhone on 2016/4/21.
 */
public class CodeEvent {
    Result result;
    public CodeEvent(Result result) {
        this.result = result;
    }

    public Result getResult() {
        return result;
    }

}
