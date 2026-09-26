package cn.soriys.assessment.dto;

import lombok.Data;

// HealthInfo.java
@Data
public class HealthInfo {
    private String pressure;
    private String joint;
    private String pain;
    private String malnutrition;
    private String airway;
    private String coma;
}