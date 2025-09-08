package com.xuecheng.content.model.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author jiaohan
 * @description 课程查询参数
 * 2025-08-06 15:02
 */
@Data
public class QueryCourseParamsDto {
    //审核状态
    @ApiModelProperty("审核状态")
    private String auditStatus;
    //课程名称
    @ApiModelProperty("课程名称")
    private String courseName;
    //发布状态
    @ApiModelProperty("发布状态")
    private String publishStatus;
}
