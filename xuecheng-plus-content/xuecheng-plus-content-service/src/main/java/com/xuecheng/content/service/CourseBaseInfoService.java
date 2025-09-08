package com.xuecheng.content.service;

import com.xuecheng.base.model.PageParams;
import com.xuecheng.base.model.PageResult;
import com.xuecheng.content.model.dto.AddCourseDto;
import com.xuecheng.content.model.dto.CourseBaseInfoDto;
import com.xuecheng.content.model.dto.EditCourseDto;
import com.xuecheng.content.model.dto.QueryCourseParamsDto;
import com.xuecheng.content.model.po.CourseBase;

/**
 * @author jiaohan
 * @description des
 * 2025-08-08
 */
public interface CourseBaseInfoService {
    /**
     * 课程查询接口
     * @param pageParams 分页参数
     * @param queryCourseParamsDto 查询条件
     * @return 分页结果
     */
    PageResult<CourseBase> queryCourseBaseList(PageParams pageParams, QueryCourseParamsDto queryCourseParamsDto);

    /**
     *  添加课程基本信息
     * @param companyId 教学机构id
     * @param addCourseDto 课程基本信息
     * @return CourseBaseInfoDto
     */
    CourseBaseInfoDto createCourseBase(Long companyId, AddCourseDto addCourseDto);

    /**
     * 根据课程ID获取课程基本信息
     * @param courseId 课程ID
     * @return CourseBaseInfoDto 课程基本信息数据传输对象
     */
    CourseBaseInfoDto getCourseBaseById(Long courseId);

    /**
     * 修改课程基本信息
     * @param companyId 公司ID
     * @param editCourseDto 课程编辑信息数据传输对象
     * @return CourseBaseInfoDto 修改后的课程基本信息数据传输对象
     */
    CourseBaseInfoDto modifyCourseBase(Long companyId, EditCourseDto editCourseDto);

    /**
     * 删除课程基本信息
     * @param companyId 公司ID
     * @param courseId 课程ID
     */
    void deleteCourseBase(Long companyId, Long courseId);

}
