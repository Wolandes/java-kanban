package manager;

import model.*;

import java.io.*;
import java.util.Map;
import java.util.Set;


public class FileBackedTaskManager extends InMemoryTaskManager {

    private File file;

    public FileBackedTaskManager(File file) {
        this.file = file;
    }

    private void save() throws ManagerSaveException {
        try {
            FileWriter fileWriter = new FileWriter(file);
            fileWriter.write("id,type,name,status,description,epic\n");
            for (Task task : getAllTasks()) {
                fileWriter.write(toString(task) + "\n");
            }
            for (Epic epic : getAllEpics()) {
                fileWriter.write(toString(epic) + "\n");
            }
            for (Subtask subtasks : getAllsubtasks()) {
                fileWriter.write(toString(subtasks) + "\n");
            }
            fileWriter.close();
        } catch (IOException exp) {
            throw new ManagerSaveException("Ошибка при сохранении в файла");
        }
    }

    private String toString(Task task) {
        String type = getTaskType(task);
        StringBuilder sb = new StringBuilder();
        sb.append(task.getId()).append(",");
        sb.append(type).append(",");
        sb.append(task.getName()).append(",");
        sb.append(task.getStatus()).append(",");
        sb.append(task.getDescription()).append(",");
        if (type.equals("SUBTASK")) {
            sb.append(((Subtask) task).getParentEpicId());
        }
        return sb.toString();
    }

    private String getTaskType(Task task) {
        if (task instanceof Subtask) {
            return "SUBTASK";
        } else if (task instanceof Epic) {
            return "EPIC";
        } else {
            return "TASK";
        }
    }

    public static FileBackedTaskManager loadFromFile(File file) throws ManagerSaveException {
        FileBackedTaskManager manager = new FileBackedTaskManager(file);
        try {
            FileReader fr = new FileReader(file);
            BufferedReader reader = new BufferedReader(fr);
            String line = reader.readLine();
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                Task task = fromString(data);
                if (task instanceof Epic) {
                    manager.epics.put(task.getId(), (Epic) task);
                } else if (task instanceof Subtask) {
                    manager.subtasks.put(task.getId(), (Subtask) task);
                } else {
                    manager.tasks.put(task.getId(), task);
                }
            }
            int maxId = findMaxId(manager.tasks, manager.epics, manager.subtasks);
            manager.setCounterId(maxId);
            for (Subtask subtask : manager.subtasks.values()) {
                int i = subtask.getParentEpicId();
                Epic epic = manager.getEpicInId(i);
                epic.addSubTaskId(subtask.getId());
            }
            reader.close();
            fr.close();
        } catch (FileNotFoundException exp) {
            throw new ManagerSaveException("Файл не найден");
        } catch (IOException exp) {
            throw new ManagerSaveException("Ошибка при загрузке файла");
        }
        return manager;
    }

    private static Task fromString(String[] value) {
        int id = Integer.parseInt(value[0]);
        TaskType taskType = TaskType.valueOf(value[1]);
        String name = value[2];
        Status status = Status.valueOf(value[3]);
        String description = value[4];

        switch (taskType) {
            case TASK:
                Task task = new Task(name, description, status);
                task.setId(id);
                return task;
            case EPIC:
                Epic epic = new Epic(name, description, status);
                epic.setId(id);
                return epic;
            case SUBTASK:
                int epicId = Integer.parseInt(value[5]);
                Subtask subtask = new Subtask(name, description, status, epicId);
                subtask.setId(id);
                return subtask;
            default:
                throw new IllegalArgumentException("Неизвестный тип задачи");
        }
    }

    @Override
    public void addTask(Task task) {
        super.addTask(task);
        save();
    }

    @Override
    public void addEpic(Epic epic) {
        super.addEpic(epic);
        save();
    }

    @Override
    public void addSubTask(Subtask subtask) {
        super.addSubTask(subtask);
        save();
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void updateSubTask(Subtask subtask) {
        super.updateSubTask(subtask);
        save();
    }

    @Override
    public void removeTaskInId(int id) {
        super.removeTaskInId(id);
        save();
    }

    @Override
    public void removeEpicInId(int id) {
        super.removeEpicInId(id);
        save();
    }

    @Override
    public void removeSubTaskInId(int id) {
        super.removeSubTaskInId(id);
        save();
    }

    @Override
    public void removeAllTasks() {
        super.removeAllTasks();
        save();
    }

    @Override
    public void removeAllEpics() {
        super.removeAllEpics();
        save();
    }

    @Override
    public void removeAllSubTasks() {
        super.removeAllSubTasks();
        save();
    }

    private static int findMaxId(Map<Integer, Task> task, Map<Integer, Epic> epic, Map<Integer, Subtask> subtask) {
        int max = 0;
        Set<Integer> allIdTask = task.keySet();
        Set<Integer> allIdEpic = epic.keySet();
        Set<Integer> allIdSubTasks = subtask.keySet();
        for (Integer i : allIdTask) {
            if (max < i) {
                max = i;
            }
        }
        for (Integer i : allIdEpic) {
            if (max < i) {
                max = i;
            }
        }
        for (Integer i : allIdSubTasks) {
            if (max < i) {
                max = i;
            }
        }
        return max;
    }

    public static void main(String[] args) throws IOException, ManagerSaveException {
        File file = new File("tasks.csv");
        FileBackedTaskManager manager = new FileBackedTaskManager(file);

        Task task1 = new Task("Name1", "Description1", Status.NEW);
        Task task2 = new Task("Name2", "Description2", Status.NEW);
        Epic epicWithSubtasks = new Epic("Name1", "Description1", Status.NEW);
        int idEpic = 3;
        Epic epicWithOutSubtasks = new Epic("Name1", "Description1", Status.NEW);
        Subtask subtask1 = new Subtask("Name1", "Description1", Status.DONE, idEpic);
        Subtask subtask2 = new Subtask("Name2", "Description2", Status.DONE, idEpic);
        Subtask subtask3 = new Subtask("Name2", "Description3", Status.DONE, idEpic);
        manager.addTask(task1);
        manager.addEpic(epicWithSubtasks);
        manager.addEpic(epicWithOutSubtasks);
        manager.addSubTask(subtask1);
        manager.addSubTask(subtask2);
        manager.addSubTask(subtask3);
        manager.addTask(task2);

        FileBackedTaskManager loadManager = FileBackedTaskManager.loadFromFile(file);
        System.out.println(loadManager.getAllTasks());
        System.out.println(loadManager.getAllEpics());
        System.out.println(loadManager.getAllsubtasks());
        System.out.println(loadManager.getCounterId());
    }
}
