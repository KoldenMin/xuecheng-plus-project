package com.xuecheng.content.service;

import com.xuecheng.content.model.dto.SaveTeachplanDto;
import com.xuecheng.content.model.dto.TeachPlanDto;

import java.util.List;

/**
 * @author jiaohan
 * @description des
 * 2025-08-12
 */
public interface TeachPlanService {
    /**
     *  查询课程计划树形结构
     * @param courseId 课程id
     * @return 树形结构
     */
    List<TeachPlanDto> findTeachplanTree(Long courseId);

    void saveTeachplan(SaveTeachplanDto saveTeachplanDto);

    void deleteTeachplan(Long teachplanId);

    void moveUpOrDown(String moveType, Long teachplanId);
}
