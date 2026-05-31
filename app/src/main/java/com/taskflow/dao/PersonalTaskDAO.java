package com.taskflow.dao;

import com.taskflow.config.DatabaseConfig;
import com.taskflow.model.PersonalTask;
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

public class PersonalTaskDAO {

    private static final DateTimeFormatter DT_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public ObservableList<PersonalTask> getTasksByUser(int userId) {
        ObservableList<PersonalTask> tasks = FXCollections.observableArrayList();
        String sql = """
            SELECT pt.*, u.full_name FROM personal_tasks pt
            LEFT JOIN users u ON pt.user_id = u.id
            WHERE pt.user_id = ?
            ORDER BY pt.deadline ASC
        """;
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                tasks.add(mapTask(rs));
            }
        } catch (SQLException e) {
            System.err.println("[PersonalTaskDAO] getTasksByUser error: " + e.getMessage());
        }
        return tasks;
    }

    public ObservableList<PersonalTask> getAllTasks() {
        ObservableList<PersonalTask> tasks = FXCollections.observableArrayList();
        String sql = """
            SELECT pt.*, u.full_name FROM personal_tasks pt
            LEFT JOIN users u ON pt.user_id = u.id
            ORDER BY pt.deadline ASC
        """;
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                tasks.add(mapTask(rs));
            }
        } catch (SQLException e) {
            System.err.println("[PersonalTaskDAO] getAllTasks error: " + e.getMessage());
        }
        return tasks;
    }

    public boolean addTask(PersonalTask task) {
        String sql = "INSERT INTO personal_tasks (title, description, category, user_id, priority, status, deadline, created_at) VALUES (?,?,?,?,?,?,?,datetime('now','localtime'))";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, task.getTitle());
            ps.setString(2, task.getDescription());
            ps.setString(3, task.getCategory());
            ps.setInt(4, task.getUserId());
            ps.setString(5, task.getPriority());
            ps.setString(6, task.getStatus());
            ps.setString(7, task.getDeadline().format(DATE_FMT));
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[PersonalTaskDAO] addTask error: " + e.getMessage());
            return false;
        }
    }

    public boolean updateTask(PersonalTask task) {
        String sql = "UPDATE personal_tasks SET title=?, description=?, category=?, priority=?, status=?, deadline=?, completed_at=? WHERE id=?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, task.getTitle());
            ps.setString(2, task.getDescription());
            ps.setString(3, task.getCategory());
            ps.setString(4, task.getPriority());
            ps.setString(5, task.getStatus());
            ps.setString(6, task.getDeadline().format(DATE_FMT));
            if (task.getCompletedAt() != null) {
                ps.setString(7, task.getCompletedAt().format(DT_FMT));
            } else {
                ps.setNull(7, Types.VARCHAR);
            }
            ps.setInt(8, task.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[PersonalTaskDAO] updateTask error: " + e.getMessage());
            return false;
        }
    }

    public boolean updateStatus(int taskId, String newStatus) {
        String sql = "Done".equals(newStatus)
            ? "UPDATE personal_tasks SET status=?, completed_at=datetime('now','localtime') WHERE id=?"
            : "UPDATE personal_tasks SET status=?, completed_at=NULL WHERE id=?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, newStatus);
            ps.setInt(2, taskId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[PersonalTaskDAO] updateStatus error: " + e.getMessage());
            return false;
        }
    }

    public boolean deleteTask(int taskId) {
        String sql = "DELETE FROM personal_tasks WHERE id=?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, taskId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[PersonalTaskDAO] deleteTask error: " + e.getMessage());
            return false;
        }
    }

    /** Ambil tugas yang deadline-nya dalam N hari ke depan dan belum selesai */
    public List<PersonalTask> getDueSoonTasks(int userId, int daysAhead) {
        List<PersonalTask> tasks = new ArrayList<>();
        String today = LocalDate.now().format(DATE_FMT);
        String limit = LocalDate.now().plusDays(daysAhead).format(DATE_FMT);
        String sql = """
            SELECT pt.*, u.full_name FROM personal_tasks pt
            LEFT JOIN users u ON pt.user_id = u.id
            WHERE pt.user_id = ? AND pt.status != 'Done'
              AND pt.deadline >= ? AND pt.deadline <= ?
            ORDER BY pt.deadline ASC
        """;
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setString(2, today);
            ps.setString(3, limit);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) tasks.add(mapTask(rs));
        } catch (SQLException e) {
            System.err.println("[PersonalTaskDAO] getDueSoonTasks error: " + e.getMessage());
        }
        return tasks;
    }

    public int countByStatus(String status, int userId) {
        String sql = "SELECT COUNT(*) FROM personal_tasks WHERE status=? AND user_id=?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, userId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            System.err.println("[PersonalTaskDAO] countByStatus error: " + e.getMessage());
        }
        return 0;
    }

    public int countOverdue(int userId) {
        String today = LocalDate.now().format(DATE_FMT);
        String sql = "SELECT COUNT(*) FROM personal_tasks WHERE status != 'Done' AND deadline < ? AND user_id=?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, today);
            ps.setInt(2, userId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            System.err.println("[PersonalTaskDAO] countOverdue error: " + e.getMessage());
        }
        return 0;
    }

    public Map<String, Map<String, Integer>> getCategoryData(int userId) {
        Map<String, Map<String, Integer>> result = new LinkedHashMap<>();
        String sql = "SELECT category, status, COUNT(*) as cnt FROM personal_tasks WHERE user_id=? GROUP BY category, status ORDER BY category";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String category = rs.getString("category");
                String status = rs.getString("status");
                int cnt = rs.getInt("cnt");
                result.computeIfAbsent(category, k -> new LinkedHashMap<>()).put(status, cnt);
            }
        } catch (SQLException e) {
            System.err.println("[PersonalTaskDAO] getCategoryData error: " + e.getMessage());
        }
        return result;
    }

    private PersonalTask mapTask(ResultSet rs) throws SQLException {
        PersonalTask task = new PersonalTask();
        task.setId(rs.getInt("id"));
        task.setTitle(rs.getString("title"));
        task.setDescription(rs.getString("description"));
        task.setCategory(rs.getString("category"));
        task.setUserId(rs.getInt("user_id"));
        task.setUserName(rs.getString("full_name"));
        task.setPriority(rs.getString("priority"));
        task.setStatus(rs.getString("status"));
        // Set projectName same as category for BaseTask compatibility
        task.setProjectName(rs.getString("category"));

        String deadlineStr = rs.getString("deadline");
        if (deadlineStr != null) {
            task.setDeadline(LocalDate.parse(deadlineStr.substring(0, 10), DATE_FMT));
        }
        String completedStr = rs.getString("completed_at");
        if (completedStr != null) {
            try { task.setCompletedAt(LocalDateTime.parse(completedStr, DT_FMT)); }
            catch (DateTimeParseException e) { task.setCompletedAt(LocalDateTime.now()); }
        }
        String createdStr = rs.getString("created_at");
        if (createdStr != null) {
            try { task.setCreatedAt(LocalDateTime.parse(createdStr, DT_FMT)); }
            catch (DateTimeParseException e) { task.setCreatedAt(LocalDateTime.now()); }
        }
        return task;
    }
}
