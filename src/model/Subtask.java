package model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

public class Subtask extends Task {
    private int parentEpicId;

    public Subtask(String name, String description, Status status, int parentEpicId) {
        super(name, description, status, Duration.ZERO, null);
        this.parentEpicId = parentEpicId;
    }

    public Subtask(String name, String description, Status status, int parentEpicId, Duration duration, LocalDateTime startTime) {
        super(name, description, status, duration, startTime);
        this.parentEpicId = parentEpicId;
    }

    public int getParentEpicId() {
        return parentEpicId;
    }

    @Override
    public String toString() {
        return "SubTask{" + "name='" + getName() + '\'' + ", id=" + getId() + ", parentEpicId=" + parentEpicId + ", description='" + getDescription() + '\'' + ", status=" + getStatus() + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Subtask)) return false;
        if (!super.equals(o)) return false;
        Subtask subTask = (Subtask) o;
        return parentEpicId == subTask.parentEpicId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), parentEpicId);
    }
}
