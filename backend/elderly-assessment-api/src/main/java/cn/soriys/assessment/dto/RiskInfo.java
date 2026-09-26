package cn.soriys.assessment.dto;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class RiskInfo {
    private Integer falls;
    private Integer wandering;
    private Integer choking;

    @JsonProperty("suicide/self-harm")
    private Integer suicideSelfHarm;

    private Integer other;
}