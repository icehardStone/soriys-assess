package cn.soriys.assessment.dto;

import lombok.Data;

@Data
public class MedicationInfo {
    private String name;
    private String method;
    private String dose;
    private String frequency;
}