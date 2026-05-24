package com.sp.selfsp.user.service;

// 查询入参对象定义列表过滤边界。
import com.sp.selfsp.user.domain.in.UserQueryIn;
// 保存入参对象定义创建和修改边界。
import com.sp.selfsp.user.domain.in.UserSaveIn;
// 输出对象定义接口返回结构。
import com.sp.selfsp.user.domain.out.UserDetailOut;
// List 用于承接列表返回。
import java.util.List;

/**
 * 用户服务接口。
 */
public interface UserService {

    /**
     * 新增用户。
     *
     * @param saveIn 保存入参
     * @return 用户详情
     */
    UserDetailOut create(UserSaveIn saveIn);

    /**
     * 更新用户。
     *
     * @param id 用户主键
     * @param saveIn 保存入参
     * @return 用户详情
     */
    UserDetailOut update(Long id, UserSaveIn saveIn);

    /**
     * 删除用户。
     *
     * @param id 用户主键
     */
    void delete(Long id);

    /**
     * 查询单个用户。
     *
     * @param id 用户主键
     * @return 用户详情
     */
    UserDetailOut getById(Long id);

    /**
     * 查询用户列表。
     *
     * @param queryIn 查询入参
     * @return 用户列表
     */
    List<UserDetailOut> list(UserQueryIn queryIn);
}
