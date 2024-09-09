import java.util.ArrayList;

public class InMemoryHistoryManager implements HistoryManager {
    private ArrayList<Task> historyList = new ArrayList<>();
    private int lastId = 0;

    public ArrayList<Task> getHistory(){
        return historyList;
    }
    public void add(Task task){
        if (historyList.size() <= 9 ){
            historyList.add(task);
        } else {
            historyList.set(lastId,task);
            lastId++;
        }
        if (lastId > 9){
            lastId = 0;
        }
    }
}
