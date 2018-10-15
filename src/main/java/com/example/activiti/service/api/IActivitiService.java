package com.example.activiti.service.api;

import com.example.activiti.entity.ProcessNodeHisDto;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.history.HistoricVariableInstance;
import org.activiti.engine.task.Task;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @Author:郭友文
 * @Data:2018/7/25 15:28
 * @Description:
 */

public interface IActivitiService {
    String startProcesse(String key,Map<String,Object> var);

    List<Task> findTaskByProcesseKeyAndUser(String key,String userId);

    void compeleteTask(String taskId,String userId,Integer type,Map<String,Object> var);

    List<HistoricTaskInstance> findHistoryByKeyAndUser(String key,String userId);

    /**
     * 根据任务id查找流程历史变量
     * @param taskIds 任务id集合
     * @return
     */
    List<HistoricVariableInstance> findHistoryVarByTaskIdsAndVarName(Set<String> taskIds,String varName);
    String createNewProcess();

    void deleteProcess(String key);

    List<ProcessNodeHisDto> findApproveHisBykeyAndUser(String key, String user) throws Exception;

    void startParallelProcess();


    Boolean processFinish(String processInsId);
}