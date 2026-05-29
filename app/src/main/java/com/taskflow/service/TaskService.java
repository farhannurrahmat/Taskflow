package com.taskflow.service;

import com.taskflow.model.Task;
import com.taskflow.model.Task.Priority;
import com.taskflow.model.Task.Status;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * TaskService - CRUD operations for tasks
 */
public class TaskService {
    private static TaskService instance;
    private List<Task> tasks = new ArrayList<>();
    private int nextId = 1;

    private TaskService() {
        // Sample data
        Task t1 = new Task(nextId++, "Laporan Praktikum OOP", "Buat laporan pertemuan 3",
                LocalDate.now().plusDays(3), Priority.TINGGI, "Pemrograman OOP");
        t1.setStatus(Status.SEDANG_DIKERJAKAN);
        tasks.add(t1);

        Task t2 = new Task(nextId++, "Quiz Basis Data", "Belajar materi normalisasi",
                LocalDate.now().plusDays(1), Priority.TINGGI, "Basis Data");
        tasks.add(t2);

        Task t3 = new Task(nextId++, "Tugas Essay Pancasila", "Essay 3 halaman",
                LocalDate.now().plusDays(7), Priority.SEDANG, "Pancasila");
        t3.setStatus(Status.SELESAI);
        tasks.add(t3);

        Task t4 = new Task(nextId++, "Presentasi Jaringan", "Slide jaringan komputer",
                LocalDate.now().plusDays(5), Priority.RENDAH, "Jaringan Komputer");
        tasks.add(t4);
    }

    public static TaskService getInstance() {
        if (instance == null) instance = new TaskService();
        return instance;
    }

    public List<Task> getAllTasks() {
        return new ArrayList<>(tasks);
    }

    public void addTask(Task task) {
        task.setId(nextId++);
        tasks.add(task);
    }

    public void updateTask(Task updated) {
        for (int i = 0; i < tasks.size(); i++) {
            if (tasks.get(i).getId() == updated.getId()) {
                tasks.set(i, updated);
                return;
            }
        }
    }

    public void deleteTask(int id) {
        tasks.removeIf(t -> t.getId() == id);
    }

    public void updateStatus(int id, Status status) {
        for (Task t : tasks) {
            if (t.getId() == id) {
                t.setStatus(status);
                return;
            }
        }
    }

    public int getTotalTasks() { return tasks.size(); }

    public int getCompletedTasks() {
        return (int) tasks.stream().filter(Task::isCompleted).count();
    }

    public int getPendingTasks() {
        return getTotalTasks() - getCompletedTasks();
    }

    public double getProgressPercentage() {
        if (tasks.isEmpty()) return 0;
        return (getCompletedTasks() * 100.0) / getTotalTasks();
    }
}
