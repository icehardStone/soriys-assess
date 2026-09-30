package cn.soriys.assessment.service;

import cn.soriys.assessment.dto.AssessmentRequest;
import cn.soriys.assessment.entity.AssessmentRecord;
import cn.soriys.assessment.entity.SysUser;
import cn.soriys.assessment.mapper.AssessmentRecordMapper;
import cn.soriys.assessment.mapper.SysUserMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class AssessmentService {
    private final AssessmentRecordMapper mapper;
    private final SysUserMapper userMapper;
    public AssessmentService(AssessmentRecordMapper mapper, SysUserMapper userMapper) { 
        this.mapper = mapper; 
        this.userMapper = userMapper;
    }

    private AssessmentConverter convert = new AssessmentConverter();

    private Long getCurrentUserAppId() {
        Long userId = Long.valueOf(SecurityContextHolder.getContext().getAuthentication().getName());
        SysUser user = userMapper.selectById(userId);
        return user != null ? user.getAppId() : null;
    }

    @Transactional
    public AssessmentRecord create(AssessmentRequest r, Long userId) {
        AssessmentRecord e = convert.from_convert(r);
        Long appId = getCurrentUserAppId();
        e.setAppId(appId);
        e.setCreatedBy(userId); 
        e.setCreatedAt(LocalDateTime.now()); 
        e.setUpdatedAt(LocalDateTime.now());
        mapper.insert(e); 
        return e;
    }

    @Transactional
    public AssessmentRecord update(Long id, AssessmentRequest r, Long userId) {
        AssessmentRecord e = convert.from_convert(r);
        Long appId = getCurrentUserAppId();
        e.setId(id); 
        e.setAppId(appId);
        e.setCreatedBy(userId); 
        e.setUpdatedAt(LocalDateTime.now());
        if (mapper.updateById(e) == 0) throw new IllegalArgumentException("评估记录不存在");
        return mapper.selectById(id);
    }

    public AssessmentRequest get(Long id) {
        AssessmentRecord e = mapper.selectById(id);
        if (e == null) throw new IllegalArgumentException("评估记录不存在");
        // Verify that the record belongs to current user's app
        Long currentAppId = getCurrentUserAppId();
        if (currentAppId != null && !currentAppId.equals(e.getAppId())) {
            throw new IllegalArgumentException("无权访问此评估记录");
        }
        AssessmentRequest r = convert.to_convert(e);
        return r;
    }

    public IPage<AssessmentRequest> page(long current, long size, String keyword) {
        Long appId = getCurrentUserAppId();
        // 1. 查询 Entity 分页
        IPage<AssessmentRecord> entityPage = mapper.selectPage(
            new Page<>(current, size),
            Wrappers.<AssessmentRecord>lambdaQuery()
                .eq(appId != null, AssessmentRecord::getAppId, appId)
                .and(keyword != null && !keyword.isBlank(),
                    q -> q.like(AssessmentRecord::getElderlyName, keyword)
                        .or().like(AssessmentRecord::getNo, keyword)
                        .or().like(AssessmentRecord::getIdNo, keyword))
                .orderByDesc(AssessmentRecord::getCreatedAt)
        );

        // 2. Entity 分页 -> Request 分页
        return entityPage.convert(convert::to_convert);
    }

    /**
     * Returns the complete assessment distribution for the dashboard, filtered by user's app.
     */
    public Map<String, Long> statistics() {
        Long appId = getCurrentUserAppId();
        Map<String, Long> statistics = new LinkedHashMap<>();
        statistics.put("total", mapper.selectCount(Wrappers.<AssessmentRecord>lambdaQuery()
            .eq(appId != null, AssessmentRecord::getAppId, appId)));
        statistics.put("level0", 0L);
        statistics.put("level1", 0L);
        statistics.put("level2", 0L);
        statistics.put("level3", 0L);
        statistics.put("level4", 0L);

        List<Map<String, Object>> grouped = mapper.selectMaps(
                Wrappers.<AssessmentRecord>query()
                        .eq(appId != null ? "app_id" : "1", appId != null ? appId : 1)
                        .select("final_level", "COUNT(*) AS count")
                        .groupBy("final_level")
        );
        for (Map<String, Object> row : grouped) {
            Object levelValue = row.get("final_level");
            Object countValue = row.get("count");
            if (levelValue instanceof Number level && countValue instanceof Number count
                    && level.intValue() >= 0 && level.intValue() <= 4) {
                statistics.put("level" + level.intValue(), count.longValue());
            }
        }
        return statistics;
    }

    @Transactional
    public void delete(Long id) {
        if (mapper.deleteById(id) == 0) throw new IllegalArgumentException("评估记录不存在");
    }
}
