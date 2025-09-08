package com.xuecheng.content.model.dto;

import com.xuecheng.content.model.po.CourseCategory;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author jiaohan
 * @description 课程分类树形节点dto
 * 2025-08-11
 */
@Data
public class CourseCategoryTreeDto extends CourseCategory implements Serializable {
    private List<CourseCategoryTreeDto> childrenTreeNodes;
}
