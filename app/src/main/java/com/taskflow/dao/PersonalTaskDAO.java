package com.taskflow.dao;

import com.taskflow.config.DatabaseConfig;
import com.taskflow.model.PersonalTask;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class PersonalTaskDAO {

    public List<PersonalTask> getTasksByUser(int userId) {
        List<PersonalTask> tasks = new ArrayList<>();
        String sql = "SELECT * FROM personal_tasks WHERE user_id = ? ORDER BY deadline ASC";
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

    public boolean addTask(PersonalTask task) {
        String sql = "INSERT INTO personal_tasks (title, description, category, user_id, priority, status, deadline) VALUES (?,?,?,?,?,?,?)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, task.getTitle());
            ps.setString(2, task.getDescription()); // Menyimpan deskripsi
            ps.setString(3, task.getCategory());
            ps.setInt(4, task.getUserId());
            ps.setString(5, task.getPriority());
            ps.setString(6, task.getStatus());
            ps.setString(7, task.getDeadline().toString());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[PersonalTaskDAO] addTask error: " + e.getMessage());
            return false;
        }
    }

    public boolean updateTask(PersonalTask task) {
        String sql = "UPDATE personal_tasks SET title=?, description=?, category=?, priority=?, status=?, deadline=? WHERE id=?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, task.getTitle());
            ps.setString(2, task.getDescription()); // Mengupdate deskripsi
            ps.setString(3, task.getCategory());
            ps.setString(4, task.getPriority());
            ps.setString(5, task.getStatus());
            ps.setString(6, task.getDeadline().toString());
            ps.setInt(7, task.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[PersonalTaskDAO] updateTask error: " + e.getMessage());
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
        
        // PERBAIKAN: Mengambil data deskripsi dari database
        task.setDescription(rs.getString("description")); 
        
        task.setCategory(rs.getString("category"));
        task.setUserId(rs.getInt("user_id"));
        task.setPriority(rs.getString("priority"));
        task.setStatus(rs.getString("status"));
        
        String deadlineStr = rs.getString("deadline");
        if (deadlineStr != null && !deadlineStr.isEmpty()) {
            task.setDeadline(LocalDate.parse(deadlineStr));
        }
        return task;
    }
}