# SpringBoot2_Activiti7 demo
SpringBoot2集成Activiti7、Swagger、Druid

## 一、环境 ##
- IDEA
- spring2.1.5
- Activiti7
- Swagger 2.9.2
- Druid 1.1.16
- mysql 5.7
- JAVA 8

[![LICENSE](https://img.shields.io/badge/license-Anti%20996-blue.svg)](https://github.com/996icu/996.ICU/blob/master/LICENSE)



## 二、Activiti7的使用流程

Activiti 是一个工作流引擎（其实就是一堆 jar 包 API），业务系统使用 activiti 来对系统的业务流

程进行自动化管理，为了方便业务系统访问(操作)activiti 的接口或功能，通常将 activiti 环境与业务

系统的环境集成在一起。

### 1. 流程定义

使用 activiti 流程建模工具(activity-designer)定义业务流程(.bpmn 文件) 。

.bpmn 文件就是业务流程定义文件，通过 xml 定义业务流程。

如果使用其它公司开发的工作作引擎一般都提供了可视化的建模工具(Process Designer)用于生

成流程定义文件，建模工具操作直观，一般都支持图形化拖拽方式、多窗口的用户界面、丰富的过

程图形元素、过程元素拷贝、粘贴、删除等功能。

### 2. 流程定义部署

向 activiti 部署业务流程定义（.bpmn 文件）。

使用 activiti 提供的 api 向 activiti 中部署.bpmn 文件（一般情况还需要一块儿部署业务流程的图

片.png）

### 3. 启动一个流程实例（ProcessInstance）

启动一个流程实例表示开始一次业务流程的运行，比如员工请假流程部署完成，如果张三要请

假就可以启动一个流程实例，如果李四要请假也启动一个流程实例，两个流程的执行互相不影

响，就好比定义一个 java 类，实例化两个对象一样，部署的流程就好比 java 类，启动一个流程

实例就好比 new 一个 java 对象。

### 4. 用户查询待办任务(Task)

因为现在系统的业务流程已经交给 activiti 管理，通过 activiti 就可以查询当前流程执行到哪了，

当前用户需要办理什么任务了，这些 activiti帮我们管理了，而不像上边需要我们在 sql语句中的where

条件中指定当前查询的状态值是多少。

### 5. 用户办理任务

用户查询待办任务后，就可以办理某个任务，如果这个任务办理完成还需要其它用户办理，比如采

购单创建后由部门经理审核，这个过程也是由 activiti 帮我们完成了，不需要我们在代码中硬编码指

定下一个任务办理人了。

### 6. 流程结束

当任务办理完成没有下一个任务/结点了，这个流程实例就完成了。



请假流程example：

```
活动ID:4ba54191-ec58-11ea-8247-00ff4adba090
流程实例ID:4ba0fbcd-ec58-11ea-8247-00ff4adba090
活动名称：用户提交申请
办理人：zhangsan
开始时间：Tue Sep 01 21:37:35 CST 2020
结束时间：Tue Sep 01 21:40:42 CST 2020
活动ID:bad04602-ec58-11ea-aa39-00ff4adba090
流程实例ID:4ba0fbcd-ec58-11ea-8247-00ff4adba090
活动名称：部门经理审批
办理人：lisi
开始时间：Tue Sep 01 21:40:42 CST 2020
结束时间：Tue Sep 01 21:43:47 CST 2020
活动ID:28f0901b-ec59-11ea-9a3b-00ff4adba090
流程实例ID:4ba0fbcd-ec58-11ea-8247-00ff4adba090
活动名称：人事审批
办理人：wangwu
开始时间：Tue Sep 01 21:43:47 CST 2020
结束时间：Tue Sep 01 21:45:15 CST 2020
```



## 三、Activiti7表定义



数据库表的命名规则：

Activiti 的表都以 ACT_开头。 第二部分是表示表的用途的两个字母标识。 用途也和服务的 API 对

应。

- ACT_RE_*: 'RE'表示 repository。 这个前缀的表包含了流程定义和流程静态资源 （图片，

规则，等等）。

- ACT_RU_*: 'RU'表示 runtime。 这些运行时的表，包含流程实例，任务，变量，异步任务，

等运行中的数据。 Activiti 只在流程实例执行过程中保存这些数据， 在流程结束时就会删

除这些记录。 这样运行时表可以一直很小速度很快。

- ACT_HI_*: 'HI'表示 history。 这些表包含历史数据，比如历史流程实例， 变量，任务等

等。

- ACT_GE_*: GE 表示 general。通用数据， 用于不同场景下。