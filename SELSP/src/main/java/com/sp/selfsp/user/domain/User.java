package com.sp.selfsp.user.domain;

// LocalDateTime 负责承接数据库审计时间字段。
import java.time.LocalDateTime;

/**
 * 用户持久化对象。
 */
public class User {

    // 数据库主键，由数据库自增生成。
    private Long id;

    // 用户显示名称。
    private String name;

    // 用户邮箱，要求在表内唯一。
    private String email;

    // 用户状态，用于区分启用和停用。
    private String status;

    // 记录创建时间，便于后续审计和排序。
    private LocalDateTime createdAt;

    // 记录更新时间，便于后续增量排查。
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
        // 回填数据库生成的主键。
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
        // 保存业务名称。
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
        // 保存用户邮箱。
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
        // 保存用户状态。
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
        // 保存创建时间。
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
        // 保存更新时间。
        this.updatedAt = updatedAt;
    }
}
