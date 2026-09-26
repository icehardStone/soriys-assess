package cn.soriys.assessment.controller;

import cn.soriys.assessment.dto.*;
import cn.soriys.assessment.entity.SysUser;
import cn.soriys.assessment.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService service;
    public AuthController(AuthService service) { this.service = service; }

    @PostMapping("/register")
    public Map<String,Object> register(@Valid @RequestBody RegisterRequest r) { return service.register(r); }

    @PostMapping("/login")
    public Map<String,Object> login(@Valid @RequestBody LoginRequest r) { return service.login(r); }

    @PutMapping("/password")
    public Map<String,Object> password(@Valid @RequestBody ChangePasswordRequest r) {
        service.changePassword(currentUserId(), r); return Map.of("message","密码修改成功");
    }

    @PutMapping("/profile")
    public Map<String,Object> profile(@Valid @RequestBody  ProfileRequest r) {
        service.updateProfile(r);
        return Map.of("message","资料修改成功");
    }

    private Long currentUserId() {
        return Long.valueOf(SecurityContextHolder.getContext().getAuthentication().getName());
    }
}
