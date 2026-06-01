package com.taskflow.model;

public class PersonalTask extends BaseTask {
    
    public PersonalTask() {}

    @Override
    public String getTaskType() {
        return "Personal"; 
    }
}