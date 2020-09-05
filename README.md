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



![](https://github.com/lvraikkonen/activiti-demo/blob/master/src/main/resources/processes/holiday_with_gateway.png)

请假流程example：

```
活动ID:2228681b-ef65-11ea-bb67-00ff4adba090
流程实例ID:222533bd-ef65-11ea-bb67-00ff4adba090
活动名称：employee apply holiday
办理人：zhangsan
开始时间：Sat Sep 05 18:47:03 CST 2020
结束时间：Sat Sep 05 18:50:16 CST 2020
活动ID:9599c83d-ef65-11ea-bb67-00ff4adba090
流程实例ID:222533bd-ef65-11ea-bb67-00ff4adba090
活动名称：dept manager check
办理人：lisi
开始时间：Sat Sep 05 18:50:16 CST 2020
结束时间：Sat Sep 05 19:45:18 CST 2020
活动ID:45ab4f8d-ef6d-11ea-818d-00ff4adba090
流程实例ID:222533bd-ef65-11ea-bb67-00ff4adba090
活动名称：boss check
办理人：wangwu
开始时间：Sat Sep 05 19:45:18 CST 2020
结束时间：Sat Sep 05 19:48:59 CST 2020
活动ID:c90764af-ef6d-11ea-818d-00ff4adba090
流程实例ID:222533bd-ef65-11ea-bb67-00ff4adba090
活动名称：HR logged
办理人：renliu
开始时间：Sat Sep 05 19:48:59 CST 2020
结束时间：Sat Sep 05 19:50:13 CST 2020
活动ID:f5a31245-ef6d-11ea-818d-00ff4adba090
流程实例ID:222533bd-ef65-11ea-bb67-00ff4adba090
活动名称：kaoqin record
办理人：kaoqin2
开始时间：Sat Sep 05 19:50:13 CST 2020
结束时间：Sat Sep 05 19:54:19 CST 2020
活动ID:f5a31243-ef6d-11ea-818d-00ff4adba090
流程实例ID:222533bd-ef65-11ea-bb67-00ff4adba090
活动名称：finance record
办理人：fin1
开始时间：Sat Sep 05 19:50:13 CST 2020
结束时间：Sat Sep 05 19:52:41 CST 2020
活动ID:884a4121-ef6e-11ea-818d-00ff4adba090
流程实例ID:222533bd-ef65-11ea-bb67-00ff4adba090
活动名称：notice employee
办理人：tongzhi
开始时间：Sat Sep 05 19:54:20 CST 2020
结束时间：Sat Sep 05 19:58:45 CST 2020
```



## 三、Activiti7表定义



数据库表的命名规则：

Activiti 的表都以 ACT_开头。 第二部分是表示表的用途的两个字母标识。 用途也和服务的 API 对应。

- ACT_RE_*: 'RE'表示 repository。 这个前缀的表包含了流程定义和流程静态资源 （图片，规则，等等）。

- ACT_RU_*: 'RU'表示 runtime。 这些运行时的表，包含流程实例，任务，变量，异步任务，等运行中的数据。 Activiti 只在流程实例执行过程中保存这些数据， 在流程结束时就会删除这些记录。 这样运行时表可以一直很小速度很快。_
- ACT_HI_*: 'HI'表示 history。 这些表包含历史数据，比如历史流程实例， 变量，任务等等。

- ACT_GE_*: GE 表示 general。通用数据， 用于不同场景下。



## 四、用户处理任务后表中数据做了哪些变化

#### 

- **处理任务代码逻辑**

  ![在这里插入图片描述](https://www.pianshen.com/images/592/8af27a34e532c784fe8b668b12541308.png)

- **数据库表变化**

1. **act_ru_task**表

   ![在这里插入图片描述](https://www.pianshen.com/images/408/1bc38e9f47f5731697d7859e7710c7b0.png)
   说明：由于sanding已经填写了请假申请单，因此activiti把表中原来那条记录给删除了。又新插入了一条了数据。而这条数据就是部门经理这个负责人进行请假单审批了。而这里字段ASSIGNEE为什么为null，是因为我们在流程实例化的时候，并没有添加具体的某个负责人。测试的时候，可以手动操作数据库去填写即可。

2. **act_hi_actinst**表

   ![在这里插入图片描述](https://www.pianshen.com/images/573/72d8e45e7a73c52c3972e406c6240abd.png)
   说明：原来这张表有两条数据。2504这条数据的END_TIME是null的, 而当sanding这人负责人(只要有任务都是负责人)填写了请假申请单后，END_TIME字段就有值了。且，**又新插入**了一条数据5001,这条数据是任务流程图中的第三个环节，**刚好一个环节（节点，任务节点）一条行为的历史记录**，而且我们依然可以看见它的END_TIME字段依然为null。

3. 通过分析sanding处理任务后，得到的结论。那么部门经理处理完任务后，也是同样的效果，继续向后面节点移动（领导审批（leader check））。而当leader check完成后，那么act_hi_actinst表中一定对应着5条记录，每个环节（每个节点）对应一条记录。而act_ru_task一定没有数据了。因为这个流程实例已经完成了。而为什么要删除，就是为了保证表中的数据量小，加快查询速度。

4. 变化的不只是这两种表，凡是逻辑上相关联的表，数据都会变化的。但是在实际应用场景中，我们可以选择记录一些重要的信息，一些不重要的记录，就可以丢掉。

5. 此操作影响的表有(可能操作不是特别规范，因为构建流程图的时候，是没有参与者，导致有些表其实是没有生成记录的，因此下表的第2条记录和第5条记录就没有数据)

| 表名                | 是否受影响 |
| :------------------ | :--------- |
| act_hi_actinst      | 是         |
| act_hi_identitylink | 是         |
| act_hi_taskinst     | 是         |
| act_ru_execution    | 是         |
| act_ru_identitylink | 是         |
| act_ru_task         | 是         |

- 部门经理进行任务处理（lisi）这个名称是手动插入到数据的，自定义名称即可。
  lisi未处理任务的时候，数据库表情况。
  ![在这里插入图片描述](https://www.pianshen.com/images/810/d45fab596cc0678227745297e56f4fda.png)
  代码查询演示
  ![在这里插入图片描述](https://www.pianshen.com/images/535/99fc884d94f3f2a365062153fa1ca677.png)
  控制台打印
  ![在这里插入图片描述](https://www.pianshen.com/images/794/4e31ff86fb7e283a1b9e02fea96ae792.png)
  lisi执行任务，代码逻辑演示
  ![在这里插入图片描述](https://www.pianshen.com/images/936/f46324f8ecfbae2366fa6330b3ad88d8.png)

数据库表变化

1. act_ru_task
   ![在这里插入图片描述](https://www.pianshen.com/images/32/d6138ffc72fdb550956843726fd51c50.png)
   ASSIGNEE是null, 是因为我在流程定义的时候，未选择参与者，实际应用场景中一定是有的，不然审批没有任何意义。当wangwu去完成任务的时候，我们需要手动把wangwu赋值到ASSIGNEE字段上即可。
   这里的结果，跟上文的推测，一模一样。
2. act_hi_actinst
   ![在这里插入图片描述](https://www.pianshen.com/images/110/b9e11c3df65857e3d537197933edd18e.png)
   数据变化过程：跟上文分析sanding处理任务后是一样的。也跟推测一模一样的。

- 领导进行任务处理(wangwu)这个名称是手动插入到数据的，自定义名称即可。
  wangwu未执行任务时，代码逻辑演示

![在这里插入图片描述](https://www.pianshen.com/images/745/32c33a7523f3d68154f4ca0691312f11.png)
控制台打印
![在这里插入图片描述](https://www.pianshen.com/images/998/cb68c05345042a0a5f7df52ab1dbc39e.png)

wangwu未执行任务时，数据库act_ru_task表
![在这里插入图片描述](https://www.pianshen.com/images/235/7c7c5ecf4b658bbf8b0e0a8f0d40cefb.png)

wangwu处理任务代码逻辑
![在这里插入图片描述](https://www.pianshen.com/images/535/34d730b0b936c2c8790d5ef685c4f887.png)

wangwu处理任务后数据库表变化

1. act_ru_task表（可以看见表中一条数据都没有了）
   ![在这里插入图片描述](https://www.pianshen.com/images/318/645e9502e97fa7b3de2fbef61983c446.png)
2. act_hi_actinst表（可以看见5条记录）
   ![在这里插入图片描述](https://www.pianshen.com/images/481/f17e423b66bffea4fbd8ad95650cd431.png)
   总结：流程定义图上有5个任务节点，分别是开始，填写申请单，部门经理审批，领导审批，结束。刚好对应5条记录。观察发现开始事件和结束时间的TASK_ID是null 。而这里的ASSIGNEE为什么是null可能跟自己定义流程图有关系。（后续找原因）
   多注意字段END_TIME是否为null