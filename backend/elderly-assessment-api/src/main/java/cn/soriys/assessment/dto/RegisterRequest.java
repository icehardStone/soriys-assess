package cn.soriys.assessment.dto;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
@Data
public class RegisterRequest {

    private String photo;
    @NotBlank private String username;
    @NotBlank private String password;
    private String realName;
    private String phone;
    @NotBlank
    private String appName;

    @NotBlank
    private String email;
}
