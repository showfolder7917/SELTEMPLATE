package com.sp.selfsp.user.service.impl;

// DAO 负责实际数据库访问。
import com.sp.selfsp.user.dao.UserDao;
// 持久化对象负责承接数据库字段。
import com.sp.selfsp.user.domain.User;
// 查询入参定义查询边界。
import com.sp.selfsp.user.domain.in.UserQueryIn;
// 保存入参定义新增和修改边界。
import com.sp.selfsp.user.domain.in.UserSaveIn;
// 输出对象定义接口返回结构。
import com.sp.selfsp.user.domain.out.UserDetailOut;
// 服务接口用于保持实现和调用边界清晰。
import com.sp.selfsp.user.service.UserService;
// List 用于承接批量数据返回。
import java.util.List;
// 流式转换用于把持久化对象映射成接口出参。
import java.util.stream.Collectors;
// Service 负责把当前类注册为业务服务。
import org.springframework.stereotype.Service;
// Transactional 负责把写操作包在事务里。
import org.springframework.transaction.annotation.Transactional;
// StringUtils 用于稳定判断文本是否为空。
import org.springframework.util.StringUtils;

/**
 * 用户服务实现。
 */
@Service
public class UserServiceImpl implements UserService {

    // 用户服务只承接业务校验、唯一性约束和对象映射。
    private final UserDao userDao;

    /**
     * 构造用户服务。
     *
     * @param userDao 用户数据访问接口
     */
    public UserServiceImpl(UserDao userDao) {
        // 注入 DAO，后续所有数据读写都从统一持久化边界进入。
        this.userDao = userDao;
    }

    /**
     * 新增用户。
     *
     * @param saveIn 保存入参
     * @return 用户详情
     */
    @Override
    @Transactional
    public UserDetailOut create(UserSaveIn saveIn) {
        // 先校验入参，避免脏数据进入数据库。
        validateSaveIn(saveIn, "新增用户入参不能为空");
        // 新增前先校验邮箱唯一性，保证业务唯一约束清晰。
        ensureEmailUnique(saveIn.getEmail(), null);
        // 把接口入参转成持久化对象，隔离 controller 和数据库模型。
        User user = toUser(saveIn);
        // 由数据库生成主键并回填到对象。
        userDao.insert(user);
        // 新增后回查完整数据，统一返回数据库实际落地结果。
        return toDetailOut(requireExisting(user.getId()));
    }

    /**
     * 更新用户。
     *
     * @param id 用户主键
     * @param saveIn 保存入参
     * @return 用户详情
     */
    @Override
    @Transactional
    public UserDetailOut update(Long id, UserSaveIn saveIn) {
        // 主键必须合法，才能进入修改链路。
        validateId(id);
        // 修改入参和新增一样要做完整校验。
        validateSaveIn(saveIn, "修改用户入参不能为空");
        // 先确认记录存在，避免更新不存在记录时静默成功。
        requireExisting(id);
        // 修改前校验邮箱是否被其他用户占用。
        ensureEmailUnique(saveIn.getEmail(), id);
        // 将入参映射为持久化对象，保持修改字段来源明确。
        User user = toUser(saveIn);
        // 把路径主键回填到更新对象，确保更新目标唯一。
        user.setId(id);
        // 提交数据库更新。
        userDao.updateById(user);
        // 返回更新后的最新结果。
        return toDetailOut(requireExisting(id));
    }

    /**
     * 删除用户。
     *
     * @param id 用户主键
     */
    @Override
    @Transactional
    public void delete(Long id) {
        // 删除前先校验主键格式。
        validateId(id);
        // 删除前先确认记录存在，保持错误语义一致。
        requireExisting(id);
        // 真正执行删除。
        userDao.deleteById(id);
    }

    /**
     * 查询单个用户。
     *
     * @param id 用户主键
     * @return 用户详情
     */
    @Override
    public UserDetailOut getById(Long id) {
        // 单查也必须校验主键格式。
        validateId(id);
        // 返回已存在的用户详情。
        return toDetailOut(requireExisting(id));
    }

    /**
     * 查询用户列表。
     *
     * @param queryIn 查询入参
     * @return 用户列表
     */
    @Override
    public List<UserDetailOut> list(UserQueryIn queryIn) {
        // 列表查询允许空条件，因此统一兜底为空对象。
        UserQueryIn safeQueryIn = queryIn == null ? new UserQueryIn() : queryIn;
        // DAO 返回持久化对象后，统一在服务层转换为对外输出对象。
        return userDao.selectList(safeQueryIn).stream().map(this::toDetailOut).collect(Collectors.toList());
    }

