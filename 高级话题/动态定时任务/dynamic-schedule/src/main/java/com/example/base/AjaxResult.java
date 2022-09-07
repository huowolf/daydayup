package com.example.base;

import lombok.Data;

@Data
public class AjaxResult {
    private static final long serialVersionUID = 1L;

    /** 返回状态码 */
    private Integer status;

    /** 返回消息 */
    private String message;

    /** 返回内容 */
    private Object data;

    /** 返回异常信息 */
    private String error;

    /** 时间搓 */
    private Long timestamp;

    /** 初始化一个新创建的 AjaxResult 对象，使其表示一个空消息。 */
    public AjaxResult() {
    }

    /** 初始化一个新创建的 AjaxResult 对象 */
    public AjaxResult(Integer status, String msg, Object data, String error) {
        this.status = status;
        this.message = msg;
        this.error = error;
        if (data != null) {
            this.data = data;
        }
        this.timestamp = System.currentTimeMillis();
    }

    public static AjaxResult result(Integer status, String msg, Object data, String error) {
        return new AjaxResult(status, msg, data, error);
    }

    // 返回成功
    public static AjaxResult success() {
        return result(200, "success", null, null);
    }

    public static AjaxResult success(String msg) {
        return success(msg, null);
    }

    public static AjaxResult success(Object data) {
        return success("success", data);
    }

    public static AjaxResult success(String msg, Object data) {
        return result(200, msg, data, null);
    }

    /** 自定义错误 */
    public static AjaxResult error(int code, String msg, String error) {
        return result(code, msg, null, error);
    }


}
