package com.claus.activiti;

import com.claus.activiti.config.SecurityUtil;
import com.claus.activiti.model.Holiday;
import com.claus.activiti.utils.RestMessage;
import org.activiti.engine.HistoryService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.DeploymentBuilder;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ApplyHolidayProcessFullTest {

    @Autowired
    private SecurityUtil securityUtil;
    @Autowired
    private RepositoryService repositoryService;
    @Autowired
    private TaskService taskService;
    @Autowired
    private RuntimeService runtimeService;
    @Autowired
    private HistoryService historyService;


    @Test // 部署流程
    public void deploy() {
        securityUtil.logInAs("salaboy");
        String bpmnName = "holiday_with_gateway";
        RestMessage restMessage = new RestMessage();

        DeploymentBuilder deploymentBuilder = repositoryService.createDeployment().name("请假流程全流程");
        Deployment deployment = null;
        try {
            deployment = deploymentBuilder.addClasspathResource("processes/" + bpmnName + ".bpmn")
                    .addClasspathResource("processes/" + bpmnName + ".png").deploy();

        } catch (Exception e) {
            e.printStackTrace();
        }
        if (deployment != null) {
            Map<String, String> result = new HashMap<>(2);
            result.put("deployID", deployment.getId());
            result.put("deployName", deployment.getName());
            restMessage = RestMessage.success("部署成功", result);
            System.out.println("部署成功");
            System.out.println("部署ID: " + deployment.getId());
            System.out.println("部署名称: " + deployment.getName());
        }
        System.out.println(restMessage);
    }

    @Test // 启动流程实例
    public void startProcessInstance() {
        System.out.println("Number of process definitions : "+ repositoryService.createProcessDefinitionQuery().count());
        System.out.println("Number of tasks : " + taskService.createTaskQuery().count());
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
        runtimeService.startProcessInstanceByKey("holiday_gateway", "busiKey2", map); // 启动流程实例的时候传参数
        System.out.println("启动流程后：");
        System.out.println("Number of process definitions : "+ repositoryService.createProcessDefinitionQuery().count());
        System.out.println("Number of tasks : " + taskService.createTaskQuery().count());
    }

    @Test // 查询指定流程的所有实例
    public void processInstanceQuery() {
        String processDefinitionKey = "holiday_var";
        List<ProcessInstance> processInstanceList = runtimeService.createProcessInstanceQuery().processDefinitionKey(processDefinitionKey).list();
        for (ProcessInstance instance : processInstanceList) {
            System.out.println("流程实例id： " + instance.getProcessInstanceId());
            System.out.println("所属流程定义id： " + instance.getProcessDefinitionId());
            System.out.println("是否执行完成： " + instance.isEnded());
            System.out.println("是否暂停： " + instance.isSuspended());
            System.out.println(" 当 前 活 动 标 识 ： " + instance.getActivityId());
        }
    }

    @Test // 查询用户的任务列表
    public void taskQuery() {
        //根据流程定义的key,负责人assignee来实现当前用户的任务列表查询
        List<Task> list = taskService.createTaskQuery()
                .processDefinitionKey("holiday_gateway")
                .taskAssignee("renliu")
                .list();

        if (list != null && list.size() > 0) {
            for (Task task : list) {
                System.out.println("任务ID:" + task.getId());
                System.out.println("任务名称:" + task.getName());
                System.out.println("任务的创建时间:" + task.getCreateTime());
                System.out.println("任务的办理人:" + task.getAssignee());
                System.out.println("流程实例ID：" + task.getProcessInstanceId());
                System.out.println("执行对象ID:" + task.getExecutionId());
                System.out.println("流程定义ID:" + task.getProcessDefinitionId());
                System.out.println("getOwner:" + task.getOwner());
                System.out.println("getCategory:" + task.getCategory());
                System.out.println("getDescription:" + task.getDescription());
                System.out.println("getFormKey:" + task.getFormKey());
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

    @Test // 根据候选人查询组任务
    public void findGroupTaskList(){
        String candidateUser = "fin1";
        List<Task> list = taskService.createTaskQuery()
                .processDefinitionKey("holiday_gateway")
                .taskCandidateUser(candidateUser) // 查询该候选人的任务
                .list();
        for (Task task: list) {
            System.out.println("--------------------------------------");
            System.out.println("流程实例Id: " + task.getProcessInstanceId());
            System.out.println("任务Id: " + task.getId());
            System.out.println("任务负责人: " + task.getAssignee());
            System.out.println("任务名称: " + task.getName());
            System.out.println("--------------------------------------");
        }
    }

    @Test
    public void claimTask(){
        String userId = "fin2";
        // 查询
        Task task = taskService.createTaskQuery()
                               .processDefinitionKey("holiday_gateway")
                               .taskCandidateUser(userId)
                               .singleResult();
        if (task != null) {
            taskService.claim(task.getId(), userId);
            System.out.println("任务拾取成功");
            System.out.println("任务Id: " + task.getId());
            System.out.println("任务负责人: " + task.getAssignee());
        }
    }

    @Test // 用户归还任务，将个人任务变为组任务
    public void setAssigneeToGroupTask() {
        String taskId = "fc833e1c-ede3-11ea-b012-00ff4adba090";
        String userId = "fin1";

        // 校验当前userID是否是taskId的负责人，只有负责人才能归还组任务
        Task task = taskService.createTaskQuery().taskId(taskId)
                               .taskAssignee(userId).singleResult();
        if (task != null) {
            taskService.setAssignee(taskId, null); // 让任务执行人为null
        }
    }

    @Test // 任务交接，任务负责人将任务交给其他候选人办理
    public void setAssigneeToCandidateUser() {
        String taskId = "fc833e1c-ede3-11ea-b012-00ff4adba090";
        String userId = "fin1";
        String candidate = "fin2";

        // 校验当前userID是否是taskId的负责人
        Task task = taskService.createTaskQuery().taskId(taskId)
                               .taskAssignee(userId).singleResult();
        if (task != null) {
            taskService.setAssignee(taskId, candidate);
        }
    }

    /**
     * 完成任务
     */
    @Test
    public void completeTask(){
        //任务ID
        String taskId = "fc833e1c-ede3-11ea-b012-00ff4adba090";
        taskService.complete(taskId);
        System.out.println("完成任务：任务ID："+taskId);
    }

    /**
     * 历史活动实例查询
     */
    @Test
    public void queryHistoryTask() {
        List<HistoricTaskInstance> list = historyService.createHistoricTaskInstanceQuery() // 创建历史活动实例查询
                .processInstanceId("dc08fdd6-ede2-11ea-a886-00ff4adba090") // 执行流程实例id
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
}
