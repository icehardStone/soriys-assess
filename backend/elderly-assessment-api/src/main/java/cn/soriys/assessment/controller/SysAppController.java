package cn.soriys.assessment.controller;

import cn.soriys.assessment.dto.InviteRequest;
import cn.soriys.assessment.entity.SysApp;
import cn.soriys.assessment.entity.SysUser;
import cn.soriys.assessment.mapper.SysUserMapper;
import cn.soriys.assessment.service.SysAppService;
import jakarta.validation.Valid;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api/apps")
public class SysAppController {

    private final SysAppService sysAppService;
    private final SysUserMapper userMapper;

    public SysAppController(SysAppService sysAppService, SysUserMapper userMapper) {
        this.sysAppService = sysAppService;
        this.userMapper = userMapper;
    }

    @GetMapping
    public java.util.List<SysApp> list() {
        return sysAppService.list();
    }

    @PostMapping
    public SysApp create(@RequestBody SysApp app) {
        sysAppService.create(app);
        return app;
    }

    @PutMapping("/{id}")
    public SysApp update(@PathVariable Long id, @RequestBody SysApp app) {
        app.setId(id);
        app.setUpdatedAt(LocalDateTime.now());
        sysAppService.updateById(app);
        return sysAppService.getById(id);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        sysAppService.removeById(id);
    }

    /**
     * 获取当前用户所在的应用信息（含应用下所有用户、是否为创建者）
     */
    @GetMapping("/current")
    public Map<String, Object> current() {
        Long userId = currentUserId();
        SysUser user = userMapper.selectById(userId);
        if (user == null || user.getAppId() == null) {
            throw new IllegalArgumentException("用户未关联应用");
        }
        return sysAppService.getAppDetail(user.getAppId(), userId);
    }

    /**
     * 邀请用户加入当前应用（仅应用创建者可操作）
     */
    @PostMapping("/invite")
    public Map<String, Object> invite(@Valid @RequestBody InviteRequest r) {
        return sysAppService.inviteUser(currentUserId(), r);
    }

    private Long currentUserId() {
        return Long.valueOf(SecurityContextHolder.getContext().getAuthentication().getName());
    }
}
