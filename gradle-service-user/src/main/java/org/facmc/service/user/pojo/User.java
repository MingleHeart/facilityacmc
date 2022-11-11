package org.facmc.service.user.pojo;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

@Data
@Setter
@Getter
@ToString
@TableName("fac_user")
public class User {
    @TableId(value = "id")
    //主键
    private long id;
    //账户名称
    private String userName;
    //行创建时间
    private Date createTime;
    //行更新时间
    private Date updateTime;
    //用户姓名
    private String rName;
    //用户公司
    private String userCom;
    //是否启用,只对普通用户生效
    private int isEnable;
    //MD5加盐
    private String encryption;
    //权限
    private int userPermission;
    //从属id
    private long belongId;
}
