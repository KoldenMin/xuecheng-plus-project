package com.xuecheng.content.model.dto;

import com.xuecheng.content.model.po.Teachplan;
import com.xuecheng.content.model.po.TeachplanMedia;
import lombok.Data;

import java.util.List;

/**
 * @author jiaohan
 * @description des
 * 2025-08-12
 */
@Data
public class TeachPlanDto extends Teachplan {
    // 子节点
    List<TeachPlanDto> teachPlanTreeNodes;
    // 关联的媒资信息
    TeachplanMedia teachplanMedia;
}