    /**
     * 校验主键。
     *
     * @param id 用户主键
     * @throws IllegalArgumentException 当主键为空或不大于 0 时抛出
     */
    private void validateId(Long id) {
        // 非法主键直接阻断，避免无意义数据库访问。
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("id 必须大于 0");
        }
    }

    /**
     * 校验保存入参。
     *
     * @param saveIn 保存入参
     * @param nullMessage 空对象提示
     * @throws IllegalArgumentException 当入参为空或关键字段不合法时抛出
     */
    private void validateSaveIn(UserSaveIn saveIn, String nullMessage) {
        // 对象为空时直接阻断本次写操作。
        if (saveIn == null) {
            throw new IllegalArgumentException(nullMessage);
        }
        // 名称为空会导致用户资料不可识别，因此必须拦截。
        if (!StringUtils.hasText(saveIn.getName())) {
            throw new IllegalArgumentException("name 不能为空");
        }
        // 邮箱既承担联系语义又承担唯一键语义，因此必须校验格式。
        if (!StringUtils.hasText(saveIn.getEmail()) || !saveIn.getEmail().contains("@")) {
            throw new IllegalArgumentException("email 格式不正确");
        }
        // 状态为空会影响后续筛选和状态流转，因此必须拦截。
        if (!StringUtils.hasText(saveIn.getStatus())) {
            throw new IllegalArgumentException("status 不能为空");
        }
    }

    /**
     * 校验邮箱唯一性。
     *
     * @param email 邮箱
     * @param currentId 当前修改记录主键，新增时为空
     * @throws IllegalArgumentException 当邮箱已被其他用户占用时抛出
     */
    private void ensureEmailUnique(String email, Long currentId) {
        // 先按邮箱查询现有记录，复用数据库唯一索引语义。
        User existing = userDao.selectByEmail(email.trim());
        // 没有重名邮箱时直接放行。
        if (existing == null) {
            return;
        }
        // 修改本人资料时允许保留同一邮箱，不应误判冲突。
        if (currentId != null && currentId.equals(existing.getId())) {
            return;
        }
        // 其他情况统一报唯一性冲突。
        throw new IllegalArgumentException("email 已存在");
    }

    /**
     * 保证用户存在。
     *
     * @param id 用户主键
     * @return 用户对象
     * @throws IllegalArgumentException 当目标用户不存在时抛出
     */
    private User requireExisting(Long id) {
        // 从 DAO 查询真实记录。
        User user = userDao.selectById(id);
        // 查不到时直接返回清晰业务错误。
        if (user == null) {
            throw new IllegalArgumentException("用户不存在，id=" + id);
        }
        // 返回已存在用户。
        return user;
    }

    /**
     * 将保存入参映射成持久化对象。
     *
     * @param saveIn 保存入参
     * @return 持久化对象
     */
    private User toUser(UserSaveIn saveIn) {
        // 新建持久化对象，避免直接把接口对象传入 DAO。
        User user = new User();
        // 归一化名称，消除前后空格。
        user.setName(saveIn.getName().trim());
        // 归一化邮箱，消除前后空格。
        user.setEmail(saveIn.getEmail().trim());
        // 归一化状态，消除前后空格。
        user.setStatus(saveIn.getStatus().trim());
        // 返回标准持久化对象。
        return user;
    }

    /**
     * 将持久化对象映射成接口出参。
     *
     * @param user 持久化对象
     * @return 接口出参
     */
    private UserDetailOut toDetailOut(User user) {
        // 新建出参对象，隔离数据库字段和接口契约。
        UserDetailOut detailOut = new UserDetailOut();
        // 回填主键。
        detailOut.setId(user.getId());
        // 回填名称。
        detailOut.setName(user.getName());
        // 回填邮箱。
        detailOut.setEmail(user.getEmail());
        // 回填状态。
        detailOut.setStatus(user.getStatus());
        // 回填创建时间。
        detailOut.setCreatedAt(user.getCreatedAt());
        // 回填更新时间。
        detailOut.setUpdatedAt(user.getUpdatedAt());
        // 返回接口结果。
        return detailOut;
    }
}
