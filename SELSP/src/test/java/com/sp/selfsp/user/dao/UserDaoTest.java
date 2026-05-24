package com.sp.selfsp.user.dao;

// 断言工具用于验证 DAO 返回值和主键回填结果。
import static org.junit.jupiter.api.Assertions.assertEquals;
// 断言工具用于验证查询结果非空。
import static org.junit.jupiter.api.Assertions.assertNotNull;
// 断言工具用于验证筛选结果顺序。
import static org.junit.jupiter.api.Assertions.assertTrue;

// 用户持久化对象用于直接验证 mapper 落库和字段映射。
import com.sp.selfsp.user.domain.User;
// 查询入参用于触发 MyBatis 动态 SQL 条件。
import com.sp.selfsp.user.domain.in.UserQueryIn;
// List 用于承接 DAO 列表结果。
import java.util.List;
// JUnit Test 用于声明 DAO 层用例。
import org.junit.jupiter.api.Test;
// Autowired 负责注入真实 MyBatis DAO。
import org.springframework.beans.factory.annotation.Autowired;
// SpringBootTest 负责启动数据源和 MyBatis 映射环境。
import org.springframework.boot.test.context.SpringBootTest;
// Sql 负责在每次用例前重建最小表结构和种子数据。
import org.springframework.test.context.jdbc.Sql;

/**
 * 用户 DAO 集成测试。
 */
@SpringBootTest
@Sql(scripts = "classpath:reset-user-test-data.sql")
public class UserDaoTest {

    // 真实 DAO 用于验证 XML SQL、主键回填和字段映射。
    @Autowired
    private UserDao userDao;

    /**
     * 验证新增用户时 MyBatis 会回填数据库生成主键。
     */
    @Test
    public void shouldInsertUserAndFillGeneratedId() {
        // 构造最小持久化对象，覆盖 insert SQL 和 useGeneratedKeys 回填。
        User user = buildUser("DAO新增用户", "dao-create@selfsp.local", "ACTIVE");
        // 执行插入，验证影响行数。
        int affectedRows = userDao.insert(user);
        // 插入成功应只影响一条记录。
        assertEquals(1, affectedRows);
        // 数据库生成的主键必须被回填到持久化对象。
        assertNotNull(user.getId());
        // 按主键回查，验证 resultMap 和驼峰映射。
        User persisted = userDao.selectById(user.getId());
        // 名称应与插入值一致。
        assertEquals("DAO新增用户", persisted.getName());
        // createdAt 映射不应为空，证明下划线字段已正确映射为 Java 属性。
        assertNotNull(persisted.getCreatedAt());
        // updatedAt 同样应成功映射。
        assertNotNull(persisted.getUpdatedAt());
    }

    /**
     * 验证列表 SQL 可同时应用名称模糊、状态过滤和倒序排序。
     */
    @Test
    public void shouldFilterUsersByNameAndStatusInDescendingOrder() {
        // 先插入多个用户，制造名称、状态和主键先后差异。
        userDao.insert(buildUser("测试用户-ACTIVE", "filter-a@selfsp.local", "ACTIVE"));
        // 第二个匹配用户用于验证 ORDER BY id DESC。
        userDao.insert(buildUser("测试用户-最新", "filter-b@selfsp.local", "ACTIVE"));
        // 不匹配状态的用户用于验证 status 条件。
        userDao.insert(buildUser("测试用户-INACTIVE", "filter-c@selfsp.local", "INACTIVE"));
        // 构造同时包含名称和状态的查询条件。
        UserQueryIn queryIn = new UserQueryIn();
        // 名称关键字用于触发 LIKE 条件。
        queryIn.setName("测试用户");
        // 状态用于触发精确匹配条件。
        queryIn.setStatus("ACTIVE");
        // 执行列表查询，验证动态 SQL 拼接结果。
        List<User> userList = userDao.selectList(queryIn);
        // 结果应至少包含初始化用户和两个新增 ACTIVE 用户。
        assertEquals(3, userList.size());
        // 第一条应该是最后插入的 ACTIVE 用户，证明倒序排序生效。
        assertEquals("filter-b@selfsp.local", userList.get(0).getEmail());
        // 第二条应是先插入的 ACTIVE 用户。
        assertEquals("filter-a@selfsp.local", userList.get(1).getEmail());
        // 初始化用户仍应出现在结果中，证明模糊名称条件把原数据也命中了。
        assertEquals("test@selfsp.local", userList.get(2).getEmail());
        // 列表中的每条记录都应满足 ACTIVE 状态过滤。
        assertTrue(userList.stream().allMatch(user -> "ACTIVE".equals(user.getStatus())));
    }

    /**
     * 构造 DAO 层测试用持久化对象。
     *
     * @param name 用户名称
     * @param email 用户邮箱
     * @param status 用户状态
     * @return 持久化对象
     */
    private User buildUser(String name, String email, String status) {
        // 新建持久化对象，直接模拟服务层交给 DAO 的入库结构。
        User user = new User();
        // 回填测试名称。
        user.setName(name);
        // 回填测试邮箱。
        user.setEmail(email);
        // 回填测试状态。
        user.setStatus(status);
        // 返回完整持久化对象。
        return user;
    }
}
