package com.example.activiti.entity;

/**
 * @Author:郭友文
 * @Data:2018/7/30 15:46
 * @Description: 流程节点历史dto
 */

public class ProcessNodeHisDto {
    /**
     * 节点名称
     */
    private String nodeName;
    /**
     * 处理人
     */
    private String handler;
    /**
     * 任务到达时间
     */
    private String taskArriveTime;
    /**
     * 处理操作
     */
    private String handlerOperation;
    /**
     * 实际处理时间
     */
    private String actualHandleTime;
    /**
     * 意见
     */
    private String advice;

    public String getAdvice() {
        return advice;
    }

    public void setAdvice(String advice) {
        this.advice = advice;
    }

    public String getNodeName() {
        return nodeName;
    }

    public void setNodeName(String nodeName) {
        this.nodeName = nodeName;
    }

    public String getHandler() {
        return handler;
    }

    public void setHandler(String handler) {
        this.handler = handler;
    }

    public String getTaskArriveTime() {
        return taskArriveTime;
    }

    public void setTaskArriveTime(String taskArriveTime) {
        this.taskArriveTime = taskArriveTime;
    }

    public String getHandlerOperation() {
        return handlerOperation;
    }

    public void setHandlerOperation(String handlerOperation) {
        this.handlerOperation = handlerOperation;
    }

    public String getActualHandleTime() {
        return actualHandleTime;
    }

    public void setActualHandleTime(String actualHandleTime) {
        this.actualHandleTime = actualHandleTime;
    }
}
