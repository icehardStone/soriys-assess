package cn.soriys.assessment.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;



@Data
public class AssessmentRequest {
    private String no;
    private LocalDate assessmentDate;
    private String reason;
    private BasicInfo basic;
    private RiskInfo risks;
    private ProviderInfo provider;
    private List<String> disease;
    private String diseaseOther;
    private List<MedicationInfo> medications;
    private HealthInfo health;
    private Map<String, Object> answers;
    private BigDecimal totalScore;
    private Integer initialLevel;
    private Integer finalLevel;
}