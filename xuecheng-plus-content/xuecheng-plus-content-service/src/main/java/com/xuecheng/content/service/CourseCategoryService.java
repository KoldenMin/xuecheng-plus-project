package com.xuecheng.content.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xuecheng.content.model.dto.CourseCategoryTreeDto;
import com.xuecheng.content.model.po.CourseCategory;

import java.util.List;

/**
 * @author jiaohan
 * @description des
 * 2025-08-11
 */
public interface CourseCategoryService extends IService<CourseCategory> {
    /**
     * 课程分类树形结构查询
     *
     * @param id
     * @return
     */
    List<CourseCategoryTreeDto> queryTreeNodes(String id);
}
