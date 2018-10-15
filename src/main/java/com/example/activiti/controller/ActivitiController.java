package com.example.activiti.controller;

import com.alibaba.fastjson.JSONObject;
import com.example.activiti.entity.ProcessNodeHisDto;
import com.example.activiti.service.api.IActivitiService;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.history.HistoricVariableInstance;
import org.activiti.engine.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @Author:郭友文
 * @Data:2018/7/25 16:59
 * @Description:
 */

@RestController
@RequestMapping("/example/activiti")
public class ActivitiController {
    @Autowired
    private IActivitiService activitiService;

    @RequestMapping(value = "/startProcess",method = RequestMethod.GET)
    public String startProcess(String key,String proposer,String[] approver){
        Map<String,Object> users=new HashMap<>();
        users.put("proposer",proposer);
        users.put("approver", Arrays.asList(approver));
        activitiService.startProcesse(key,users);
        return "process start complete!";
    }

    @RequestMapping(value = "/getUserTask",method = RequestMethod.GET)
    public String getUserTask(String key,String userId){
        List<Task> taskList=activitiService.findTaskByProcesseKeyAndUser(key,userId);
        return taskList.toString();
    }

    @RequestMapping(value = "/getUserHisTask",method = RequestMethod.GET)
    public String getUserHisTask(String key,String userId){
        List<HistoricTaskInstance> hisTaskList=activitiService.findHistoryByKeyAndUser(key,userId);
        return hisTaskList.toString();
    }

    @RequestMapping(value = "/getUserHisVar",method = RequestMethod.GET)
    public String getUserHisVar(String[] taskIds){
        Set<String> taskIdSet= Arrays.stream(taskIds).collect(Collectors.toSet());
        List<HistoricVariableInstance> hisVarList=activitiService.findHistoryVarByTaskIdsAndVarName(taskIdSet,"status");
        return hisVarList.toString();
    }

    @RequestMapping(value = "/completeTask",method = RequestMethod.GET)
    public String completeTask(String taskId,String userId,Integer type,String status){
        Map<String,Object> codition=new HashMap<>();
        codition.put("status",status);
        activitiService.compeleteTask(taskId,userId,type,codition);
        return "task:"+taskId+" complete by "+userId;
    }

    @RequestMapping(value = "/createNewProcess",method =RequestMethod.GET)
    public String createNewProcee(){
        String key=activitiService.createNewProcess();
        return "create success,process key:"+key;
    }

    @RequestMapping(value = "/deleteProcess",method =RequestMethod.GET)
    public String deleteProcess(String key){
        activitiService.deleteProcess(key);
        return "delete success,process key:"+key;
    }

    @RequestMapping(value = "/getHis",method = RequestMethod.GET)
    public String getHis(String key,String user) throws Exception{
        List<ProcessNodeHisDto> hisList=activitiService.findApproveHisBykeyAndUser(key,user);
        return JSONObject.toJSONString(hisList);
    }
    @RequestMapping(value = "/startParallel",method = RequestMethod.GET)
    public String startParallel(String[] users,String proposer){
        Map<String,Object> var=new HashMap<>();
        var.put("proposer",proposer);
        var.put("assigneeList",Arrays.asList(users));
        activitiService.startParallelProcess();
        return activitiService.startProcesse("testP",var);
    }
    @RequestMapping(value = "/completeParallel",method = RequestMethod.GET)
    public String completeParallel(String taskId,String userId,String status){
        Map<String,Object> var=new HashMap<>();
        var.put("status",status);
        activitiService.compeleteTask(taskId,userId,1,var);
        return "approve complete";
    }

    @RequestMapping(value = "/processFinish",method = RequestMethod.GET)
    public String processFinish(String processInsId){
        return activitiService.processFinish(processInsId).toString();
    }
}
