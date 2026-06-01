package com.taskflow.model;

public class TeamTask extends BaseTask {
    private String teamName;

    public TeamTask() {}

    public String getTeamName() {
        return teamName;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

    @Override
    public String getTaskType() {
        return "Tim"; 
    }
}