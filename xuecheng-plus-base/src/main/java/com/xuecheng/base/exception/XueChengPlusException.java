package com.xuecheng.base.exception;


import lombok.Getter;

/**
 * @author Mr.M
 * @version 1.0
 * @description 学成在线项目异常类
 * @date 2022/9/6 11:29
 */
@Getter
public class XueChengPlusException extends RuntimeException {

    private String errCode;
    private String errMessage;

    public XueChengPlusException() {
        super();
    }

    public XueChengPlusException(String errMessage) {
        super(errMessage);
        this.errMessage = errMessage;
    }

    public XueChengPlusException(String errCode, String errMessage) {
        super(errMessage);
        this.errCode = errCode;
        this.errMessage = errMessage;
    }


    public static void cast(CommonError commonError) {
        throw new XueChengPlusException(commonError.getErrMessage());
    }

    public static void cast(String errMessage) {
        throw new XueChengPlusException(errMessage);
    }

    public static void cast(String errMessage, String errCode) {
        throw new XueChengPlusException(errCode, errMessage);
    }

}