package cn.soriys.assessment.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import cn.soriys.assessment.entity.SysApp;
import cn.soriys.assessment.mapper.SysAppMapper;
import cn.soriys.assessment.service.SysAppService;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.stereotype.Service;

@Service
public class SysAppService extends ServiceImpl<SysAppMapper, SysApp>  {
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
}