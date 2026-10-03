package cn.soriys.assessment.dto;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class InviteRequest {

    @NotBlank
    private String username;

    @NotBlank
    private String password;

    private String realName;

    private String phone;

    private String email;
}
