package com.example.activiti.service.impl;

import com.example.activiti.entity.ProcessNodeHisDto;
import com.example.activiti.listener.MyTaskListener;
import com.example.activiti.service.api.IActivitiService;
import org.activiti.bpmn.model.*;
import org.activiti.bpmn.model.Process;
import org.activiti.engine.*;
import org.activiti.engine.delegate.TaskListener;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.history.HistoricVariableInstance;
import org.activiti.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Author:郭友文
 * @Data:2018/7/25 15:29
 * @Description:
 */
@Service
public class ActivitiServiceImpl implements IActivitiService {
    @Autowired
    private ProcessEngine processEngine;
    @Autowired
    private RuntimeService runtimeService;
    @Autowired
    private TaskService taskService;
    @Autowired
    private HistoryService historyService;
    @Autowired
    private RepositoryService repositoryService;
    @Autowired
    private ProcessEngineConfigurationImpl processEngineConfiguration;
    public static final String DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss";


    @Override
    public String startProcesse(String key, Map<String, Object> var) {
       ProcessInstance processInstance=runtimeService.startProcessInstanceByKey(key,var);
       return processInstance.getId();
    }

    @Override
    public List<Task> findTaskByProcesseKeyAndUser(String key, String userId) {
        List<ProcessInstance> processInstances=runtimeService.createProcessInstanceQuery().superProcessInstanceId(key).list();
        List<Task> taskList=new ArrayList<>();
        if(processInstances!=null&&!processInstances.isEmpty()){
            List<String> ids=processInstances.stream().map(ProcessInstance::getId).collect(Collectors.toList());
            ids.add(key);
            taskList=taskService.createTaskQuery().processInstanceIdIn(ids).taskCandidateOrAssigned(userId).list();
        }
        else{
            taskList=taskService.createTaskQuery().processInstanceId(key).taskCandidateOrAssigned(userId).list();
        }
//        List<Task> taskList=taskService.createTaskQuery().processDefinitionKey(key).list();
        return taskList;
    }

    @Override
    public void compeleteTask(String taskId, String userId,Integer type, Map<String, Object> var) {
        taskService.claim(taskId, userId);
        taskService.setVariable(taskId,"status",var.get("status"));
        var.put("advice","意见"+Math.random());
        taskService.complete(taskId,var,true);
    }

    @Override
    public List<HistoricTaskInstance> findHistoryByKeyAndUser(String key, String userId) {
        List<HistoricTaskInstance> result=historyService.createHistoricTaskInstanceQuery().processDefinitionKey(key).taskAssignee(userId).list();
        return result;
    }

    @Override
    public List<HistoricVariableInstance> findHistoryVarByTaskIdsAndVarName(Set<String> taskIds,String varName) {
        if(varName!=null){
            return historyService.createHistoricVariableInstanceQuery().taskIds(taskIds).variableName(varName).list();
        }
        return historyService.createHistoricVariableInstanceQuery().taskIds(taskIds).list();
    }


