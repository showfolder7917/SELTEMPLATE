package com.sp.selfsp.user.dao;

// 用户持久化对象用于承接增删改查结果。
import com.sp.selfsp.user.domain.User;
// 查询入参对象用于传递列表过滤条件。
import com.sp.selfsp.user.domain.in.UserQueryIn;
// List 用于承接多条记录结果。
import java.util.List;
// Mapper 负责把接口注册为 MyBatis 数据访问组件。
import org.apache.ibatis.annotations.Mapper;
// Param 负责给单参数 SQL 指定稳定命名。
import org.apache.ibatis.annotations.Param;

/**
 * 用户数据访问接口。
 */
@Mapper
public interface UserDao {

    /**
     * 新增用户。
     *
     * @param user 持久化对象
     * @return 影响行数
     */
    int insert(User user);

    /**
     * 按主键更新用户。
     *
     * @param user 持久化对象
     * @return 影响行数
     */
    int updateById(User user);

    /**
     * 按主键删除用户。
     *
     * @param id 主键
     * @return 影响行数
     */
    int deleteById(@Param("id") Long id);

    /**
     * 按主键查询用户。
     *
     * @param id 主键
     * @return 用户对象
     */
    User selectById(@Param("id") Long id);

    /**
     * 按邮箱查询用户。
     *
     * @param email 邮箱
     * @return 用户对象
     */
    User selectByEmail(@Param("email") String email);

    /**
     * 按条件查询用户列表。
     *
     * @param queryIn 查询条件
     * @return 用户列表
     */
    List<User> selectList(UserQueryIn queryIn);
}
