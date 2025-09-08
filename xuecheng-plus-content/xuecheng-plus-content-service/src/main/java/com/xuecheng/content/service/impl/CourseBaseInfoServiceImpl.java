package com.xuecheng.content.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xuecheng.base.exception.XueChengPlusException;
import com.xuecheng.base.model.PageParams;
import com.xuecheng.base.model.PageResult;
import com.xuecheng.content.mapper.*;
import com.xuecheng.content.model.dto.AddCourseDto;
import com.xuecheng.content.model.dto.CourseBaseInfoDto;
import com.xuecheng.content.model.dto.EditCourseDto;
import com.xuecheng.content.model.dto.QueryCourseParamsDto;
import com.xuecheng.content.model.po.*;
import com.xuecheng.content.service.CourseBaseInfoService;
import com.xuecheng.content.service.CourseCategoryService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author jiaohan
 * @description des
 * 2025-08-08
 */
@Service
@RequiredArgsConstructor
public class CourseBaseInfoServiceImpl implements CourseBaseInfoService {

    private final CourseBaseMapper courseBaseMapper;
    private final CourseMarketMapper courseMarketMapper;
    private final CourseCategoryMapper courseCategoryMapper;
    private final CourseCategoryService categoryService;
    private final TeachplanMapper teachplanMapper;
    private final CourseTeacherMapper courseTeacherMapper;
    private final TeachplanMediaMapper teachplanMediaMapper;

    @Override
    public PageResult<CourseBase> queryCourseBaseList(PageParams pageParams, QueryCourseParamsDto queryCourseParamsDto) {
        LambdaQueryWrapper<CourseBase> queryWrapper = new LambdaQueryWrapper<>();

        queryWrapper.like(StringUtils.isNotEmpty(queryCourseParamsDto.getCourseName()), CourseBase::getName, queryCourseParamsDto.getCourseName());
        queryWrapper.eq(StringUtils.isNotEmpty(queryCourseParamsDto.getAuditStatus()), CourseBase::getAuditStatus, queryCourseParamsDto.getAuditStatus());
        queryWrapper.eq(StringUtils.isNotEmpty(queryCourseParamsDto.getPublishStatus()), CourseBase::getStatus, queryCourseParamsDto.getPublishStatus());

        Page<CourseBase> page = new Page<>(pageParams.getPageNo(), pageParams.getPageSize());

        Page<CourseBase> pageResult = courseBaseMapper.selectPage(page, queryWrapper);
        List<CourseBase> records = pageResult.getRecords();
        long total = pageResult.getTotal();

        return new PageResult<>(records, total, pageParams.getPageNo(), pageParams.getPageSize());

    }

    @Override
    @Transactional
    public CourseBaseInfoDto createCourseBase(Long companyId, AddCourseDto dto) {
        // 新增对象
        CourseBase courseBaseNew = new CourseBase();
        BeanUtils.copyProperties(dto, courseBaseNew);
        //设置审核状态
        courseBaseNew.setAuditStatus("202002");
        //设置发布状态
        courseBaseNew.setStatus("203001");
        courseBaseNew.setCompanyId(companyId);
        courseBaseNew.setCreateDate(LocalDateTime.now());

        int count = courseBaseMapper.insert(courseBaseNew);
        if (count <= 0) {
            throw new RuntimeException("新增课程基本信息失败");
        }
        // 向课程营销表保存课程营销信息
        CourseMarket courseMarketNew = new CourseMarket();
        Long courseBaseId = courseBaseNew.getId();
        BeanUtils.copyProperties(dto, courseMarketNew);
        courseMarketNew.setId(courseBaseId);
        int i = saveCourseMarket(courseMarketNew);
        if (i <= 0) {
            throw new RuntimeException("保存课程营销信息失败");
        }
        // 查询课程基本信息及营销信息并返回
        return this.getCourseBaseById(courseBaseId);
    }

