package manager;

import model.Task;
import model.Epic;
import model.Subtask;
import model.Status;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryTaskManager implements TaskManager {
    private Map<Integer, Task> tasks = new HashMap<>();
    private Map<Integer, Epic> epics = new HashMap<>();
    private Map<Integer, Subtask> subtasks = new HashMap<>();
    private int counterId = 0;
    HistoryManager historyManager;

    public InMemoryTaskManager() {
        this.historyManager = Managers.getDefaultHistory();
    }

    //Генерация ID
    private int increaseId() {
        return ++counterId;
    }

    @Override
    //Методы Task
    public ArrayList<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public void removeAllTasks() {
        List<Task> listHistory = getHistory();
        List<Task> allTasks = getAllTasks();
        for (Task history : listHistory) {
            for (Task task : allTasks) {
                if (history.getId() == task.getId()) {
                    historyManager.remove(task.getId());
                }
            }
        }
        tasks.clear();
    }

    @Override
    public Task getTaskInId(int id) {
        Task task = tasks.get(id);
        if (task != null) {
            historyManager.add(task);
        }
        return tasks.get(id);
    }

    @Override
    public void addTask(Task task) {
        int id = increaseId();
        task.setId(id);
        tasks.put(id, task);
    }

    @Override
    public void updateTask(Task task) {
        if (tasks.containsKey(task.getId())) {
            tasks.put(task.getId(), task);
        }
    }

    @Override
    public void removeTaskInId(int id) {
        List<Task> listHistory = getHistory();
        List<Task> listTasks = getAllTasks();
        for (Task taskHistory : listHistory) {
            if (taskHistory.getId() == id) {
                for (Task listTask : listTasks) {
                    if (listTask.getId() == id) {
                        historyManager.remove(taskHistory.getId());
                        tasks.remove(id);
                        return;
                    }
                }
            }
        }
        tasks.remove(id);
    }

    //Методы Epic
    @Override
    public ArrayList<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public void removeAllEpics() {
        List<Task> listHistory = getHistory();
        List<Epic> allEpics = getAllEpics();
        for (Task taskHistosy : listHistory) {
            for (Epic epic : allEpics) {
                if (taskHistosy.getId() == epic.getId()) {
                    Epic epicHistory = (Epic) taskHistosy;
                    if (epicHistory.getSubTaskIds() != null) {
                        ArrayList<Integer> subTasksId = epicHistory.getSubTaskIds();
                        for (int subTaskIid : subTasksId) {
                            historyManager.remove(subTaskIid);
                        }
                    }
                    historyManager.remove(epicHistory.getId());
                }
            }
        }

        epics.clear();
        subtasks.clear();
    }

    @Override
    public Epic getEpicInId(int id) {
        Epic epic = epics.get(id);
        if (epic != null) {
            historyManager.add(epic);
        }
        return epics.get(id);

    }

    @Override
    public void addEpic(Epic epic) {
        int id = increaseId();
        epic.setId(id);
        epics.put(id, epic);
        updateEpicStatus(epic);
    }

    @Override
    public void updateEpic(Epic epic) {
        Epic epic1 = epics.get(epic.getId());
        if (epic1 != null) {
            String name = epic.getName();
            String description = epic.getDescription();
            epic1.setName(name);
            epic1.setDescription(description);
            updateEpicStatus(epic);
        }
    }

    @Override
    public void removeEpicInId(int id) {
        //newForTask
        List<Task> tasks = getHistory();
        for (Task task : tasks) {
            if (task.getId() == id) {
                Epic epic = (Epic) task;
                if (epic.getSubTaskIds() != null) {
                    ArrayList<Integer> subTasksId = epic.getSubTaskIds();
                    for (int subTaskIid : subTasksId) {
                        historyManager.remove(subTaskIid);
                    }
                }
                historyManager.remove(task.getId());
            }
        }

        Epic epic = epics.remove(id);
        if (epic != null) {
            for (Integer subTaskId : epic.getSubTaskIds()) {
                subtasks.remove(subTaskId);
            }
        }
    }

    //Методы SubTask
    @Override
    public ArrayList<Subtask> getAllsubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public void removeAllSubTasks() {
        List<Task> listHistory = getHistory();
        List<Subtask> allSubtask = getAllsubtasks();
        for (Task taskHistory : listHistory) {
            for (Subtask subtask : allSubtask) {
                if (taskHistory.getId() == subtask.getId()) {
                    historyManager.remove(taskHistory.getId());
                }
            }
        }
        subtasks.clear();
        for (Epic epic : epics.values()) {
            epic.clearSubTaskIds();
            updateEpicStatus(epic);
        }
    }

    @Override
    public Subtask getSubTaskInId(int id) {
        Subtask subtask = subtasks.get(id);
        if (subtask != null) {
            historyManager.add(subtask);
        }
        return subtasks.get(id);
    }

    @Override
    public void addSubTask(Subtask subtask) {
        Epic epic = epics.get(subtask.getParentEpicId());
        if (epic == null) {
            return;
        }
        int id = increaseId();
        subtask.setId(id);
        subtasks.put(id, subtask);
        epic.addSubTaskId(id);
        updateEpicStatus(epic);
    }

    @Override
    public void updateSubTask(Subtask subtask) {
        Subtask subtaskChek = subtasks.get(subtask.getId());
        if (subtaskChek == null) {
            return;
        }
        int parentEpicId = subtaskChek.getParentEpicId();
        int parentEpicIdGetAtr = subtask.getParentEpicId();

        if (parentEpicId != parentEpicIdGetAtr) {
            return;
        }

        subtasks.put(subtask.getId(), subtask);
        Epic epic = epics.get(subtask.getParentEpicId());
        if (epic != null) {
            updateEpicStatus(epic);
        }
    }

    @Override
    public void removeSubTaskInId(int id) {
        List<Task> listHistory = getHistory();
        List<Subtask> allSubtask = getAllsubtasks();
        for (Task taskHistosy : listHistory) {
            for (Subtask subtask : allSubtask) {
                if (taskHistosy.getId() == subtask.getId()) {
                    historyManager.remove(taskHistosy.getId());
                    Subtask subTask = subtasks.remove(id);
                    if (subTask != null) {
                        Epic epic = epics.get(subTask.getParentEpicId());
                        if (epic != null) {
                            epic.removeSubTaskId(id);
                            updateEpicStatus(epic);
                        }
                    }
                    return;
                }
            }
        }
        Subtask subTask = subtasks.remove(id);
        if (subTask != null) {
            Epic epic = epics.get(subTask.getParentEpicId());
            if (epic != null) {
                epic.removeSubTaskId(id);
                updateEpicStatus(epic);
            }
        }
    }

    @Override
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

    //Новый метод getHistory
    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }
}
