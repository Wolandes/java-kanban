package manager;
import model.Task;

import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    private ArrayList<Task> historyList = new ArrayList<>();

    public List<Task> getHistory(){
        return new ArrayList<>(historyList);
    }
    public void add(Task task){
        historyList.add(task);
        if (historyList.size() > 10){
            historyList.remove(0);
        }
    }
}
