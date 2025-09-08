package com.xuecheng.content.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xuecheng.base.exception.XueChengPlusException;
import com.xuecheng.content.mapper.CourseBaseMapper;
import com.xuecheng.content.mapper.CourseTeacherMapper;
import com.xuecheng.content.model.po.CourseBase;
import com.xuecheng.content.model.po.CourseTeacher;
import com.xuecheng.content.service.CourseTeacherService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * *作者：jiaohan
 * *日期：2025/8/25 20:51
 * *文件描述：
 */
@Service
@RequiredArgsConstructor
public class CourseTeacherServiceImpl implements CourseTeacherService {

    private final CourseTeacherMapper courseTeacherMapper;
    private final CourseBaseMapper courseBaseMapper;

    @Override
    public List<CourseTeacher> getCourseTeacherListById(Long courseId) {
        LambdaQueryWrapper<CourseTeacher> queryWrapper = new LambdaQueryWrapper<CourseTeacher>()
                .eq(CourseTeacher::getCourseId, courseId);
        return courseTeacherMapper.selectList(queryWrapper);
    }

    @Override
    @Transactional
    public CourseTeacher addOrUpdateCourseTeacher(Long companyId, CourseTeacher courseTeacher) {
        handleAuth(companyId, courseTeacher.getCourseId());

        Long id = courseTeacher.getId();
        CourseTeacher tempCourseTeacher = new CourseTeacher();
        if (id == null) {
            // 添加
            BeanUtils.copyProperties(courseTeacher, tempCourseTeacher);
            tempCourseTeacher.setCreateDate(LocalDateTime.now());
            courseTeacherMapper.insert(tempCourseTeacher);
            return tempCourseTeacher;
        } else {
            // 修改
            CourseTeacher dbCourseTeacher = courseTeacherMapper.selectById(id);
            BeanUtils.copyProperties(courseTeacher, dbCourseTeacher, "id");
            courseTeacherMapper.updateById(dbCourseTeacher);
            return dbCourseTeacher;
        }
    }

    @Override
    @Transactional
    public void deleteCourseTeacher(Long companyId, Long courseId, Long teacherId) {
        handleAuth(companyId, courseId);
        courseTeacherMapper.delete(new LambdaQueryWrapper<CourseTeacher>()
                .eq(CourseTeacher::getCourseId, courseId)
                .eq(CourseTeacher::getId, teacherId));
    }

    private void handleAuth(Long companyId, Long courseId) {
        CourseBase courseBase = courseBaseMapper.selectById(courseId);
        if (courseBase == null) {
            XueChengPlusException.cast("课程不存在");
        }
        if (!courseBase.getCompanyId().equals(companyId)) {
            XueChengPlusException.cast("不能删除其他机构的课程");
        }
    }
}
