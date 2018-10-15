package com.example.activiti.listener;

import com.example.activiti.util.SpringUtil;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * @Author:郭友文
 * @Data:2018/8/24 11:18
 * @Description: 监听器
 */
@SuppressWarnings("serial")
@Service
public class MyTaskListener implements TaskListener {
    private static Logger logger=LoggerFactory.getLogger(MyTaskListener.class);

    @Override
    public void notify(DelegateTask delegateTask) {
        String ProcessInsId=delegateTask.getExecutionId();
        RuntimeService runtimeService= SpringUtil.getBean(RuntimeService.class);
        Map<String,Object> var=runtimeService.getVariables(ProcessInsId);
        delegateTask.setAssignee((String) var.getOrDefault("proposer","defect"));
//        delegateTask.addCandidateUsers(Arrays.asList("zhangsan","lisi","wangwu"));
        logger.info("设置审批人完成:"+delegateTask.getAssignee());
        logger.info(delegateTask.toString());
    }
}
