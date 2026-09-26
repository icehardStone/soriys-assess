package cn.soriys.assessment.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("assessment_record")
public class AssessmentRecord {
    @TableId
    private Long id;
    private String no;
    private LocalDate assessmentDate;
    private String reason;
    private String elderlyName;
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
    private Long createdBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
