package com.sp.selfsp.common.util;

/**
 * 统一响应壳，保证所有接口都返回 code、message、data 三段结构。
 *
 * @param <T> 业务数据类型
 */
public class CommonResponse<T> {

    // 业务码用于给前端判断成功或失败。
    private int code;

    // 提示语用于描述处理结果。
    private String message;

    // data 统一承载本次接口返回的业务数据。
    private T data;

    /**
     * 组装成功响应。
     *
     * @param data 业务数据
     * @param <T> 数据类型
     * @return 标准成功壳
     */
    public static <T> CommonResponse<T> success(T data) {
        // 新建响应对象，避免调用方自己散落拼装成功结构。
        CommonResponse<T> response = new CommonResponse<>();
        // 0 统一表示成功。
        response.setCode(0);
        // success 统一作为成功消息。
        response.setMessage("success");
        // 透传业务数据给前端。
        response.setData(data);
        // 返回统一结构。
        return response;
    }

    /**
     * 组装失败响应。
     *
     * @param code 失败码
     * @param message 失败消息
     * @param <T> 数据类型
     * @return 标准失败壳
     */
    public static <T> CommonResponse<T> failure(int code, String message) {
        // 失败场景同样由统一壳负责生成，避免控制器各自定义错误格式。
        CommonResponse<T> response = new CommonResponse<>();
        // 回填业务失败码。
        response.setCode(code);
        // 回填失败消息。
        response.setMessage(message);
        // 返回失败结构。
        return response;
    }

    /**
     * 获取业务码。
     *
     * @return 业务码
     */
    public int getCode() {
        return code;
    }

    /**
     * 设置业务码。
     *
     * @param code 业务码
     */
    public void setCode(int code) {
        // 保存业务码，供前端统一处理。
        this.code = code;
    }

    /**
     * 获取提示语。
     *
     * @return 提示语
     */
    public String getMessage() {
        return message;
    }

    /**
     * 设置提示语。
     *
     * @param message 提示语
     */
    public void setMessage(String message) {
        // 保存接口提示语。
        this.message = message;
    }

    /**
     * 获取业务数据。
     *
     * @return 业务数据
     */
    public T getData() {
        return data;
    }

    /**
     * 设置业务数据。
     *
     * @param data 业务数据
     */
    public void setData(T data) {
        // 保存接口业务数据。
        this.data = data;
    }
}