    /**
     * 根据id查询课程信息，包括基本信息和营销信息
     *
     * @param courseId 课程基础信息id
     * @return CourseBaseInfoDto
     */
    @Override
    public CourseBaseInfoDto getCourseBaseById(Long courseId) {
        CourseBase courseBase = courseBaseMapper.selectById(courseId);
        if (courseBase == null) {
            return null;
        }
        CourseMarket courseMarket = courseMarketMapper.selectById(courseId);
        CourseBaseInfoDto courseBaseInfoDto = new CourseBaseInfoDto();
        BeanUtils.copyProperties(courseBase, courseBaseInfoDto);
        if (courseMarket != null) {
            BeanUtils.copyProperties(courseMarket, courseBaseInfoDto);
        }

        // 查询分类名称
        String st = courseBase.getSt(); // 小分类
        String mt = courseBase.getMt();  // 大分类
        Map<String, String> categoryMap = categoryService.lambdaQuery().in(CourseCategory::getId, st, mt).list().stream().collect(Collectors.toMap(CourseCategory::getId, CourseCategory::getName));
        courseBaseInfoDto.setStName(categoryMap.get(st));
        courseBaseInfoDto.setMtName(categoryMap.get(mt));

        return courseBaseInfoDto;
    }

    @Override
    @Transactional
    public CourseBaseInfoDto modifyCourseBase(Long companyId, EditCourseDto editCourseDto) {
        // 查课程id
        Long courseId = editCourseDto.getId();
        CourseBase courseBase = courseBaseMapper.selectById(courseId);
        if (courseBase == null) {
            XueChengPlusException.cast("课程不存在");
        }
        // 校验本机构只能改本机构课程
        if (!courseBase.getCompanyId().equals(companyId)) {
            XueChengPlusException.cast("不能修改其他机构的课程");
        }
        BeanUtils.copyProperties(editCourseDto, courseBase);
        courseBase.setChangeDate(LocalDateTime.now());
        int i = courseBaseMapper.updateById(courseBase);
        // 更新营销信息
        CourseMarket courseMarket = new CourseMarket();
        BeanUtils.copyProperties(editCourseDto, courseMarket);
        this.saveCourseMarket(courseMarket);
        // 查询课程信息
        return this.getCourseBaseById(courseId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteCourseBase(Long companyId, Long courseId) {
        /*
          课程的审核状态为未提交时方可删除。
          删除课程需要删除课程相关的基本信息、营销信息、课程计划、课程教师信息。
         */
        CourseBase courseBase = courseBaseMapper.selectById(courseId);
        if (!courseBase.getCompanyId().equals(companyId)) {
            XueChengPlusException.cast("不能删除其他机构的课程");
        }
        if (StringUtils.equals(courseBase.getStatus(), "202002")) {
            XueChengPlusException.cast("课程审核状态为未提交时方可删除");
        }
        // 删除基本信息
        courseBaseMapper.deleteById(courseId);
        // 删除营销信息
        courseMarketMapper.deleteById(courseId);
        // 删除课程计划
        teachplanMapper.delete(new LambdaQueryWrapper<Teachplan>()
                .eq(Teachplan::getCourseId, courseId));
        // 删除对应的媒体信息
        teachplanMediaMapper.delete(new LambdaQueryWrapper<TeachplanMedia>()
                .eq(TeachplanMedia::getCourseId, courseId));
        // 删除课程教师信息
        courseTeacherMapper.delete(new LambdaQueryWrapper<CourseTeacher>()
                .eq(CourseTeacher::getCourseId, courseId));
    }

    /**
     * 保存课程营销信息
     *
     * @param courseMarketNew 课程营销实体类
     * @return 插入数据条数
     */
    private int saveCourseMarket(CourseMarket courseMarketNew) {
        // 收费规则
        String charge = courseMarketNew.getCharge();
        if (StringUtils.isBlank(charge)) {
            throw new RuntimeException("收费规则没有选择");
        }
        //收费
        if (charge.equals("201001")) {
            if (courseMarketNew.getPrice() == null || courseMarketNew.getPrice() <= 0) {
                throw new XueChengPlusException("课程为收费价格不能为空且必须大于0");
            }
        }
        // 根据id从课程营销表查询，没记录新增，有记录更新
        CourseMarket selectById = courseMarketMapper.selectById(courseMarketNew.getId());
        if (selectById == null) {
            // 新增
            return courseMarketMapper.insert(courseMarketNew);
        } else {
            // 更新
            BeanUtils.copyProperties(courseMarketNew, selectById);
            selectById.setId(courseMarketNew.getId());
            return courseMarketMapper.updateById(selectById);
        }

    }
}
