package com.xuecheng.base.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author jiaohan
 * @describe 分页查询通用参数
 * 2025-08-06 14:54
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PageParams {

    //当前页码
    @ApiModelProperty("当前页码")
    private Long pageNo;

    //每页记录数默认值
    @ApiModelProperty("每页记录数默认值")
    private Long pageSize;

}
