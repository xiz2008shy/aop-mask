package com.tomqi.aop_mask.pojo;


/**
 * @author TOMQI
 * @Title: MValidatorResult
 * @ProjectName: aop_mask
 * @Description : 用于效验器结果的传递
 * @data 2020/10/8 17:14
 **/
public class MValidatorResult {

    private String message;

    /**
     * 结果为true表示通过效验，可以继续下一步效验
     */
    private boolean pass;

    public MValidatorResult() {
        this.message = "";
        this.pass = false;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isPass() {
        return pass;
    }

    public void setPass(boolean pass) {
        this.pass = pass;
    }
}
