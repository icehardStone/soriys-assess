package cn.soriys.assessment.dto;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
@Data
public class ProfileRequest {

    private Long id;
    private String photo;
    
    private String realName;
    private String phone;

    @NotBlank
    private String email;
}
