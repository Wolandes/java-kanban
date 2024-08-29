import java.util.Objects;

public class SubTask extends Task {
    private int parentEpicId;

    public SubTask(String name, int parentEpicId) {
        super(name);
        this.parentEpicId = parentEpicId;
    }

    public int getParentEpicId() {
        return parentEpicId;
    }

    @Override
    public String toString() {
        return "SubTask{" +
                "name='" + getName() + '\'' +
                ", id=" + getId() +
                ", parentEpicId=" + parentEpicId +
                ", description='" + getDescription() + '\'' +
                ", status=" + getStatus() +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SubTask)) return false;
        if (!super.equals(o)) return false;
        SubTask subTask = (SubTask) o;
        return parentEpicId == subTask.parentEpicId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), parentEpicId);
    }
}
