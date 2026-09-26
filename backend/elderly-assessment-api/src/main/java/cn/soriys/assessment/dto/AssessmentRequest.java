package cn.soriys.assessment.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class AssessmentRequest {
    @NotBlank private String no;
    private LocalDate assessmentDate;
    private String reason;
    @NotBlank private String elderlyName;
    private String gender;
    private LocalDate birthDate;
    private BigDecimal height;
    private BigDecimal weight;
    private String ethnicity;
    private String religion;
    private String idNo;
    private String education;
    private String living;
    private String marriage;
    private String risks;
    private String provider;
    private String disease;
    private String diseaseOther;
    private String medications;
    private String health;
    private String answers;
    private BigDecimal totalScore;
    private Integer initialLevel;
    private Integer finalLevel;
}
