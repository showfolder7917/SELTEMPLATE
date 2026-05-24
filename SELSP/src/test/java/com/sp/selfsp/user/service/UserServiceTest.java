package com.sp.selfsp.user.service;

// 断言工具用于验证服务层返回值和异常消息。
import static org.junit.jupiter.api.Assertions.assertEquals;
// 断言工具用于验证服务层返回集合大小。
import static org.junit.jupiter.api.Assertions.assertFalse;
// 断言工具用于验证非空业务结果。
import static org.junit.jupiter.api.Assertions.assertNotNull;
// 断言工具用于验证业务异常分支。
import static org.junit.jupiter.api.Assertions.assertThrows;

// 用户保存入参用于构造新增和修改业务动作。
import com.sp.selfsp.user.domain.in.UserSaveIn;
// 用户详情出参用于读取服务层返回的实际业务数据。
import com.sp.selfsp.user.domain.out.UserDetailOut;
// List 用于承接列表测试结果。
import java.util.List;
// JUnit Test 用于声明服务层测试用例。
import org.junit.jupiter.api.Test;
// Autowired 负责注入真实用户服务。
import org.springframework.beans.factory.annotation.Autowired;
// SpringBootTest 负责启动完整服务、事务和 DAO 依赖。
import org.springframework.boot.test.context.SpringBootTest;
// Sql 负责每次用例前重建最小数据库状态。
import org.springframework.test.context.jdbc.Sql;

/**
 * 用户服务集成测试。
 */
@SpringBootTest
@Sql(scripts = "classpath:reset-user-test-data.sql")
public class UserServiceTest {

    // 用户服务是真实业务入口，用于验证 service 层校验和编排分支。
    @Autowired
    private UserService userService;

    /**
     * 验证修改本人资料时保留原邮箱不会误判为重复。
     */
    @Test
    public void shouldAllowUpdateWhenKeepingOwnEmail() {
        // 组装与初始化数据同邮箱的修改入参，覆盖“本人保留邮箱”分支。
        UserSaveIn saveIn = buildSaveIn("保留邮箱用户", "test@selfsp.local", "ACTIVE");
        // 调用更新服务，验证唯一性校验不会误伤当前记录。
        UserDetailOut detailOut = userService.update(1L, saveIn);
        // 返回对象应该保留原主键，说明更新目标正确。
        assertEquals(1L, detailOut.getId());
        // 名称应更新为新值，证明修改动作真实落库。
        assertEquals("保留邮箱用户", detailOut.getName());
        // 邮箱保持原值且允许成功。
        assertEquals("test@selfsp.local", detailOut.getEmail());
    }

    /**
     * 验证列表查询传空条件对象时会走服务层兜底逻辑。
     */
    @Test
    public void shouldReturnUsersWhenListQueryIsNull() {
        // 直接传入 null，覆盖服务层 safeQueryIn 兜底逻辑。
        List<UserDetailOut> detailOutList = userService.list(null);
        // 返回结果不应为空集合，说明空条件已被转成全量查询。
        assertFalse(detailOutList.isEmpty());
        // 初始化用户应仍然可见。
        assertEquals("测试用户", detailOutList.get(0).getName());
    }

    /**
     * 验证更新不存在用户时会返回清晰业务异常。
     */
    @Test
    public void shouldRejectUpdateWhenUserMissing() {
        // 组装合法修改入参，把失败原因限定在“目标不存在”。
        UserSaveIn saveIn = buildSaveIn("不存在用户", "missing@selfsp.local", "ACTIVE");
        // 调用不存在主键，验证服务层存在性校验语义。
        IllegalArgumentException error = assertThrows(IllegalArgumentException.class, () -> userService.update(999L, saveIn));
        // 错误消息应明确指出不存在的主键。
        assertEquals("用户不存在，id=999", error.getMessage());
    }

    /**
     * 验证新增空状态会被服务层字段校验直接拦截。
     */
    @Test
    public void shouldRejectCreateWhenStatusBlank() {
        // 组装缺少状态的新增入参，覆盖保存入参校验分支。
        UserSaveIn saveIn = buildSaveIn("无状态用户", "blank-status@selfsp.local", " ");
        // 调用新增服务，验证 status 校验错误消息。
        IllegalArgumentException error = assertThrows(IllegalArgumentException.class, () -> userService.create(saveIn));
        // 错误消息应精确落在状态字段。
        assertEquals("status 不能为空", error.getMessage());
    }

    /**
     * 验证修改成其他用户邮箱时会命中唯一性校验。
     */
    @Test
    public void shouldRejectUpdateWhenEmailBelongsToAnotherUser() {
        // 先创建第二个用户，为“邮箱被别人占用”分支准备真实样本。
        UserDetailOut secondUser = userService.create(buildSaveIn("第二用户", "second@selfsp.local", "ACTIVE"));
        // 组装把原用户修改成第二用户邮箱的请求。
        UserSaveIn saveIn = buildSaveIn("冲突邮箱用户", "second@selfsp.local", "ACTIVE");
        // 调用修改服务，验证唯一性校验识别出跨用户冲突。
        IllegalArgumentException error = assertThrows(IllegalArgumentException.class, () -> userService.update(1L, saveIn));
        // 错误消息应返回统一唯一性提示。
        assertEquals("email 已存在", error.getMessage());
        // 额外确认第二个用户已真实创建，避免测试因样本未落库失真。
        assertNotNull(secondUser.getId());
    }

    /**
     * 构造用户保存入参。
     *
     * @param name 用户名称
     * @param email 用户邮箱
     * @param status 用户状态
     * @return 服务层测试入参
     */
    private UserSaveIn buildSaveIn(String name, String email, String status) {
        // 新建保存入参，模拟控制器传给服务层的标准对象。
        UserSaveIn saveIn = new UserSaveIn();
        // 回填测试名称。
        saveIn.setName(name);
        // 回填测试邮箱。
        saveIn.setEmail(email);
        // 回填测试状态。
        saveIn.setStatus(status);
        // 返回完整保存入参。
        return saveIn;
    }
}
