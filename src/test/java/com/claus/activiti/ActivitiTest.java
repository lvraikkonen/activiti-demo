package com.claus.activiti;

import com.claus.activiti.config.SecurityUtil;
import org.activiti.api.process.model.builders.ProcessPayloadBuilder;
import org.activiti.api.process.runtime.ProcessRuntime;
import org.activiti.api.runtime.shared.query.Page;
import org.activiti.api.runtime.shared.query.Pageable;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.task.Task;
import org.activiti.api.task.model.builders.TaskPayloadBuilder;
import org.activiti.api.task.runtime.TaskRuntime;
import org.activiti.engine.HistoryService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * SpringBoot与Junit整合，测试流程定义的相关操作
 *  任务完成
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class ActivitiTest {
    @Autowired
    private RepositoryService repositoryService;
    @Autowired
    private TaskService taskService;
    @Autowired
    private RuntimeService runtimeService;
    @Autowired
    private HistoryService historyService;
    /**
     * 启动一个实例
     */
    @Test
    public void startProcessInstance() {
        System.out.println("Number of process definitions : "+ repositoryService.createProcessDefinitionQuery().count());
        System.out.println("Number of tasks : " + taskService.createTaskQuery().count());
        runtimeService.startProcessInstanceByKey("holiday", "busiKey"); // 启动流程实例的时候指定businesskey(业务标识)
    }

    /**
     * 查询指定流程的所有实例
     */
    @Test
    public void processInstanceQuery() {
        String processDefinitionKey = "holiday";
        List<ProcessInstance> processInstanceList = runtimeService.createProcessInstanceQuery().processDefinitionKey(processDefinitionKey).list();
        for (ProcessInstance instance : processInstanceList) {
            System.out.println("流程实例id： " + instance.getProcessInstanceId());
            System.out.println("所属流程定义id： " + instance.getProcessDefinitionId());
            System.out.println("是否执行完成： " + instance.isEnded());
            System.out.println("是否暂停： " + instance.isSuspended());
            System.out.println(" 当 前 活 动 标 识 ： " + instance.getActivityId());
        }
    }

    /**
     * 查询用户的任务列表
     */
    @Test
    public void taskQuery() {
        //根据流程定义的key,负责人assignee来实现当前用户的任务列表查询
        List<Task> list = taskService.createTaskQuery()
                .processDefinitionKey("holiday")
                .taskAssignee("wangwu")
                .list();

        if(list!=null && list.size()>0){
            for(Task task:list){
                System.out.println("任务ID:"+task.getId());
                System.out.println("任务名称:"+task.getName());
                System.out.println("任务的创建时间:"+task.getCreateTime());
                System.out.println("任务的办理人:"+task.getAssignee());
                System.out.println("流程实例ID："+task.getProcessInstanceId());
                System.out.println("执行对象ID:"+task.getExecutionId());
                System.out.println("流程定义ID:"+task.getProcessDefinitionId());
                System.out.println("getOwner:"+task.getOwner());
                System.out.println("getCategory:"+task.getCategory());
                System.out.println("getDescription:"+task.getDescription());
                System.out.println("getFormKey:"+task.getFormKey());
                Map<String, Object> map = task.getProcessVariables();
                for (Map.Entry<String, Object> m : map.entrySet()) {
                    System.out.println("key:" + m.getKey() + " value:" + m.getValue());
                }
                for (Map.Entry<String, Object> m : task.getTaskLocalVariables().entrySet()) {
                    System.out.println("key:" + m.getKey() + " value:" + m.getValue());
                }

            }
        }
    }

    /**
     * 完成任务
     */
    @Test
    public void completeTask(){
        //任务ID
        String taskId = "28f0901b-ec59-11ea-9a3b-00ff4adba090";
        taskService.complete(taskId);
        System.out.println("完成任务：任务ID："+taskId);
    }

    /**
     * 历史活动实例查询
     */
    @Test
    public void queryHistoryTask() {
        List<HistoricTaskInstance> list = historyService.createHistoricTaskInstanceQuery() // 创建历史活动实例查询
                .processInstanceId("4ba0fbcd-ec58-11ea-8247-00ff4adba090") // 执行流程实例id
                .orderByTaskCreateTime()
                .asc()
                .list();
        for (HistoricTaskInstance hai : list) {
            System.out.println("活动ID:" + hai.getId());
            System.out.println("流程实例ID:" + hai.getProcessInstanceId());
            System.out.println("活动名称：" + hai.getName());
            System.out.println("办理人：" + hai.getAssignee());
            System.out.println("开始时间：" + hai.getStartTime());
            System.out.println("结束时间：" + hai.getEndTime());
        }
    }


    /**
     * 删除已经部署的流程定义
     * delete from ACT_RE_DEPLOYMENT 流程部署信息表;
     * ACT_RE_PROCDEF 流程定义数据表;
     * ACT_GE_BYTEARRAY 二进制数据表;
     */
    @Test
    public void deleteProcessDefinition(){
        //执行删除流程定义  参数代表流程部署的id
        repositoryService.deleteDeployment("b10a151b-3366-11ea-bc18-30b49ec7161f");
    }

    /**
     * 删除流程实例
     */
    @Test
    public void deleteProcessInstance(){
        String processInstanceId = "80a5703b-35c0-11ea-aa1a-30b49ec7161f";

        //当前流程实例没有完全结束的时候，删除流程实例就会失败
        runtimeService.deleteProcessInstance(processInstanceId,"删除原因");
    }



    /**
     * 操作流程的挂起、激活
     */
    @Test
    public void activateProcessDefinitionById(){
        //查询流程定义对象
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().processDefinitionKey("myProcess_1").singleResult();
        //当前流程定义的实例是否都为暂停状态
        boolean suspended = processDefinition.isSuspended();

        String processDefinitionId = processDefinition.getId();

        if(suspended){
            //挂起状态则激活
            repositoryService.activateProcessDefinitionById(processDefinitionId,true,new Date());
            System.out.println("流程定义："+processDefinitionId+"激活");
        }else{
            //激活状态则挂起
            repositoryService.suspendProcessDefinitionById(processDefinitionId,true,new Date());
            System.out.println("流程定义："+processDefinitionId+"挂起");
        }
    }


    /**
     * 单个流程实例的挂起，激活
     */
    @Test
    public void activateProcessInstanceById(){
        //查询流程实例对象
        ProcessInstance processInstance = runtimeService.createProcessInstanceQuery().processInstanceId("80a5703b-35c0-11ea-aa1a-30b49ec7161f").singleResult();

        //当前流程定义的实例是否都为暂停状态
        boolean suspended = processInstance.isSuspended();

        String processInstanceId = processInstance.getId();
        if(suspended){
            //激活
            runtimeService.activateProcessInstanceById(processInstanceId);
            System.out.println("流程："+processInstanceId+"激活");
        }else{
            //挂起
            runtimeService.suspendProcessInstanceById(processInstanceId);
            System.out.println("流程："+processInstanceId+"挂起");
        }
    }
}