package com.xuecheng.content.api;


import com.xuecheng.content.model.po.CourseTeacher;
import com.xuecheng.content.service.CourseTeacherService;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * *作者：jiaohan
 * *日期：2025/8/25 20:54
 * *文件描述：
 */
@RestController
@RequiredArgsConstructor
public class CourseTeacherController {

    private final CourseTeacherService courseTeacherService;

    @ApiOperation("查询指定课程id教师信息")
    @GetMapping("/courseTeacher/list/{id}")
    public List<CourseTeacher> getCourseTeacherList(@PathVariable("id") Long courseId) {
        return courseTeacherService.getCourseTeacherListById(courseId);
    }

    @ApiOperation("添加或修改教师")
    @PostMapping("/courseTeacher")
    public CourseTeacher addOrUpdateCourseTeacher(@RequestBody CourseTeacher courseTeacher) {
        Long companyId = 1232141425L;
        return courseTeacherService.addOrUpdateCourseTeacher(companyId, courseTeacher);
    }

    @ApiOperation("删除教师")
    @DeleteMapping("/courseTeacher/course/{courseId}/{teacherId}")
    public void deleteCourseTeacher(@PathVariable("courseId") Long courseId, @PathVariable("teacherId") Long teacherId) {
        Long companyId = 1232141425L;
        courseTeacherService.deleteCourseTeacher(companyId, courseId, teacherId);
    }
}
