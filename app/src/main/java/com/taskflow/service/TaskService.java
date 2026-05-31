package com.taskflow.service;

import com.taskflow.dao.TaskDAO;
import com.taskflow.model.Task;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.time.LocalDate;
import java.util.stream.Collectors;

public class TaskService {

    private final TaskDAO taskDAO = new TaskDAO();

    public ObservableList<Task> getTasksForCurrentUser() {
        var user = AuthService.getCurrentUser();
        if (user == null) return FXCollections.observableArrayList();
        if (user.isManager()) {
            return taskDAO.getAllTasks();
        } else {
            return taskDAO.getTasksByUser(user.getId());
        }
    }

    public boolean addTask(Task task) {
        if (task.getTitle() == null || task.getTitle().isBlank()) return false;
        if (task.getDeadline() == null || task.getDeadline().isBefore(LocalDate.now())) return false;
        return taskDAO.addTask(task);
    }

    public boolean updateTask(Task task) {
        if (task.getTitle() == null || task.getTitle().isBlank()) return false;
        if (task.getDeadline() == null) return false;
        return taskDAO.updateTask(task);
    }

    public boolean deleteTask(int taskId) {
        return taskDAO.deleteTask(taskId);
    }

    public boolean updateStatus(int taskId, String newStatus) {
        return taskDAO.updateStatus(taskId, newStatus);
    }

    public int getTotal(Integer userId) { return taskDAO.countTotal(userId); }
    public int getDone(Integer userId) { return taskDAO.countByStatus("Done", userId); }
    public int getInProgress(Integer userId) { return taskDAO.countByStatus("In Progress", userId); }
    public int getToDo(Integer userId) { return taskDAO.countByStatus("To Do", userId); }
    public int getOverdue(Integer userId) { return taskDAO.countOverdue(userId); }

    public TaskDAO getTaskDAO() { return taskDAO; }
}
