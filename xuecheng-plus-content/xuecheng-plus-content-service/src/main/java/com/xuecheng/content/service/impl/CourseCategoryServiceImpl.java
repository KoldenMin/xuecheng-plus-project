package com.xuecheng.content.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xuecheng.content.mapper.CourseCategoryMapper;
import com.xuecheng.content.model.dto.CourseCategoryTreeDto;
import com.xuecheng.content.model.po.CourseCategory;
import com.xuecheng.content.service.CourseCategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author jiaohan
 * @description des
 * 2025-08-11
 */
@Service
@RequiredArgsConstructor
public class CourseCategoryServiceImpl extends ServiceImpl<CourseCategoryMapper, CourseCategory> implements CourseCategoryService {


    private final CourseCategoryMapper courseCategoryMapper;

    @Override
    public List<CourseCategoryTreeDto> queryTreeNodes(String id) {
        List<CourseCategoryTreeDto> categoryDtos = courseCategoryMapper.selectTreeNodes(id);

        Map<String, CourseCategoryTreeDto> map = categoryDtos.stream()
                // 排除根节点
                .filter(item -> !id.equals(item.getId()))
                .collect(Collectors.toMap(CourseCategory::getId, value -> value,
                        (key1, key2) -> key2));

        List<CourseCategoryTreeDto> result = new ArrayList<>();
        // 依次遍历每个元素，排除根节点
        categoryDtos.stream()
                .filter(item -> !id.equals(item.getId()))
                .forEach(item -> {
                    if (item.getParentid().equals(id)) {
                        result.add(item);
                    }
                    // 找到当前节点父节点
                    CourseCategoryTreeDto parent = map.get(item.getParentid());
                    if (parent != null) {
                        if (parent.getChildrenTreeNodes() == null) {
                            parent.setChildrenTreeNodes(new ArrayList<>());
                        }
                        // 往ChildrenTreeNodes中添加节点
                        parent.getChildrenTreeNodes().add(item);
                    }
                });

        return result;
    }
}
