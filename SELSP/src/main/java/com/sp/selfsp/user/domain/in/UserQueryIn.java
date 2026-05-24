package com.sp.selfsp.user.domain.in;

/**
 * 用户列表查询入参。
 */
public class UserQueryIn {

    // 名称关键字用于模糊过滤。
    private String name;

    // 状态用于精确过滤。
    private String status;

    /**
     * 获取名称关键字。
     *
     * @return 名称关键字
     */
    public String getName() {
        return name;
    }

    /**
     * 设置名称关键字。
     *
     * @param name 名称关键字
     */
    public void setName(String name) {
        // 保存查询条件中的名称关键字。
        this.name = name;
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
        // 保存查询条件中的状态。
        this.status = status;
    }
}
