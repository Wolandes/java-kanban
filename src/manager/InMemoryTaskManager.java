package manager;

import model.Task;
import model.Epic;
import model.Subtask;
import model.Status;

import java.time.LocalDateTime;
import java.util.*;

public class InMemoryTaskManager implements TaskManager {
    protected Map<Integer, Task> tasks = new HashMap<>();
    protected Map<Integer, Epic> epics = new HashMap<>();
    protected Map<Integer, Subtask> subtasks = new HashMap<>();
    private final TreeSet<Task> prioritizedTasks = new TreeSet<>(Comparator.comparing(Task::getStartTime, Comparator.nullsLast(Comparator.naturalOrder())));
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
        Set<Integer> idAllTasks = tasks.keySet();
        for (Integer id : idAllTasks) {
            historyManager.remove(id);
        }
        tasks.clear();
        prioritizedTasks.removeIf(task -> task instanceof Task);
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
        if (task.getStartTime() != null && isOverlapping(task)) {
            throw new IllegalArgumentException("Пересекающееся время.");
        }
        int id = increaseId();
        task.setId(id);
        tasks.put(id, task);
        if (task.getStartTime() != null) {
            prioritizedTasks.add(task);
        }
    }

    @Override
    public void updateTask(Task task) {
        if (task.getStartTime() != null && isOverlapping(task)) {
            throw new IllegalArgumentException("Пересекающееся время.");
        }
        Task existingTask = tasks.get(task.getId());
        if (existingTask != null) {
            prioritizedTasks.remove(existingTask);
            tasks.put(task.getId(), task);
            if (task.getStartTime() != null) {
                prioritizedTasks.add(task);
            }
        }
    }

    @Override
    public void removeTaskInId(int id) {
        Task taskDelete = tasks.remove(id);
        if (taskDelete != null) {
            historyManager.remove(id);
            prioritizedTasks.remove(taskDelete);
        }
    }

    //Методы Epic
    @Override
    public ArrayList<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public void removeAllEpics() {
        Set<Integer> idAllEpics = epics.keySet();
        Set<Integer> idAllSubtasks = subtasks.keySet();
        for (Integer id : idAllEpics) {
            historyManager.remove(id);
        }
        for (Integer id : idAllSubtasks) {
            historyManager.remove(id);
        }
        prioritizedTasks.removeIf(task -> task instanceof Epic);
        prioritizedTasks.removeIf(task -> task instanceof Subtask);
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
        Epic epic = epics.remove(id);
        if (epic != null) {
            historyManager.remove(id);
            for (Integer subTaskId : epic.getSubTaskIds()) {
                historyManager.remove(subTaskId);
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
        Set<Integer> idAllSubtasks = subtasks.keySet();
        for (Integer id : idAllSubtasks) {
            historyManager.remove(id);
        }
        subtasks.clear();
        prioritizedTasks.removeIf(task -> task instanceof Subtask);
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
        if (subtask.getStartTime() != null && isOverlapping(subtask)) {
            throw new IllegalArgumentException("Пересекающееся время задачи");
        }
        int id = increaseId();
        subtask.setId(id);
        subtasks.put(id, subtask);
        epic.addSubTaskId(id);
        if (subtask.getStartTime() != null) {
            prioritizedTasks.add(subtask);
        }
        updateEpicStatus(epic);
    }

    @Override
    public void updateSubTask(Subtask subtask) {
        if (subtask.getStartTime() != null && isOverlapping(subtask)) {
            throw new IllegalArgumentException("Пересекающееся время задачи");
        }
        Subtask existingSubtask = subtasks.get(subtask.getId());
        if (existingSubtask != null) {
            prioritizedTasks.remove(existingSubtask);
            subtasks.put(subtask.getId(), subtask);
            Epic epic = epics.get(subtask.getParentEpicId());
            if (epic != null) {
                updateEpicStatus(epic);
            }
            if (subtask.getStartTime() != null) {
                prioritizedTasks.add(subtask);
            }
        }
    }

    @Override
    public void removeSubTaskInId(int id) {
        Subtask subTask = subtasks.remove(id);
        if (subTask != null) {
            historyManager.remove(id);
            Epic epic = epics.get(subTask.getParentEpicId());
            if (epic != null) {
                epic.removeSubTaskId(id);
                updateEpicStatus(epic);
            }
            prioritizedTasks.remove(subTask);
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

    //Определить counterId
    protected void setCounterId(int newId) {
        counterId = newId;
    }

    //Для проверки записи counterId
    protected int getCounterId() {
        return counterId;
    }

    public List<Task> getPrioritizedTasks() {
        return new ArrayList<>(prioritizedTasks);
    }

    private boolean isOverlapping(Task task) {
        return prioritizedTasks.stream()
                .filter(existingTask -> tasksOverlap(task, existingTask))
                .map(existingTask -> true)
                .findAny()
                .orElse(false);
    }

    private boolean tasksOverlap(Task task1, Task task2) {
        if (task1.getStartTime() == null || task2.getStartTime() == null) {
            return false;
        }
        LocalDateTime start1 = task1.getStartTime();
        LocalDateTime end1 = start1.plus(task1.getDuration());
        LocalDateTime start2 = task2.getStartTime();
        LocalDateTime end2 = start2.plus(task2.getDuration());

        return start1.isBefore(end2) && start2.isBefore(end1);
    }
}
