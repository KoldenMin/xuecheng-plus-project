package com.xuecheng.content.api;

import com.xuecheng.content.model.dto.SaveTeachplanDto;
import com.xuecheng.content.model.dto.TeachPlanDto;
import com.xuecheng.content.service.TeachPlanService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author jiaohan
 * @description 课程计划编辑接口
 * 2025-08-12
 */
@Api(value = "课程计划编辑接口", tags = "课程计划编辑接口")
@RestController
@RequiredArgsConstructor
public class TeachPlanController {

    private final TeachPlanService teachPlanService;

    @ApiOperation("查询课程计划树形结构")
    @ApiImplicitParam(value = "courseId", name = "课程Id", required = true, dataType = "Long", paramType = "path")
    @GetMapping("/teachplan/{courseId}/tree-nodes")
    public List<TeachPlanDto> getTreeNodes(@PathVariable Long courseId) {
        return teachPlanService.findTeachplanTree(courseId);
    }

    @ApiOperation("创建、修改课程计划")
    @PostMapping("/teachplan")
    public void saveTeachplan(@RequestBody SaveTeachplanDto saveTeachplanDto) {
        teachPlanService.saveTeachplan(saveTeachplanDto);
    }

    @ApiOperation("删除课程计划")
    @DeleteMapping("/teachplan/{id}")
    public void deleteTeachplan(@PathVariable("id") Long teachplanId) {
        teachPlanService.deleteTeachplan(teachplanId);
    }

    @ApiOperation("向上向下移动")
    @PostMapping("teachplan/{moveType}/{id}")
    public void moveUpOrDown(@PathVariable String moveType, @PathVariable("id") Long teachplanId) {
        teachPlanService.moveUpOrDown(moveType, teachplanId);
    }
}
