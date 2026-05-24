package com.sp.selfsp.user.controller;

// 统一响应壳用于保持所有接口响应结构一致。
import com.sp.selfsp.common.util.CommonResponse;
// 查询入参定义列表过滤边界。
import com.sp.selfsp.user.domain.in.UserQueryIn;
// 保存入参定义新增和修改边界。
import com.sp.selfsp.user.domain.in.UserSaveIn;
// 输出对象定义接口返回数据结构。
import com.sp.selfsp.user.domain.out.UserDetailOut;
// 服务层负责实际业务编排。
import com.sp.selfsp.user.service.UserService;
// List 用于承接列表结果。
import java.util.List;
// DeleteMapping 负责暴露删除接口。
import org.springframework.web.bind.annotation.DeleteMapping;
// GetMapping 负责暴露查询接口。
import org.springframework.web.bind.annotation.GetMapping;
// PathVariable 负责读取路径主键。
import org.springframework.web.bind.annotation.PathVariable;
// PostMapping 负责暴露新增接口。
import org.springframework.web.bind.annotation.PostMapping;
// PutMapping 负责暴露更新接口。
import org.springframework.web.bind.annotation.PutMapping;
// RequestBody 负责读取 JSON 请求体。
import org.springframework.web.bind.annotation.RequestBody;
// RequestMapping 负责声明模块统一路径前缀。
import org.springframework.web.bind.annotation.RequestMapping;
// RequestParam 负责读取列表过滤参数。
import org.springframework.web.bind.annotation.RequestParam;
// RestController 负责把返回值直接序列化成 JSON。
import org.springframework.web.bind.annotation.RestController;

/**
 * 用户接口控制器。
 */
@RestController
@RequestMapping("/api/users")
public class UserController {

    // 控制器只负责协议层收口，业务编排全部交给服务层。
    private final UserService userService;

    /**
     * 构造用户控制器。
     *
     * @param userService 用户服务
     */
    public UserController(UserService userService) {
        // 注入用户服务，所有业务动作都走统一服务边界。
        this.userService = userService;
    }

    /**
     * 新增用户。
     *
     * @param saveIn 保存入参
     * @return 统一响应
     */
    @PostMapping
    public CommonResponse<UserDetailOut> create(@RequestBody UserSaveIn saveIn) {
        // POST 请求只负责接收入参并调用服务层。
        return CommonResponse.success(userService.create(saveIn));
    }

    /**
     * 更新用户。
     *
     * @param id 用户主键
     * @param saveIn 保存入参
     * @return 统一响应
     */
    @PutMapping("/{id}")
    public CommonResponse<UserDetailOut> update(@PathVariable Long id, @RequestBody UserSaveIn saveIn) {
        // 路径主键和请求体一起交给服务层统一处理。
        return CommonResponse.success(userService.update(id, saveIn));
    }

    /**
     * 删除用户。
     *
     * @param id 用户主键
     * @return 统一响应
     */
    @DeleteMapping("/{id}")
    public CommonResponse<Void> delete(@PathVariable Long id) {
        // 删除动作只返回统一成功壳，不额外拼装业务数据。
        userService.delete(id);
        // 删除成功后 data 返回 null。
        return CommonResponse.success(null);
    }

    /**
     * 查询单个用户。
     *
     * @param id 用户主键
     * @return 统一响应
     */
    @GetMapping("/{id}")
    public CommonResponse<UserDetailOut> getById(@PathVariable Long id) {
        // 单查场景直接透传服务层结果。
        return CommonResponse.success(userService.getById(id));
    }

    /**
     * 查询用户列表。
     *
     * @param name 名称关键字
     * @param status 状态
     * @return 统一响应
     */
    @GetMapping
    public CommonResponse<List<UserDetailOut>> list(
        @RequestParam(required = false) String name,
        @RequestParam(required = false) String status
    ) {
        // 列表请求先组装查询对象，避免控制器直接把散参数下传到 DAO。
        UserQueryIn queryIn = new UserQueryIn();
        // 回填名称关键字。
        queryIn.setName(name);
        // 回填状态。
        queryIn.setStatus(status);
        // 返回统一列表结果。
        return CommonResponse.success(userService.list(queryIn));
    }
}