    @Override
    public String createNewProcess() {
        BpmnModel model=new BpmnModel();
        Process process=new Process();
        model.addProcess(process);

        process.setId("test");
        process.setName("测试");
        //开始
        StartEvent startEvent=new StartEvent();
        startEvent.setId("startEvent");
        //结束
        EndEvent endEvent=new EndEvent();
        endEvent.setId("endEvent");
        //用户任务
        UserTask userTask=new UserTask();
        userTask.setId("testTask");
        userTask.setAssignee("${proposer}");

        UserTask userTask1=new UserTask();
        List<String> tempList=new ArrayList<>();
        tempList.add("${approver}");
        userTask1.setId("testTask1");
        userTask1.setCandidateUsers(tempList);

        //顺序流
        SequenceFlow sequenceFlow1=new SequenceFlow();
        sequenceFlow1.setSourceRef("startEvent");
        sequenceFlow1.setTargetRef("testTask");
//        sequenceFlow1.setConditionExpression("${status==null}");

        SequenceFlow sequenceFlow2=new SequenceFlow();
        sequenceFlow2.setSourceRef("testTask");
        sequenceFlow2.setTargetRef("testTask1");
        sequenceFlow2.setConditionExpression("${status==1}");

        SequenceFlow sequenceFlow5=new SequenceFlow();
        sequenceFlow5.setSourceRef("testTask");
        sequenceFlow5.setTargetRef("testTask");
        sequenceFlow5.setConditionExpression("${status==2}");

        SequenceFlow sequenceFlow3=new SequenceFlow();
        sequenceFlow3.setSourceRef("testTask1");
        sequenceFlow3.setTargetRef("endEvent");
        sequenceFlow3.setConditionExpression("${status==1}");

        SequenceFlow sequenceFlow4=new SequenceFlow();
        sequenceFlow4.setSourceRef("testTask1");
        sequenceFlow4.setTargetRef("testTask");
        sequenceFlow4.setConditionExpression("${status==2}");

        process.addFlowElement(startEvent);
        process.addFlowElement(userTask);
        process.addFlowElement(userTask1);
        process.addFlowElement(endEvent);
        process.addFlowElement(sequenceFlow1);
        process.addFlowElement(sequenceFlow2);
        process.addFlowElement(sequenceFlow3);
        process.addFlowElement(sequenceFlow4);
        process.addFlowElement(sequenceFlow5);

        Deployment deployment=repositoryService.createDeployment().addBpmnModel("test.bpmn",model).deploy();

        InputStream processBpmn = processEngine.getRepositoryService().getResourceAsStream(deployment.getId(), "test.bpmn");
        try {
            FileOutputStream fos=new FileOutputStream(new File("C:/Users/Administrator/Desktop/test.bpmn"));
            byte[] bytes=IOUtils.toByteArray(processBpmn);
            fos.write(bytes);
            fos.close();
            processBpmn.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "test";
    }

    @Override
    public void deleteProcess(String key) {
        repositoryService.deleteDeployment(key,true);
    }

    @Override
    public List<ProcessNodeHisDto> findApproveHisBykeyAndUser(String key, String user) throws Exception {
        List<ProcessNodeHisDto> resultList=new ArrayList<>();
        //查询任务历史记录
        List<HistoricTaskInstance> hisTaskList=this.findHistoryByKeyAndUser(key, user);
        if(hisTaskList==null||hisTaskList.isEmpty()){
            return new ArrayList<>();
        }
        //获取任务ids查询任务变量
        Set<String> taskIds=hisTaskList.stream().map(HistoricTaskInstance::getId).collect(Collectors.toSet());
        List<HistoricVariableInstance> hisVarList=this.findHistoryVarByTaskIdsAndVarName(taskIds,"status");
        //taskId-变量名称-变量值map
        Map<String,Map<String,String>> taskIdVarNameValueMap=null;
        if (hisVarList!=null) {
            taskIdVarNameValueMap=hisVarList.stream()
                    .collect(Collectors.groupingBy(HistoricVariableInstance::getTaskId,
                            Collectors.toMap(HistoricVariableInstance::getVariableName,hisVar->hisVar.getValue().toString())));
        }
        for(HistoricTaskInstance historicTaskInstance:hisTaskList){
            if(historicTaskInstance.getEndTime()==null){
                continue;
            }
            ProcessNodeHisDto processNodeHisDto=new ProcessNodeHisDto();
            Map<String,String> varMap=null;
            if(taskIdVarNameValueMap!=null) {
                varMap = taskIdVarNameValueMap.getOrDefault(historicTaskInstance.getId(), null);
            }
            String status="";
            if(varMap==null){
                processNodeHisDto.setHandlerOperation("发起审批");
            }
            else {
                status=varMap.get("status");
            }
            //todo 获取意见
            processNodeHisDto.setActualHandleTime(format(historicTaskInstance.getEndTime()));
            processNodeHisDto.setHandler(historicTaskInstance.getAssignee());
            if (status.equals("1")) {
                processNodeHisDto.setHandlerOperation("同意");
            }
            else if(status.equals("2")){
                processNodeHisDto.setHandlerOperation("拒绝");
            }
            else if(status.equals("3")){
                processNodeHisDto.setHandlerOperation("退回");
            }
            else{
                processNodeHisDto.setHandlerOperation("发起审批");
            }
            processNodeHisDto.setNodeName(historicTaskInstance.getName());
            processNodeHisDto.setTaskArriveTime(format(historicTaskInstance.getStartTime()));
            resultList.add(processNodeHisDto);
        }
        return resultList;
    }

    @Override
    public void startParallelProcess() {
        BpmnModel model=new BpmnModel();
        Process process=new Process();
        model.addProcess(process);

        process.setId("testP");
        process.setName("测试");
        //开始
        StartEvent startEvent=new StartEvent();
        startEvent.setId("startEvent");
        //结束
        EndEvent endEvent=new EndEvent();
        endEvent.setId("endEvent");
        //调用子流程
        CallActivity callActivity=new CallActivity();
        callActivity.setId("子流程");
        callActivity.setCalledElement("testParallel");
        List<IOParameter> inParameterList=new ArrayList<>();
        IOParameter ioParameter=new IOParameter();
        ioParameter.setSource("assigneeList");
        ioParameter.setTarget("assigneeList");
        inParameterList.add(ioParameter);
        ioParameter=new IOParameter();
        ioParameter.setSource("proposer");
        ioParameter.setTarget("proposer");
        inParameterList.add(ioParameter);
        callActivity.setInParameters(inParameterList);

        UserTask userTask=new UserTask();
        userTask.setId("用户任务");
//        List<ActivitiListener> listeners=new ArrayList<>();
//        ActivitiListener activitiListener=new ActivitiListener();
//        activitiListener.setEvent("create");
//        activitiListener.setImplementation("com.example.activiti.listener.MyTaskListener");
//        activitiListener.setImplementationType("class");
//        listeners.add(activitiListener);
//        userTask.setTaskListeners(listeners);

        userTask.setAssignee("${assignee}");
        MultiInstanceLoopCharacteristics multi=new MultiInstanceLoopCharacteristics();
        multi.setSequential(true);
        multi.setElementVariable("assignee");
        multi.setInputDataItem("${assigneeList}");
        multi.setCompletionCondition("${status==1}");
        userTask.setLoopCharacteristics(multi);
//        List<BoundaryEvent> eventList=new ArrayList<>();
//        BoundaryEvent boundaryEvent=new BoundaryEvent();
//        TimerEventDefinition timerEventDefinition=new TimerEventDefinition();
//        timerEventDefinition.setTimeDate("${date}");
//        boundaryEvent.setId("时间边界事件");
//        boundaryEvent.setAttachedToRef(userTask);
//        boundaryEvent.addEventDefinition(timerEventDefinition);

        //顺序流
        SequenceFlow sequenceFlow1=new SequenceFlow();
        sequenceFlow1.setSourceRef("startEvent");
        sequenceFlow1.setTargetRef("用户任务");
//        sequenceFlow1.setTargetRef("用户任务");

        SequenceFlow sequenceFlow3=new SequenceFlow();
        sequenceFlow3.setSourceRef("用户任务");
        sequenceFlow3.setTargetRef("子流程");

        SequenceFlow sequenceFlow2=new SequenceFlow();
        sequenceFlow2.setSourceRef("子流程");
        sequenceFlow2.setTargetRef("endEvent");

//        SequenceFlow sequenceFlow4=new SequenceFlow();
//        sequenceFlow4.setSourceRef("时间边界事件");
//        sequenceFlow4.setTargetRef("endEvent");

//        process.addFlowElement(boundaryEvent);
        process.addFlowElement(startEvent);
        process.addFlowElement(userTask);
        process.addFlowElement(endEvent);
        process.addFlowElement(callActivity);
        process.addFlowElement(sequenceFlow1);
        process.addFlowElement(sequenceFlow2);
        process.addFlowElement(sequenceFlow3);
//        process.addFlowElement(sequenceFlow4);
        Deployment deployment=repositoryService.createDeployment().addBpmnModel("testP.bpmn",model).deploy();
        InputStream processBpmn = processEngine.getRepositoryService().getResourceAsStream(deployment.getId(), "testP.bpmn");
        try {
            FileOutputStream fos=new FileOutputStream(new File("C:/Users/Administrator/Desktop/testP.bpmn"));
            byte[] bytes=IOUtils.toByteArray(processBpmn);
            fos.write(bytes);
            fos.close();
            processBpmn.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String format(Date date) throws Exception {
        if (date == null) {
            return "";
        }
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(DATETIME_FORMAT);
            return sdf.format(date);
        } catch (Exception ex) {
            Exception ue = new Exception("日期格式化产生异常", ex);
            throw ue;
        } catch (Throwable t) {
            throw new Exception("日期格式化产生异常", t);
        }
    }

    @Override
    public Boolean processFinish(String processInsId) {
        ProcessInstance processInstance=runtimeService.createProcessInstanceQuery().processInstanceId(processInsId)
                .singleResult();
        if(processInstance==null){
            return true;
        }
        return false;
    }
}
