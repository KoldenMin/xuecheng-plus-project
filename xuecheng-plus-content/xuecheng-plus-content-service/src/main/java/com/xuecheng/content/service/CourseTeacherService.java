package com.xuecheng.content.service;

import com.xuecheng.content.model.po.CourseTeacher;

import java.util.List;

public interface CourseTeacherService {
    /**
     * 根据课程ID获取课程教师列表
     * @param courseId 课程ID
     * @return 课程教师列表
     */
    List<CourseTeacher> getCourseTeacherListById(Long courseId);

    /**
     * 添加或更新课程教师信息
     * @param companyId 公司ID
     * @param courseTeacher 课程教师对象
     * @return 添加或更新后的课程教师信息
     */
    CourseTeacher addOrUpdateCourseTeacher(Long companyId, CourseTeacher courseTeacher);

    /**
     * 删除课程教师关联关系
     * @param companyId 公司ID
     * @param courseId 课程ID
     * @param teacherId 教师ID
     */
    void deleteCourseTeacher(Long companyId, Long courseId, Long teacherId);

}
