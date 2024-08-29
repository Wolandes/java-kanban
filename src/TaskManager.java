import java.util.ArrayList;
import java.util.HashMap;

public class TaskManager {
    private HashMap<Integer, Task> tasks = new HashMap<>();
    private HashMap<Integer, Epic> epics = new HashMap<>();
    private HashMap<Integer, Subtask> subtasks = new HashMap<>();
    private int counterId = 0;
    //Генерация ID
    private int increaseId() {
        return ++counterId;
    }
    //Методы Task
    public ArrayList<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }
    public void removeAllTasks() {
        tasks.clear();
    }
    public Task getTaskInId(int id) {
        return tasks.get(id);
    }
    public void addTask(Task task) {
        int id = increaseId();
        task.setId(id);
        tasks.put(id, task);
    }
    public void updateTask(Task task) {
        if (tasks.containsKey(task.getId())) {
            tasks.put(task.getId(), task);
        }
    }
    public void removeTaskInId(int id) {
        tasks.remove(id);
    }
    //Методы Epic
    public ArrayList<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());
    }
    public void removeAllEpics() {
        epics.clear();
        subtasks.clear();
    }
    public Epic getEpicInId(int id) {
        return epics.get(id);
    }
    public void addEpic(Epic epic) {
        int id = increaseId();
        epic.setId(id);
        epics.put(id, epic);
        updateEpicStatus(epic);
    }
    public void updateEpic(Epic epic) {
        if (epics.containsKey(epic.getId())) {
            epics.put(epic.getId(), epic);
            updateEpicStatus(epic);
        }
    }
    public void removeEpicInId(int id) {
        Epic epic = epics.remove(id);
        if (epic != null) {
            for (Integer subTaskId : epic.getSubTaskIds()) {
                subtasks.remove(subTaskId);
            }
        }
    }
    public void updateTaskStatus(int taskId, Status newStatus) {
        Task task = tasks.get(taskId);
        if (task != null) {
            task.setStatus(newStatus);
        }
    }
    //Методы SubTask
    public ArrayList<Subtask> getAllsubtasks() {
        return new ArrayList<>(subtasks.values());
    }
    public void removeAllsubtasks() {
        subtasks.clear();
        for (Epic epic : epics.values()) {
            epic.getSubTaskIds().clear();
            updateEpicStatus(epic);
        }
    }
    public Subtask getSubTaskInId(int id) {
        return subtasks.get(id);
    }
    public void addSubTask(Subtask subTask) {
        int id = increaseId();
        subTask.setId(id);
        subtasks.put(id, subTask);
        Epic epic = epics.get(subTask.getParentEpicId());
        if (epic != null) {
            epic.addSubTaskId(id);
            updateEpicStatus(epic);
        }
    }
    public void updateSubTask(Subtask subTask) {
        if (subtasks.containsKey(subTask.getId())) {
            subtasks.put(subTask.getId(), subTask);
            Epic epic = epics.get(subTask.getParentEpicId());
            if (epic != null) {
                updateEpicStatus(epic);
            }
        }
    }
    public void removeSubTaskInId(int id) {
        Subtask subTask = subtasks.remove(id);
        if (subTask != null) {
            Epic epic = epics.get(subTask.getParentEpicId());
            if (epic != null) {
                epic.removeSubTaskId(id);
                updateEpicStatus(epic);
            }
        }
    }
    public ArrayList<Subtask> getsubtasksInEpic(int epicId) {
        Epic epic = epics.get(epicId);
        ArrayList<Subtask> result = new ArrayList<>();
        if (epic != null) {
            for (Integer subTaskId : epic.getSubTaskIds()) {
                result.add(subtasks.get(subTaskId));
            }
        }
        return result;
    }
    public void updatesubtaskstatus(int subTaskId, Status newStatus) {
        Subtask subTask = subtasks.get(subTaskId);
        if (subTask != null) {
            subTask.setStatus(newStatus);
            Epic epic = epics.get(subTask.getParentEpicId());
            if (epic != null) {
                updateEpicStatus(epic);
            }
        }
    }
    //Метод обновления Epic
    private void updateEpicStatus(Epic epic) {
        ArrayList<Integer> subTaskIds = epic.getSubTaskIds();

        if (subTaskIds.isEmpty()) {
            epic.setStatus(Status.NEW);
            return;
        }

        boolean allNew = true;
        boolean allDone = true;

        for (Integer subTaskId : subTaskIds) {
            Subtask subTask = subtasks.get(subTaskId);
            if (subTask.getStatus() != Status.NEW) {
                allNew = false;
            }
            if (subTask.getStatus() != Status.DONE) {
                allDone = false;
            }
        }

        if (allNew) {
            epic.setStatus(Status.NEW);
        } else if (allDone) {
            epic.setStatus(Status.DONE);
        } else {
            epic.setStatus(Status.IN_PROGRESS);
        }
    }
}
