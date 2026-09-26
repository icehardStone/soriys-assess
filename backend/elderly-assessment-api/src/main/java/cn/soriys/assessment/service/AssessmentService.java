package cn.soriys.assessment.service;

import cn.soriys.assessment.dto.AssessmentRequest;
import cn.soriys.assessment.entity.AssessmentRecord;
import cn.soriys.assessment.mapper.AssessmentRecordMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class AssessmentService {
    private final AssessmentRecordMapper mapper;
    public AssessmentService(AssessmentRecordMapper mapper) { this.mapper = mapper; }

    private  AssessmentConverter convert = new AssessmentConverter();
    @Transactional
    public AssessmentRecord create(AssessmentRequest r, Long userId) {
        AssessmentRecord e = convert.from_convert(r);
        e.setCreatedBy(userId); e.setCreatedAt(LocalDateTime.now()); e.setUpdatedAt(LocalDateTime.now());
        mapper.insert(e); return e;
    }

    @Transactional
    public AssessmentRecord update(Long id, AssessmentRequest r, Long userId) {
        AssessmentRecord e = convert.from_convert(r);
        e.setId(id); e.setCreatedBy(userId); e.setUpdatedAt(LocalDateTime.now());
        if (mapper.updateById(e) == 0) throw new IllegalArgumentException("评估记录不存在");
        return mapper.selectById(id);
    }

    public AssessmentRequest get(Long id) {
        AssessmentRecord e = mapper.selectById(id);
        if (e == null) throw new IllegalArgumentException("评估记录不存在");
        AssessmentRequest r = convert.to_convert(e);
        return r;
    }

    public IPage<AssessmentRequest> page(long current, long size, String keyword) {
        // 1. 查询 Entity 分页
        IPage<AssessmentRecord> entityPage = mapper.selectPage(
            new Page<>(current, size),
            Wrappers.<AssessmentRecord>lambdaQuery()
                .and(keyword != null && !keyword.isBlank(),
                    q -> q.like(AssessmentRecord::getElderlyName, keyword)
                        .or().like(AssessmentRecord::getNo, keyword)
                        .or().like(AssessmentRecord::getIdNo, keyword))
                .orderByDesc(AssessmentRecord::getCreatedAt)
        );

        // 2. Entity 分页 -> Request 分页
        return entityPage.convert(convert::to_convert);
    }

    @Transactional
    public void delete(Long id) {
        if (mapper.deleteById(id) == 0) throw new IllegalArgumentException("评估记录不存在");
    }
}
