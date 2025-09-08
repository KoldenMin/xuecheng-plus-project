package com.xuecheng.content.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xuecheng.base.exception.XueChengPlusException;
import com.xuecheng.content.mapper.CourseBaseMapper;
import com.xuecheng.content.mapper.TeachplanMapper;
import com.xuecheng.content.mapper.TeachplanMediaMapper;
import com.xuecheng.content.model.dto.SaveTeachplanDto;
import com.xuecheng.content.model.dto.TeachPlanDto;
import com.xuecheng.content.model.po.CourseBase;
import com.xuecheng.content.model.po.Teachplan;
import com.xuecheng.content.model.po.TeachplanMedia;
import com.xuecheng.content.service.TeachPlanService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * @author jiaohan
 * @description des
 * 2025-08-12
 */
@Service
@RequiredArgsConstructor
public class TeachPlanServiceImpl implements TeachPlanService {

    private final TeachplanMapper teachplanMapper;
    private final CourseBaseMapper courseBaseMapper;
    private final TeachplanMediaMapper teachplanMediaMapper;

    @Override
    public List<TeachPlanDto> findTeachplanTree(Long courseId) {
        return teachplanMapper.selectTreeNodes(courseId);
    }

    @Transactional
    @Override
    public void saveTeachplan(SaveTeachplanDto dto) {
        Long id = dto.getId();
        if (id == null) {
            // 新增
            // 取出同父级节点数量
            int count = getTeachplanCount(dto.getCourseId(), dto.getParentid());

            // 拿到目前最大的orderby
            int maxOderBy = getMaxOderBy(dto.getCourseId(), dto.getParentid());


            Teachplan teachplan = new Teachplan();
            BeanUtils.copyProperties(dto, teachplan);
            // 设置排序字段
            teachplan.setOrderby(maxOderBy + 1);
            teachplan.setCreateDate(LocalDateTime.now());
            teachplanMapper.insert(teachplan);
        } else {
            // 修改
            Teachplan teachplan = teachplanMapper.selectById(id);
            BeanUtils.copyProperties(dto, teachplan);
            teachplan.setChangeDate(LocalDateTime.now());
            teachplanMapper.updateById(teachplan);
        }
    }

    @Override
    @Transactional
    public void deleteTeachplan(Long teachplanId) {
        Teachplan teachplan = teachplanMapper.selectById(teachplanId);
        Long courseId = teachplan.getCourseId();
        String auditStatus = Optional.ofNullable(courseBaseMapper.selectById(courseId)).map(CourseBase::getAuditStatus).orElse(null);
        if (!StringUtils.equals(auditStatus, "202002")) {
            // 不是未提交状态
            XueChengPlusException.cast("课程已经提交，无法删除");
        }
        if (teachplan.getGrade() == 1) {
            // 一级章节，需要保证没有二级章节才能删
            LambdaQueryWrapper<Teachplan> queryWrapper = new LambdaQueryWrapper<Teachplan>().eq(Teachplan::getParentid, teachplanId);

            Integer count = teachplanMapper.selectCount(queryWrapper);
            if (count > 0) {
                XueChengPlusException.cast("课程计划信息还有子级信息，无法操作", "120409");
            }
            teachplanMapper.deleteById(teachplanId);
        } else {
            // 二级章节，需要同时删除关联信息
            LambdaQueryWrapper<TeachplanMedia> queryWrapper = new LambdaQueryWrapper<TeachplanMedia>().eq(TeachplanMedia::getTeachplanId, teachplanId);
            Integer count = teachplanMediaMapper.selectCount(queryWrapper);
            if (count > 0) {
                // 删除对应的media信息
                teachplanMediaMapper.delete(queryWrapper);
            }
            teachplanMapper.deleteById(teachplanId);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void moveUpOrDown(String moveType, Long teachplanId) {
        if (StringUtils.equals(moveType, "moveup")) {
            moveItem(teachplanId, true);
        } else if (StringUtils.equals(moveType, "movedown")) {
            moveItem(teachplanId, false);
        }
    }

    private void moveItem(Long teachplanId, boolean moveUp) {
        Teachplan current = teachplanMapper.selectById(teachplanId);
        Integer currentOrderby = current.getOrderby();
        Long courseId = current.getCourseId();
        Long parentid = current.getParentid();
        Teachplan exchangeTarget = findExchangeTarget(parentid, currentOrderby, courseId, moveUp);
        if (ObjectUtils.isEmpty(exchangeTarget)) {
            // 已经在边界了，无法移动
            return;
        }
        // 交换orderby
        exchangeOderby(current, exchangeTarget);
    }

    /**
     * 查找要交换orderby的目标项
     *
     * @param parentid 父级id
     * @param orderby  当前的oderby值
     * @param moveUp   是否是上移
     * @return Teachplan
     */
    private Teachplan findExchangeTarget(Long parentid, Integer orderby, Long courseId, boolean moveUp) {
        LambdaQueryWrapper<Teachplan> queryWrapper = new LambdaQueryWrapper<Teachplan>()
                .eq(Teachplan::getParentid, parentid)
                .eq(Teachplan::getCourseId, courseId)
                .eq(Teachplan::getStatus, 1);
        if (moveUp) {
            queryWrapper.lt(Teachplan::getOrderby, orderby)
                    .orderByDesc(Teachplan::getOrderby);
        } else {
            queryWrapper.gt(Teachplan::getOrderby, orderby)
                    .orderByAsc(Teachplan::getOrderby);
        }
        queryWrapper.last("limit 1");

        return teachplanMapper.selectOne(queryWrapper);
    }

    /**
     * 交换两个Teachplan的orderby值
     *
     * @param item1 -
     * @param item2 -
     */
    private void exchangeOderby(Teachplan item1, Teachplan item2) {
        Integer item1Orderby = item1.getOrderby();
        Integer item2Orderby = item2.getOrderby();
        item1.setOrderby(item2Orderby);
        item2.setOrderby(item1Orderby);
        teachplanMapper.updateById(item1);
        teachplanMapper.updateById(item2);
    }

    /**
     * 获取最新的排序号
     *
     * @param courseId 课程id
     * @param parentid 父课程id
     * @return 排序号
     */
    private int getTeachplanCount(Long courseId, Long parentid) {
        LambdaQueryWrapper<Teachplan> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Teachplan::getCourseId, courseId)
                .eq(Teachplan::getParentid, parentid);

        return teachplanMapper.selectCount(wrapper);
    }

    /**
     * 获取orderby的最大值
     *
     * @param courseId 课程id
     * @param parentid 父课程id
     * @return orderby的最大值
     */
    private int getMaxOderBy(Long courseId, Long parentid) {
        LambdaQueryWrapper<Teachplan> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Teachplan::getCourseId, courseId).eq(Teachplan::getParentid, parentid).orderByDesc(Teachplan::getOrderby).last("limit 1");

        Teachplan teachplan = teachplanMapper.selectOne(wrapper);
        if (teachplan == null) {
            return 0;
        } else {
            return teachplan.getOrderby();
        }
    }
}
