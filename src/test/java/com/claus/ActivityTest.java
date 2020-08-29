package com.claus;

import com.claus.config.SecurityUtil;
import org.activiti.api.process.model.ProcessDefinition;
import org.activiti.api.process.model.ProcessInstance;
import org.activiti.api.process.model.builders.ProcessPayloadBuilder;
import org.activiti.api.process.runtime.ProcessRuntime;
import org.activiti.api.runtime.shared.query.Page;
import org.activiti.api.runtime.shared.query.Pageable;
import org.activiti.api.task.model.Task;
import org.activiti.api.task.model.builders.TaskPayloadBuilder;
import org.activiti.api.task.runtime.TaskRuntime;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.repository.Deployment;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * SpringBoot与Junit整合，测试流程定义的相关操作
 *  任务完成
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class ActivityTest {
    @Autowired
    private ProcessRuntime processRuntime; //实现流程定义相关操作

    @Autowired
    private TaskRuntime taskRuntime; //实现任务相关操作

    @Autowired
    private SecurityUtil securityUtil;//SpringSecurity相关的工具类

    @Test
    public void testDeploy(){
        // 1. 创建ProcessEngine对象
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        // 2. 得到RepositoryService实例
        RepositoryService repositoryService = processEngine.getRepositoryService();
        // 3. 部署
        Deployment deployment = repositoryService.createDeployment()
                .addClasspathResource("processes/leaveProcess.xml")
                //.addClasspathResource("processes/MyProcess.bpmn")
                .name("请假申请单流程")
                .deploy();
        // 4. 输出部署的一些信息
        System.out.println(deployment.getName());
        System.out.println(deployment.getId());
    }

    //流程定义信息的查看
    @Test
    public void testDefinition(){
        securityUtil.logInAs("salaboy");//SpringSecurity认证
        //分页查询出流程定义信息
        Page<ProcessDefinition> processDefinitionPage = processRuntime
                .processDefinitions(Pageable.of(0, 10));
        System.out.println("可用的流程定义数量：" + processDefinitionPage.getTotalItems());//查看已部署的流程个数
        for (ProcessDefinition pd : processDefinitionPage.getContent()) {
            System.out.println("流程定义：" + pd);
        }
    }

    /**
     * 启动流程实例
     */
    @Test
    public void testStartProcess() {
        securityUtil.logInAs("salaboy");
        ProcessInstance pi = processRuntime.start(ProcessPayloadBuilder
                .start()
                .withProcessDefinitionKey("myProcess_1")
                .build());//启动流程实例
        System.out.println("流程实例ID：" + pi.getId());
    }

    /**
     * 查询任务，并完成自己的任务
     */
    @Test
    public void testTask() {
        securityUtil.logInAs("ryandawsonuk");//认证
        Page<Task> taskPage=taskRuntime.tasks(Pageable.of(0,10));
        if (taskPage.getTotalItems()>0){
            for (Task task:taskPage.getContent()){
                System.out.println("任务："+task);

                //拾取任务
                taskRuntime.claim(TaskPayloadBuilder.claim().withTaskId(task.getId()).build());

                //执行任务
                taskRuntime.complete(TaskPayloadBuilder.complete().withTaskId(task.getId()).build());
            }
        }

        //完成后再次查询任务
        System.out.println("再次查询任务的结果：");
        Page<Task> taskPage2=taskRuntime.tasks(Pageable.of(0,10));
        if (taskPage2.getTotalItems()>0){
            System.out.println("剩余任务："+taskPage2.getContent());
        }
    }
}