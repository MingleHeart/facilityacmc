package org.facmc.common.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class SysLog implements Serializable {

    private Integer id;
    private String username;
    private String operation;
    private String time;
    private String method;
    private String params;
    private String ip;
    //   @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private String createTime;
    private String result;
}
