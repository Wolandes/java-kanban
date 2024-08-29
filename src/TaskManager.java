import java.util.ArrayList;
import java.util.HashMap;

public class TaskManager {
    private HashMap<Integer, Task> tasks = new HashMap<>();
    private HashMap<Integer, Epic> epics = new HashMap<>();
    private HashMap<Integer, SubTask> subTasks = new HashMap<>();
    private int currentId = 0;
    //Генерация ID
    private int generateId() {
        return ++currentId;
    }
    //Методы Task
    public ArrayList<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }
    public void removeAllTasks() {
        tasks.clear();
    }
    public Task getTaskById(int id) {
        return tasks.get(id);
    }
    public void addTask(Task task) {
        int id = generateId();
        task.setId(id);
        tasks.put(id, task);
    }
    public void updateTask(Task task) {
        if (tasks.containsKey(task.getId())) {
            tasks.put(task.getId(), task);
        }
    }
    public void removeTaskById(int id) {
        tasks.remove(id);
    }
    //Методы Epic
    public ArrayList<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());
    }
    public void removeAllEpics() {
        epics.clear();
        subTasks.clear();
    }
    public Epic getEpicById(int id) {
        return epics.get(id);
    }
    public void addEpic(Epic epic) {
        int id = generateId();
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
    public void removeEpicById(int id) {
        Epic epic = epics.remove(id);
        if (epic != null) {
            for (Integer subTaskId : epic.getSubTaskIds()) {
                subTasks.remove(subTaskId);
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
    public ArrayList<SubTask> getAllSubTasks() {
        return new ArrayList<>(subTasks.values());
    }
    public void removeAllSubTasks() {
        subTasks.clear();
        for (Epic epic : epics.values()) {
            epic.getSubTaskIds().clear();
            updateEpicStatus(epic);
        }
    }
    public SubTask getSubTaskById(int id) {
        return subTasks.get(id);
    }
    public void addSubTask(SubTask subTask) {
        int id = generateId();
        subTask.setId(id);
        subTasks.put(id, subTask);
        Epic epic = epics.get(subTask.getParentEpicId());
        if (epic != null) {
            epic.addSubTaskId(id);
            updateEpicStatus(epic);
        }
    }
    public void updateSubTask(SubTask subTask) {
        if (subTasks.containsKey(subTask.getId())) {
            subTasks.put(subTask.getId(), subTask);
            Epic epic = epics.get(subTask.getParentEpicId());
            if (epic != null) {
                updateEpicStatus(epic);
            }
        }
    }
    public void removeSubTaskById(int id) {
        SubTask subTask = subTasks.remove(id);
        if (subTask != null) {
            Epic epic = epics.get(subTask.getParentEpicId());
            if (epic != null) {
                epic.removeSubTaskId(id);
                updateEpicStatus(epic);
            }
        }
    }
    public ArrayList<SubTask> getSubTasksByEpic(int epicId) {
        Epic epic = epics.get(epicId);
        ArrayList<SubTask> result = new ArrayList<>();
        if (epic != null) {
            for (Integer subTaskId : epic.getSubTaskIds()) {
                result.add(subTasks.get(subTaskId));
            }
        }
        return result;
    }
    public void updateSubTaskStatus(int subTaskId, Status newStatus) {
        SubTask subTask = subTasks.get(subTaskId);
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
            SubTask subTask = subTasks.get(subTaskId);
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
