package com.sp.selfsp.user.controller;

// ObjectMapper 负责把测试入参序列化成 JSON。
import com.fasterxml.jackson.databind.ObjectMapper;
// SpringBootTest 用于启动完整 Spring 容器验证真实接口链路。
import org.junit.jupiter.api.Test;
// Autowired 用于注入测试所需组件。
import org.springframework.beans.factory.annotation.Autowired;
// AutoConfigureMockMvc 用于注入 MockMvc。
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
// SpringBootTest 负责拉起完整上下文。
import org.springframework.boot.test.context.SpringBootTest;
// MediaType 负责声明 JSON 请求头。
import org.springframework.http.MediaType;
// Sql 负责每次测试前重建最小数据库数据。
import org.springframework.test.context.jdbc.Sql;
// MockMvc 负责模拟真实 HTTP 请求。
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 用户控制器集成测试。
 */
@SpringBootTest
@AutoConfigureMockMvc
@Sql(scripts = "classpath:reset-user-test-data.sql")
public class UserControllerTest {

    // MockMvc 用于模拟真实的 API 调用流程。
    @Autowired
    private MockMvc mockMvc;

    // ObjectMapper 用于构建 JSON 请求体，避免手写字符串出错。
    @Autowired
    private ObjectMapper objectMapper;

    /**
     * 验证用户列表接口可返回初始化数据。
     *
     * @throws Exception 测试异常
     */
    @Test
    public void shouldReturnUserList() throws Exception {
        // 发起真实列表请求，验证控制器、服务、DAO 和 H2 初始化是否连通。
        mockMvc.perform(get("/api/users").param("name", "测试用户"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(0))
            .andExpect(jsonPath("$.data[0].name").value("测试用户"))
            .andExpect(jsonPath("$.data[0].email").value("test@selfsp.local"));
    }

    /**
     * 验证新增用户接口可写入并返回数据库主键。
     *
     * @throws Exception 测试异常
     */
    @Test
    public void shouldCreateUser() throws Exception {
        // 组装最小新增入参，覆盖新增主路径。
        String requestBody = toJson(new SaveUserRequest("新用户", "new@selfsp.local", "ACTIVE"));
        // 发起新增请求，验证统一响应、主键回填和字段落库。
        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(0))
            .andExpect(jsonPath("$.data.id").isNumber())
            .andExpect(jsonPath("$.data.name").value("新用户"))
            .andExpect(jsonPath("$.data.email").value("new@selfsp.local"));
    }

    /**
     * 验证用户详情接口可按主键返回初始化用户。
     *
     * @throws Exception 测试异常
     */
    @Test
    public void shouldReturnUserDetailById() throws Exception {
        // 按初始化主键查询详情，验证 controller 到 DAO 的单查链路。
        mockMvc.perform(get("/api/users/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(0))
            .andExpect(jsonPath("$.data.id").value(1))
            .andExpect(jsonPath("$.data.name").value("测试用户"))
            .andExpect(jsonPath("$.data.status").value("ACTIVE"));
    }

    /**
     * 验证更新接口可修改用户并返回最新结果。
     *
     * @throws Exception 测试异常
     */
    @Test
    public void shouldUpdateUser() throws Exception {
        // 组装更新请求，覆盖修改主路径和返回最新值。
        String requestBody = toJson(new SaveUserRequest("已更新用户", "updated@selfsp.local", "INACTIVE"));
        // 发起更新请求，验证修改成功后返回的就是最新数据库状态。
        mockMvc.perform(put("/api/users/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(0))
            .andExpect(jsonPath("$.data.id").value(1))
            .andExpect(jsonPath("$.data.name").value("已更新用户"))
            .andExpect(jsonPath("$.data.email").value("updated@selfsp.local"))
            .andExpect(jsonPath("$.data.status").value("INACTIVE"));
    }

    /**
     * 验证删除接口可清除记录，后续再查会返回不存在错误。
     *
     * @throws Exception 测试异常
     */
    @Test
    public void shouldDeleteUserAndRejectFollowUpQuery() throws Exception {
        // 先删除初始化用户，覆盖删除主路径。
        mockMvc.perform(delete("/api/users/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(0))
            .andExpect(jsonPath("$.data").doesNotExist());
        // 删除后再次单查，验证服务层存在性校验仍然生效。
        mockMvc.perform(get("/api/users/1"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code").value(400))
            .andExpect(jsonPath("$.message").value("用户不存在，id=1"));
    }

    /**
     * 验证非法主键会被统一异常收口成 400。
     *
     * @throws Exception 测试异常
     */
    @Test
    public void shouldRejectInvalidId() throws Exception {
        // 直接传入非法主键，验证控制器和异常处理器的失败契约。
        mockMvc.perform(get("/api/users/0"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code").value(400))
            .andExpect(jsonPath("$.message").value("id 必须大于 0"));
    }

    /**
     * 验证新增重复邮箱会返回业务唯一性错误。
     *
     * @throws Exception 测试异常
     */
    @Test
    public void shouldRejectDuplicateEmailOnCreate() throws Exception {
        // 使用初始化用户邮箱构造新增请求，覆盖唯一约束错误路径。
        String requestBody = toJson(new SaveUserRequest("重复邮箱用户", "test@selfsp.local", "ACTIVE"));
        // 发起新增请求，验证服务层唯一性校验和统一错误壳。
        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code").value(400))
            .andExpect(jsonPath("$.message").value("email 已存在"));
    }

    /**
     * 验证列表接口可按状态过滤并返回最新插入的匹配用户。
     *
     * @throws Exception 测试异常
     */
    @Test
    public void shouldFilterUserListByStatus() throws Exception {
        // 先创建一个停用用户，给状态过滤准备样本数据。
        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(new SaveUserRequest("停用用户", "inactive@selfsp.local", "INACTIVE"))))
            .andExpect(status().isOk());
        // 按状态过滤列表，验证查询参数最终生效到 SQL 条件。
        mockMvc.perform(get("/api/users").param("status", "INACTIVE"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(0))
            .andExpect(jsonPath("$.data[0].email").value("inactive@selfsp.local"))
            .andExpect(jsonPath("$.data[0].status").value("INACTIVE"));
    }

    /**
     * 把测试请求对象转成 JSON。
     *
     * @param request 测试请求对象
     * @return JSON 文本
     * @throws Exception 序列化异常
     */
    private String toJson(SaveUserRequest request) throws Exception {
        // 统一通过 ObjectMapper 序列化，保持所有写接口请求格式一致。
        return objectMapper.writeValueAsString(request);
    }

    /**
     * 测试内部请求体。
     */
    private static class SaveUserRequest {

        // 新增接口的用户名称。
        private final String name;

        // 新增接口的用户邮箱。
        private final String email;

        // 新增接口的用户状态。
        private final String status;

        /**
         * 构造测试请求体。
         *
         * @param name 用户名称
         * @param email 用户邮箱
         * @param status 用户状态
         */
        private SaveUserRequest(String name, String email, String status) {
            // 保存测试名称。
            this.name = name;
            // 保存测试邮箱。
            this.email = email;
            // 保存测试状态。
            this.status = status;
        }

        /**
         * 获取名称。
         *
         * @return 名称
         */
        public String getName() {
            // 返回测试名称。
            return name;
        }

        /**
         * 获取邮箱。
         *
         * @return 邮箱
         */
        public String getEmail() {
            // 返回测试邮箱。
            return email;
        }

        /**
         * 获取状态。
         *
         * @return 状态
         */
        public String getStatus() {
            // 返回测试状态。
            return status;
        }
    }
}
