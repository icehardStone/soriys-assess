package cn.soriys.assessment.controller;

import cn.soriys.assessment.entity.SysApp;
import cn.soriys.assessment.service.SysAppService;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/apps")
public class SysAppController {

    private final SysAppService sysAppService;

    public SysAppController(SysAppService sysAppService) {
        this.sysAppService = sysAppService;
    }

    @GetMapping
    public List<SysApp> list() {
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
}