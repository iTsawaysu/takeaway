package com.sun.takeaway.model.vo;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author sun
 */
@Data
public class LoginEmployeeVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    // 用户名（Unique）
    private String username;

    // 姓名
    private String name;

    // 手机号
    private String phone;

    // 性别
    private String sex;

    // 身份证号
    private String idNumber;

    // 状态 （0 禁用，1 正常）
    private Integer status;

        /**
     * 创建时间
     */
    private LocalDateTime createTime;

        /**
     * 更新时间
     */
    private LocalDateTime updateTime;

        /**
     * 创建人
     */
    @TableField(fill = FieldFill.INSERT)
    private Long createUser;

        /**
     * 修改人
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Long updateUser;
}
