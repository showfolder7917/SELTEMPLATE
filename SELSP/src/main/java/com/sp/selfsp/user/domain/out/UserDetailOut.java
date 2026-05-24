package com.sp.selfsp.user.domain.out;

// LocalDateTime 负责对外暴露审计时间。
import java.time.LocalDateTime;

/**
 * 用户接口出参。
 */
public class UserDetailOut {

    // 对外返回的用户主键。
    private Long id;

    // 对外返回的用户名称。
    private String name;

    // 对外返回的用户邮箱。
    private String email;

    // 对外返回的用户状态。
    private String status;

    // 对外返回的创建时间。
    private LocalDateTime createdAt;

    // 对外返回的更新时间。
    private LocalDateTime updatedAt;

    /**
     * 获取主键。
     *
     * @return 主键
     */
    public Long getId() {
        return id;
    }

    /**
     * 设置主键。
     *
     * @param id 主键
     */
    public void setId(Long id) {
        // 保存对外返回的主键。
        this.id = id;
    }

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
        // 保存对外返回的名称。
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
        // 保存对外返回的邮箱。
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
        // 保存对外返回的状态。
        this.status = status;
    }

    /**
     * 获取创建时间。
     *
     * @return 创建时间
     */
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    /**
     * 设置创建时间。
     *
     * @param createdAt 创建时间
     */
    public void setCreatedAt(LocalDateTime createdAt) {
        // 保存对外返回的创建时间。
        this.createdAt = createdAt;
    }

    /**
     * 获取更新时间。
     *
     * @return 更新时间
     */
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    /**
     * 设置更新时间。
     *
     * @param updatedAt 更新时间
     */
    public void setUpdatedAt(LocalDateTime updatedAt) {
        // 保存对外返回的更新时间。
        this.updatedAt = updatedAt;
    }
}
