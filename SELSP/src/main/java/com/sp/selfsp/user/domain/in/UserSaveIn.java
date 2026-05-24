package com.sp.selfsp.user.domain.in;

/**
 * 用户新增和修改入参。
 */
public class UserSaveIn {

    // 请求中的用户名称。
    private String name;

    // 请求中的用户邮箱。
    private String email;

    // 请求中的用户状态。
    private String status;

    /**
     * 获取名称。
     *
     * @return 名称
     */
    public String getName() {
        return name;
    }

    /**
     * 设置名称。
     *
     * @param name 名称
     */
    public void setName(String name) {
        // 保存前端提交的名称。
        this.name = name;
    }

    /**
     * 获取邮箱。
     *
     * @return 邮箱
     */
    public String getEmail() {
        return email;
    }

    /**
     * 设置邮箱。
     *
     * @param email 邮箱
     */
    public void setEmail(String email) {
        // 保存前端提交的邮箱。
        this.email = email;
    }

    /**
     * 获取状态。
     *
     * @return 状态
     */
    public String getStatus() {
        return status;
    }

    /**
     * 设置状态。
     *
     * @param status 状态
     */
    public void setStatus(String status) {
        // 保存前端提交的状态。
        this.status = status;
    }
}
