package cn.soriys.assessment.service;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import cn.soriys.assessment.dto.InviteRequest;
import cn.soriys.assessment.entity.SysApp;
import cn.soriys.assessment.entity.SysUser;
import cn.soriys.assessment.mapper.SysAppMapper;
import cn.soriys.assessment.mapper.SysUserMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class SysAppService extends ServiceImpl<SysAppMapper, SysApp> {

    private final SysUserMapper userMapper;
    private final PasswordEncoder encoder;

    public SysAppService(SysUserMapper userMapper, PasswordEncoder encoder) {
        this.userMapper = userMapper;
        this.encoder = encoder;
    }

    public SysApp create(SysApp app) {
        app.setAppKey(UUID.randomUUID().toString().replace("-", ""));
        // 生成 64 位十六进制 secret
        app.setAppSecret(java.util.UUID.randomUUID().toString().replace("-", "")
                + java.util.UUID.randomUUID().toString().replace("-", ""));
        app.setEnabled(true);
        app.setCreatedAt(LocalDateTime.now());
        app.setUpdatedAt(LocalDateTime.now());
        save(app);
        return app;
    }

    /**
     * 获取应用详情（含应用下所有用户、当前用户是否为创建者）
     */
    public Map<String, Object> getAppDetail(Long appId, Long currentUserId) {
        SysApp app = getById(appId);
        if (app == null) {
            throw new IllegalArgumentException("应用不存在");
        }
        List<SysUser> users = userMapper.selectList(
                Wrappers.<SysUser>lambdaQuery().eq(SysUser::getAppId, appId)
                        .orderByAsc(SysUser::getCreatedAt));

        boolean isCreator = app.getCreatorId() != null && app.getCreatorId().equals(currentUserId);

        Map<String, Object> result = new HashMap<>();
        result.put("app", app);
        result.put("users", users);
        result.put("isCreator", isCreator);
        return result;
    }

    /**
     * 邀请用户加入当前应用（仅应用创建者可操作）
     */
    @Transactional
    public Map<String, Object> inviteUser(Long creatorId, InviteRequest r) {
        SysUser creator = userMapper.selectById(creatorId);
        if (creator == null || creator.getAppId() == null) {
            throw new IllegalArgumentException("用户未关联应用");
        }
        SysApp app = getById(creator.getAppId());
        if (app == null) {
            throw new IllegalArgumentException("应用不存在");
        }
        if (app.getCreatorId() == null || !app.getCreatorId().equals(creatorId)) {
            throw new IllegalArgumentException("仅应用创建者可邀请用户");
        }

        if (userMapper.selectOne(Wrappers.<SysUser>lambdaQuery().eq(SysUser::getUsername, r.getUsername())) != null) {
            throw new IllegalArgumentException("用户名已存在");
        }

        SysUser u = new SysUser();
        u.setUsername(r.getUsername());
        u.setPassword(encoder.encode(r.getPassword()));
        u.setRealName(r.getRealName());
        u.setPhone(r.getPhone());
        u.setEmail(r.getEmail());
        u.setAppId(app.getId());
        u.setEnabled(true);
        u.setCreatedAt(LocalDateTime.now());
        u.setUpdatedAt(LocalDateTime.now());
        userMapper.insert(u);

        return Map.of("id", u.getId(), "username", u.getUsername());
    }
}
