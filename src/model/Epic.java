package model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Epic extends Task {
    private ArrayList<Integer> subTaskIds = new ArrayList<>();
    private Duration duration = Duration.ZERO;
    private LocalDateTime startTime = null;

    public Epic(String name, String description, Status status) {
        super(name, description, status, Duration.ZERO, null);
    }

    public Epic(String name, String description, Status status, Duration duration, LocalDateTime startTime) {
        super(name, description, status, duration, startTime);
    }

    public void addSubTaskId(int subTaskId) {
        if (subTaskId != this.getId()) {
            if (!subTaskIds.contains(subTaskId)) {
                subTaskIds.add(subTaskId);
            }
        }
    }

    public void updateEpicDurationAndTime(List<Subtask> subtasks) {
        duration = Duration.ZERO;
        startTime = null;
        for (Subtask subtask : subtasks) {
            duration = duration.plus(subtask.getDuration());
            if (startTime == null || (subtask.getStartTime() != null && subtask.getStartTime().isBefore(startTime))) {
                startTime = subtask.getStartTime();
            }
        }
    }

    public void removeSubTaskId(int subTaskId) {
        subTaskIds.remove((Integer) subTaskId);
    }

    public ArrayList<Integer> getSubTaskIds() {
        return new ArrayList<>(subTaskIds);
    }

    public void clearSubTaskIds() {
        subTaskIds.clear();
    }

    @Override
    public String toString() {
        return "Epic{" + "name='" + getName() + '\'' + ", id=" + getId() + ", subTaskIds=" + subTaskIds + ", description='" + getDescription() + '\'' + ", status=" + getStatus() + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Epic epic = (Epic) o;
        return Objects.equals(getId(), epic.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), subTaskIds);
    }
}
