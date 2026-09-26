package cn.soriys.assessment.service;

import cn.soriys.assessment.dto.*;
import cn.soriys.assessment.entity.SysUser;
import cn.soriys.assessment.mapper.SysUserMapper;
import cn.soriys.assessment.security.JwtService;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Map;

@Service
public class AuthService {
    private final SysUserMapper mapper;
    private final PasswordEncoder encoder;
    private final JwtService jwt;

    public AuthService(SysUserMapper mapper, PasswordEncoder encoder, JwtService jwt) {
        this.mapper = mapper; this.encoder = encoder; this.jwt = jwt;
    }

    @Transactional
    public Map<String,Object> register(RegisterRequest r) {
        if (mapper.selectOne(Wrappers.<SysUser>lambdaQuery().eq(SysUser::getUsername, r.getUsername())) != null)
            throw new IllegalArgumentException("用户名已存在");
        SysUser u = new SysUser();
        u.setUsername(r.getUsername());
        u.setPassword(encoder.encode(r.getPassword()));
        u.setRealName(r.getRealName());
        u.setPhone(r.getPhone());
        u.setEnabled(true);
        u.setCreatedAt(LocalDateTime.now());
        u.setUpdatedAt(LocalDateTime.now());
        mapper.insert(u);
        return Map.of("id", u.getId(), "username", u.getUsername());
    }

    public Map<String,Object> login(LoginRequest r) {
        SysUser u = mapper.selectOne(Wrappers.<SysUser>lambdaQuery().eq(SysUser::getUsername, r.getUsername()));
        if (u == null || !Boolean.TRUE.equals(u.getEnabled()) || !encoder.matches(r.getPassword(), u.getPassword()))
            throw new IllegalArgumentException("用户名或密码错误");
        return Map.of("token", jwt.createToken(u.getId(), u.getUsername()),
                      "user", Map.of("id", u.getId(), "username", u.getUsername(), "realName", u.getRealName()));
    }

    @Transactional
    public void changePassword(Long userId, ChangePasswordRequest r) {
        SysUser u = mapper.selectById(userId);
        if (u == null || !encoder.matches(r.getOldPassword(), u.getPassword()))
            throw new IllegalArgumentException("原密码错误");
        u.setPassword(encoder.encode(r.getNewPassword()));
        u.setUpdatedAt(LocalDateTime.now());
        mapper.updateById(u);
    }

    @Transactional
    public void updateProfile(Long userId, String realName, String phone) {
        SysUser u = mapper.selectById(userId);
        if (u == null) throw new IllegalArgumentException("用户不存在");
        u.setRealName(realName); u.setPhone(phone); u.setUpdatedAt(LocalDateTime.now());
        mapper.updateById(u);
    }
}
