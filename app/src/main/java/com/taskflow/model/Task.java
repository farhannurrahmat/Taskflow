package com.taskflow.model;

import java.time.LocalDate;

/**
 * Task class extends BaseTask (Inheritance + Polymorphism)
 */
public class Task extends BaseTask {

    public enum Status {
        BELUM_DIKERJAKAN("Belum Dikerjakan"),
        SEDANG_DIKERJAKAN("Sedang Dikerjakan"),
        SELESAI("Selesai");

        private final String label;
        Status(String label) { this.label = label; }
        public String getLabel() { return label; }
    }

    public enum Priority {
        RENDAH("Rendah"),
        SEDANG("Sedang"),
        TINGGI("Tinggi");

        private final String label;
        Priority(String label) { this.label = label; }
        public String getLabel() { return label; }
    }

    private Status status;
    private Priority priority;
    private String mataKuliah;

    public Task(int id, String title, String description, LocalDate deadline,
                Priority priority, String mataKuliah) {
        super(id, title, description, deadline);
        this.status = Status.BELUM_DIKERJAKAN;
        this.priority = priority;
        this.mataKuliah = mataKuliah;
    }

    @Override
    public String getStatusLabel() {
        return status.getLabel();
    }

    @Override
    public boolean isCompleted() {
        return status == Status.SELESAI;
    }

    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }

    public Priority getPriority() { return priority; }
    public void setPriority(Priority priority) { this.priority = priority; }

    public String getMataKuliah() { return mataKuliah; }
    public void setMataKuliah(String mataKuliah) { this.mataKuliah = mataKuliah; }
}
