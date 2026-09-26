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

    private AssessmentRecord convert(AssessmentRequest r) {
        AssessmentRecord e = new AssessmentRecord();
        e.setNo(r.getNo()); e.setAssessmentDate(r.getAssessmentDate()); e.setReason(r.getReason());
        e.setElderlyName(r.getElderlyName()); e.setGender(r.getGender()); e.setBirthDate(r.getBirthDate());
        e.setHeight(r.getHeight()); e.setWeight(r.getWeight()); e.setEthnicity(r.getEthnicity());
        e.setReligion(r.getReligion()); e.setIdNo(r.getIdNo()); e.setEducation(r.getEducation());
        e.setLiving(r.getLiving()); e.setMarriage(r.getMarriage()); e.setRisks(r.getRisks());
        e.setProvider(r.getProvider()); e.setDisease(r.getDisease()); e.setDiseaseOther(r.getDiseaseOther());
        e.setMedications(r.getMedications()); e.setHealth(r.getHealth()); e.setAnswers(r.getAnswers());
        e.setTotalScore(r.getTotalScore()); e.setInitialLevel(r.getInitialLevel()); e.setFinalLevel(r.getFinalLevel());
        return e;
    }

    @Transactional
    public AssessmentRecord create(AssessmentRequest r, Long userId) {
        AssessmentRecord e = convert(r);
        e.setCreatedBy(userId); e.setCreatedAt(LocalDateTime.now()); e.setUpdatedAt(LocalDateTime.now());
        mapper.insert(e); return e;
    }

    @Transactional
    public AssessmentRecord update(Long id, AssessmentRequest r, Long userId) {
        AssessmentRecord e = convert(r);
        e.setId(id); e.setCreatedBy(userId); e.setUpdatedAt(LocalDateTime.now());
        if (mapper.updateById(e) == 0) throw new IllegalArgumentException("评估记录不存在");
        return mapper.selectById(id);
    }

    public AssessmentRecord get(Long id) {
        AssessmentRecord e = mapper.selectById(id);
        if (e == null) throw new IllegalArgumentException("评估记录不存在");
        return e;
    }

    public IPage<AssessmentRecord> page(long current, long size, String keyword) {
        return mapper.selectPage(new Page<>(current, size),
            Wrappers.<AssessmentRecord>lambdaQuery()
                .and(keyword != null && !keyword.isBlank(),
                    q -> q.like(AssessmentRecord::getElderlyName, keyword)
                          .or().like(AssessmentRecord::getNo, keyword)
                          .or().like(AssessmentRecord::getIdNo, keyword))
                .orderByDesc(AssessmentRecord::getCreatedAt));
    }

    @Transactional
    public void delete(Long id) {
        if (mapper.deleteById(id) == 0) throw new IllegalArgumentException("评估记录不存在");
    }
}
