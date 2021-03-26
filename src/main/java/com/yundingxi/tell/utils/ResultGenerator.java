package com.yundingxi.tell.utils;

import org.springframework.util.StringUtils;

/**
 * 响应结果生成工具
 *
 * @author 13
 * @qq交流群 796794009
 * @email 2449207463@qq.com
 * @link http://13blog.site
 */
public class ResultGenerator {
    private static final String DEFAULT_SUCCESS_MESSAGE = "SUCCESS";
    private static final String DEFAULT_FAIL_MESSAGE = "FAIL";
    private static final int RESULT_CODE_SUCCESS = 200;
    private static final int RESULT_CODE_SERVER_ERROR = 500;

    /**
     * 无参数 获取成功结果
     * @return
     */
    public static Result<String> genSuccessResult() {
        Result<String> result = new Result<String>();
        result.setResultCode(RESULT_CODE_SUCCESS);
        result.setMessage(DEFAULT_SUCCESS_MESSAGE);
        return result;
    }

    /**
     * 返回成功信息
     * @param message 成功信息
     * @return
     */
    public static Result<String> genSuccessResult(String message) {
        Result<String> result = new Result<String>();
        result.setResultCode(RESULT_CODE_SUCCESS);
        result.setMessage(message);
        return result;
    }

    /**
     * 获取成功结果
     * @param data 传入对象数据
     * @return
     */
    public static Result<Object> genSuccessResult(Object data) {
        Result<Object> result = new Result<Object>();
        result.setResultCode(RESULT_CODE_SUCCESS);
        result.setMessage(DEFAULT_SUCCESS_MESSAGE);
        result.setData(data);
        return result;
    }

    /**
     * 生成失败结果
     * @param message 失败结果
     * @return 失败结果
     */
    public static Result<String> genFailResult(String message) {
        Result<String> result = new Result<String>();
        result.setResultCode(RESULT_CODE_SERVER_ERROR);
        if (StringUtils.isEmpty(message)) {
            result.setMessage(DEFAULT_FAIL_MESSAGE);
        } else {
            result.setMessage(message);
        }
        return result;
    }

    /**
     * 返回失败结果
     * @param code 状态码
     * @param message 返回信息
     * @return 结果，有状态码
     */
    public static Result<String> genErrorResult(int code, String message) {
        Result<String> result = new Result<String>();
        result.setResultCode(code);
        result.setMessage(message);
        return result;
    }
}
