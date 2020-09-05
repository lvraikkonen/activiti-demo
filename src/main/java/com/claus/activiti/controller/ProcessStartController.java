package com.claus.activiti.controller;

import com.claus.activiti.model.Holiday;
import com.claus.activiti.utils.RestMessage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.activiti.api.process.model.ProcessInstance;
import org.activiti.api.process.model.builders.ProcessPayloadBuilder;
import org.activiti.api.process.runtime.ProcessRuntime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * @author xugj<br>
 * @version 1.0<br>
 * @createDate 2019/05/31 10:58 <br>
 * @Description <p> 启动流程实例 </p>
 */
@RestController
@Api(tags = "启动流程实例")
public class ProcessStartController {
    private Logger logger = LoggerFactory.getLogger(ProcessStartController.class);

    private final ProcessRuntime processRuntime;

    public ProcessStartController(ProcessRuntime processRuntime) {
        this.processRuntime = processRuntime;
    }

    @GetMapping("/start-process/{processDefinitionKey}")
    @ApiOperation(value = "启动流程实例", notes = "启动流程实例_带参数")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "processDefinitionKey", value = "定义流程KEY", dataType = "String", paramType = "path", example = "SampleProcess"),
            @ApiImplicitParam(name = "param_map", value = "参数", dataType = "String", paramType = "path", example = "map")
    })
    public RestMessage startProcessWithParams(
            @PathVariable("processDefinitionKey") String processDefinitionKey) {

        RestMessage restMessage = new RestMessage();
        ProcessInstance processInstance = null;
        // 动态设置assignee的人
        Map<String, Object> map = new HashMap<>();
        map.put("employee", "zhangsan");
        map.put("deptManager", "lisi");
        map.put("boss", "wangwu");
        map.put("hr", "renliu");
        Holiday holiday = new Holiday();
        holiday.setHolidayLength((double) 5F);
        // 将请假对象(包含请假天数)传入
        map.put("holiday", holiday);
        try {
            processInstance = processRuntime.start(ProcessPayloadBuilder
                    .start()
                    .withProcessDefinitionKey(processDefinitionKey)
                    .withVariables(map)
                    .build());
        } catch (Exception e) {
            e.printStackTrace();
            restMessage = RestMessage.fail("启动失败", e.getMessage());
        }

        if (processInstance != null) {
            restMessage = RestMessage.success("启动成功", processInstance);
        }

        return restMessage;
    }

}
