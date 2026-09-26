package cn.soriys.assessment.service;


import cn.soriys.assessment.dto.AssessmentRequest;
import cn.soriys.assessment.dto.BasicInfo;
import cn.soriys.assessment.dto.HealthInfo;
import cn.soriys.assessment.dto.MedicationInfo;
import cn.soriys.assessment.dto.ProviderInfo;
import cn.soriys.assessment.dto.RiskInfo;
import cn.soriys.assessment.entity.AssessmentRecord;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * AssessmentRequest（嵌套） <-> AssessmentRecord（扁平 + String JSON）
 */
@Component
public class AssessmentConverter {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    // ============================================================
    // 1. Request -> Entity
    // ============================================================
    public AssessmentRecord from_convert(AssessmentRequest r) {
        if (r == null) return null;

        AssessmentRecord e = new AssessmentRecord();

        e.setId(r.getId());
        e.setNo(r.getNo());
        e.setAssessmentDate(r.getAssessmentDate());
        e.setReason(r.getReason());

        // ===== 从嵌套 basic 中拆出扁平字段 =====
        BasicInfo b = r.getBasic();
        if (b != null) {
            e.setElderlyName(b.getName());
            e.setGender(b.getGender());
            e.setBirthDate(b.getBirthDate());
            e.setHeight(b.getHeight());
            e.setWeight(b.getWeight());
            e.setEthnicity(b.getEthnicity());
            e.setReligion(b.getReligion());
            e.setIdNo(b.getIdNo());
            e.setEducation(b.getEducation());
            e.setLiving(toJson(b.getLiving()));         // List<String> -> JSON
            e.setMarriage(b.getMarriage());
        }

        // ===== 嵌套对象 / 集合 转 JSON 字符串 =====
        e.setRisks(toJson(r.getRisks()));              // RiskInfo -> JSON
        e.setProvider(toJson(r.getProvider()));        // ProviderInfo -> JSON
        e.setDisease(toJson(r.getDisease()));          // List<String> -> JSON
        e.setDiseaseOther(r.getDiseaseOther());
        e.setMedications(toJson(r.getMedications()));  // List<MedicationInfo> -> JSON
        e.setHealth(toJson(r.getHealth()));            // HealthInfo -> JSON
        e.setAnswers(toJson(r.getAnswers()));          // Map -> JSON

        e.setTotalScore(r.getTotalScore());
        e.setInitialLevel(r.getInitialLevel());
        e.setFinalLevel(r.getFinalLevel());

        return e;
    }

    // ============================================================
    // 2. Entity -> Request
    // ============================================================
    public AssessmentRequest to_convert(AssessmentRecord e) {
        if (e == null) return null;

        AssessmentRequest r = new AssessmentRequest();

        r.setId(e.getId());
        r.setNo(e.getNo());
        r.setAssessmentDate(e.getAssessmentDate());
        r.setReason(e.getReason());

        // ===== 扁平字段组装成 BasicInfo =====
        BasicInfo b = new BasicInfo();
        b.setName(e.getElderlyName());
        b.setGender(e.getGender());
        b.setBirthDate(e.getBirthDate());
        b.setHeight(e.getHeight());
        b.setWeight(e.getWeight());
        b.setEthnicity(e.getEthnicity());
        b.setReligion(e.getReligion());
        b.setIdNo(e.getIdNo());
        b.setEducation(e.getEducation());
        b.setLiving(fromJsonList(e.getLiving(), String.class));  // JSON -> List<String>
        b.setMarriage(e.getMarriage());
        r.setBasic(b);

        // ===== JSON 字符串 转回嵌套对象 / 集合 =====
        r.setRisks(fromJson(e.getRisks(), RiskInfo.class));
        r.setProvider(fromJson(e.getProvider(), ProviderInfo.class));
        r.setDisease(fromJsonList(e.getDisease(), String.class));
        r.setDiseaseOther(e.getDiseaseOther());
        r.setMedications(fromJsonList(e.getMedications(), MedicationInfo.class));
        r.setHealth(fromJson(e.getHealth(), HealthInfo.class));
        r.setAnswers(fromJsonMap(e.getAnswers()));

        r.setTotalScore(e.getTotalScore());
        r.setInitialLevel(e.getInitialLevel());
        r.setFinalLevel(e.getFinalLevel());

        return r;
    }

    // ============================================================
    // 3. JSON 工具方法
    // ============================================================

    /** 对象 -> JSON 字符串 */
    private String toJson(Object obj) {
        if (obj == null) return null;
        try {
            return MAPPER.writeValueAsString(obj);
        } catch (Exception ex) {
            throw new RuntimeException("JSON 序列化失败", ex);
        }
    }

    /** JSON 字符串 -> 对象 */
    private <T> T fromJson(String json, Class<T> clazz) {
        if (json == null || json.isBlank()) return null;
        try {
            return MAPPER.readValue(json, clazz);
        } catch (Exception ex) {
            throw new RuntimeException("JSON 反序列化失败", ex);
        }
    }

    /** JSON 字符串 -> List<T> */
    private <T> List<T> fromJsonList(String json, Class<T> clazz) {
        if (json == null || json.isBlank()) return null;
        try {
            return MAPPER.readValue(
                json,
                MAPPER.getTypeFactory().constructCollectionType(List.class, clazz)
            );
        } catch (Exception ex) {
            throw new RuntimeException("JSON 反序列化 List 失败", ex);
        }
    }

    /** JSON 字符串 -> Map<String, Object> */
    private Map<String, Object> fromJsonMap(String json) {
        if (json == null || json.isBlank()) return null;
        try {
            return MAPPER.readValue(json, new TypeReference<Map<String, Object>>() {});
        } catch (Exception ex) {
            throw new RuntimeException("JSON 反序列化 Map 失败", ex);
        }
    }

    // ============================================================
    // 4. 附带审计字段的版本
    // ============================================================

    /** 新建：填充创建人/时间 */
    public AssessmentRecord from_convert(AssessmentRequest r, Long userId) {
        AssessmentRecord e = from_convert(r);
        if (e == null) return null;
        e.setCreatedBy(userId);
        e.setCreatedAt(java.time.LocalDateTime.now());
        e.setUpdatedAt(java.time.LocalDateTime.now());
        return e;
    }

    /** 更新：保留 id 和审计字段，仅覆盖业务字段 */
    public AssessmentRecord from_convert(AssessmentRequest r, AssessmentRecord existing) {
        if (r == null || existing == null) return existing;
        AssessmentRecord fresh = from_convert(r);
        fresh.setId(existing.getId());
        fresh.setCreatedBy(existing.getCreatedBy());
        fresh.setCreatedAt(existing.getCreatedAt());
        fresh.setUpdatedAt(java.time.LocalDateTime.now());
        return fresh;
    }
}