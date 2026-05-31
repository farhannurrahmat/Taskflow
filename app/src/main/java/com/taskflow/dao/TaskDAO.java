package com.taskflow.dao;

import com.taskflow.config.DatabaseConfig;
import com.taskflow.model.Task;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class TaskDAO {

    private static final DateTimeFormatter DT_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public ObservableList<Task> getAllTasks() {
        return queryTasks("SELECT t.*, u.full_name FROM tasks t LEFT JOIN users u ON t.assigned_to = u.id ORDER BY t.deadline ASC", null);
    }

    public ObservableList<Task> getTasksByUser(int userId) {
        return queryTasks("SELECT t.*, u.full_name FROM tasks t LEFT JOIN users u ON t.assigned_to = u.id WHERE t.assigned_to = ? ORDER BY t.deadline ASC", userId);
    }

    private ObservableList<Task> queryTasks(String sql, Integer userId) {
        ObservableList<Task> tasks = FXCollections.observableArrayList();
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            if (userId != null) ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                tasks.add(mapTask(rs));
            }
        } catch (SQLException e) {
            System.err.println("[TaskDAO] Query error: " + e.getMessage());
        }
        return tasks;
    }

    public boolean addTask(Task task) {
        String sql = "INSERT INTO tasks (title, description, project_name, assigned_to, priority, status, deadline, created_at) VALUES (?,?,?,?,?,?,?,datetime('now','localtime'))";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, task.getTitle());
            ps.setString(2, task.getDescription());
            ps.setString(3, task.getProjectName());
            ps.setInt(4, task.getAssignedTo());
            ps.setString(5, task.getPriority());
            ps.setString(6, task.getStatus());
            ps.setString(7, task.getDeadline().format(DATE_FMT));
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[TaskDAO] addTask error: " + e.getMessage());
            return false;
        }
    }

    public boolean updateTask(Task task) {
        String sql = "UPDATE tasks SET title=?, description=?, project_name=?, assigned_to=?, priority=?, status=?, deadline=?, completed_at=? WHERE id=?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, task.getTitle());
            ps.setString(2, task.getDescription());
            ps.setString(3, task.getProjectName());
            ps.setInt(4, task.getAssignedTo());
            ps.setString(5, task.getPriority());
            ps.setString(6, task.getStatus());
            ps.setString(7, task.getDeadline().format(DATE_FMT));
            if (task.getCompletedAt() != null) {
                ps.setString(8, task.getCompletedAt().format(DT_FMT));
            } else {
                ps.setNull(8, Types.VARCHAR);
            }
            ps.setInt(9, task.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[TaskDAO] updateTask error: " + e.getMessage());
            return false;
        }
    }

    public boolean updateStatus(int taskId, String newStatus) {
        String sql;
        if ("Done".equals(newStatus)) {
            sql = "UPDATE tasks SET status=?, completed_at=datetime('now','localtime') WHERE id=?";
        } else {
            sql = "UPDATE tasks SET status=?, completed_at=NULL WHERE id=?";
        }
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, newStatus);
            ps.setInt(2, taskId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[TaskDAO] updateStatus error: " + e.getMessage());
            return false;
        }
    }

    public boolean deleteTask(int taskId) {
        String sql = "DELETE FROM tasks WHERE id=?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, taskId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[TaskDAO] deleteTask error: " + e.getMessage());
            return false;
        }
    }

    // Statistics queries
    public int countByStatus(String status, Integer userId) {
        String sql = userId == null
            ? "SELECT COUNT(*) FROM tasks WHERE status=?"
            : "SELECT COUNT(*) FROM tasks WHERE status=? AND assigned_to=?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            if (userId != null) ps.setInt(2, userId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            System.err.println("[TaskDAO] countByStatus error: " + e.getMessage());
        }
        return 0;
    }

    public int countTotal(Integer userId) {
        String sql = userId == null
            ? "SELECT COUNT(*) FROM tasks"
            : "SELECT COUNT(*) FROM tasks WHERE assigned_to=?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            if (userId != null) ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            System.err.println("[TaskDAO] countTotal error: " + e.getMessage());
        }
        return 0;
    }

    public int countOverdue(Integer userId) {
        String today = LocalDate.now().format(DATE_FMT);
        String sql = userId == null
            ? "SELECT COUNT(*) FROM tasks WHERE status != 'Done' AND deadline < ?"
            : "SELECT COUNT(*) FROM tasks WHERE status != 'Done' AND deadline < ? AND assigned_to=?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, today);
            if (userId != null) ps.setInt(2, userId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            System.err.println("[TaskDAO] countOverdue error: " + e.getMessage());
        }
        return 0;
    }

    // Workload data per member: { memberName -> { status -> count } }
    public Map<String, Map<String, Integer>> getWorkloadData(String projectFilter) {
        Map<String, Map<String, Integer>> result = new LinkedHashMap<>();
        String sql = projectFilter == null || projectFilter.equals("Semua Proyek")
            ? "SELECT u.full_name, t.status, COUNT(*) as cnt FROM tasks t JOIN users u ON t.assigned_to = u.id WHERE t.status != 'Done' GROUP BY u.full_name, t.status ORDER BY u.full_name"
            : "SELECT u.full_name, t.status, COUNT(*) as cnt FROM tasks t JOIN users u ON t.assigned_to = u.id WHERE t.status != 'Done' AND t.project_name = ? GROUP BY u.full_name, t.status ORDER BY u.full_name";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            if (projectFilter != null && !projectFilter.equals("Semua Proyek")) {
                ps.setString(1, projectFilter);
            }
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String name = rs.getString("full_name");
                String status = rs.getString("status");
                int cnt = rs.getInt("cnt");
                result.computeIfAbsent(name, k -> new LinkedHashMap<>()).put(status, cnt);
            }
        } catch (SQLException e) {
            System.err.println("[TaskDAO] getWorkloadData error: " + e.getMessage());
        }
        return result;
    }

    public List<String> getDistinctProjects() {
        List<String> projects = new ArrayList<>();
        projects.add("Semua Proyek");
        String sql = "SELECT DISTINCT project_name FROM tasks ORDER BY project_name";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                projects.add(rs.getString("project_name"));
            }
        } catch (SQLException e) {
            System.err.println("[TaskDAO] getDistinctProjects error: " + e.getMessage());
        }
        return projects;
    }

    private Task mapTask(ResultSet rs) throws SQLException {
        Task task = new Task();
        task.setId(rs.getInt("id"));
        task.setTitle(rs.getString("title"));
        task.setDescription(rs.getString("description"));
        task.setProjectName(rs.getString("project_name"));
        task.setAssignedTo(rs.getInt("assigned_to"));
        task.setAssignedToName(rs.getString("full_name"));
        task.setPriority(rs.getString("priority"));
        task.setStatus(rs.getString("status"));

        String deadlineStr = rs.getString("deadline");
        if (deadlineStr != null) {
            task.setDeadline(LocalDate.parse(deadlineStr.substring(0, 10), DATE_FMT));
        }

        String completedStr = rs.getString("completed_at");
        if (completedStr != null) {
            try {
                task.setCompletedAt(LocalDateTime.parse(completedStr, DT_FMT));
            } catch (DateTimeParseException e) {
                task.setCompletedAt(LocalDateTime.now());
            }
        }

        String createdStr = rs.getString("created_at");
        if (createdStr != null) {
            try {
                task.setCreatedAt(LocalDateTime.parse(createdStr, DT_FMT));
            } catch (DateTimeParseException e) {
                task.setCreatedAt(LocalDateTime.now());
            }
        }
        return task;
    }
}
