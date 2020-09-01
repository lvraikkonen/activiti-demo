package com.claus.activiti.controller;

import com.claus.activiti.utils.RestMessage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.activiti.api.process.model.ProcessInstance;
import org.activiti.api.process.model.ProcessInstanceMeta;
import org.activiti.api.process.runtime.ProcessRuntime;
import org.activiti.api.runtime.shared.query.Pageable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author xugj<br>
 * @version 1.0<br>
 * @createDate 2019/05/31 11:14 <br>
 * @Description <p> 列出流程实例 </p>
 */

@RestController
@Api(tags = "列出流程实例")
public class ProcessInstanceController {
    private Logger logger = LoggerFactory.getLogger(ProcessInstanceController.class);

    private final ProcessRuntime processRuntime;

    public ProcessInstanceController(ProcessRuntime processRuntime) {
        this.processRuntime = processRuntime;
    }

    @GetMapping("/process-instances")
    @ApiOperation(value = "列出流程实例", notes = "列出流程实例")
    public RestMessage getProcessInstances() {
        RestMessage restMessage = new RestMessage();
        List<ProcessInstance> processInstances = null;
        try {
            processInstances = processRuntime.processInstances(Pageable.of(0, 10)).getContent();
        } catch (Exception e) {
            e.printStackTrace();
            restMessage = RestMessage.fail("查询失败", e.getMessage());
        }
        if (processInstances != null) {
            restMessage = RestMessage.success("查询成功", processInstances);
        }

        return restMessage;
    }

    @GetMapping("/process-instance-meta/{processInstanceId}")
    @ApiOperation(value = "根据processInstanceId列出流程实例", notes = "根据processInstanceId列出流程实例")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "processInstanceId", value = "流程实例KEY", dataType = "String", paramType = "path", example = "")
    })
    public RestMessage getProcessInstanceMeta(@PathVariable(value = "processInstanceId") String processInstanceId) {
        RestMessage restMessage = new RestMessage();
        ProcessInstanceMeta processInstanceMeta = null;
        try {
            processInstanceMeta = processRuntime.processInstanceMeta(processInstanceId);
        } catch (Exception e) {
            e.printStackTrace();
            restMessage = RestMessage.fail("查询失败", e.getMessage());
        }

        if (processInstanceMeta != null) {
            restMessage = RestMessage.success("查询成功", processInstanceMeta);
        }

        return restMessage;
    }
}
