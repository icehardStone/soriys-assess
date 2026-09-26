package cn.soriys.assessment.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;


@Data
public class BasicInfo {
    private String name;
    private String gender;
    private LocalDate birthDate;
    private BigDecimal height;
    private BigDecimal weight;
    private String ethnicity;
    private String religion;
    private String idNo;
    private String education;
    private List<String> living;
    private String marriage;
}