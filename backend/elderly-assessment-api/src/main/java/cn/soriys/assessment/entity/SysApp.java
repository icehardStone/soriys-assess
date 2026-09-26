
package cn.soriys.assessment.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("sys_app")
public class SysApp {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String appName;

    private String appKey;

    private String appSecret;

    private Boolean enabled;

    private String description;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}