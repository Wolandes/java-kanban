package manager;

import model.Task;
import model.Node;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

public class InMemoryHistoryManager implements HistoryManager {
    private Node head;
    private Node tail;
    private final Map<Integer, Node> nodeMap = new HashMap<>();

    public List<Task> getHistory() {
        List<Task> listHistory = new ArrayList<>();
        Node current = head;
        if (current.data != null && current.next == null) {
            listHistory.add(current.data);
        }
        while (current.next != null) {
            listHistory.add(current.data);
            current = current.next;
            if (current.next == null) {
                listHistory.add(current.data);
            }
        }
        return listHistory;
    }

    @Override
    public void remove(int id) {
        if (nodeMap.get(id) == null) {
            return;
        } else {
            Node deleteNode = nodeMap.get(id);
            removeNode(deleteNode);
            nodeMap.remove(id);
        }
    }

    @Override
    public void add(Task task) {
        if (nodeMap.get(task.getId()) != null) {
            removeNode(nodeMap.get(task.getId()));
        }
        Node node = new Node(task);
        linkLast(node);
        nodeMap.put(task.getId(), node);
    }

    private void removeNode(Node node) {
        Node prev = node.prev;
        Node next = node.next;

        if (prev != null) {
            prev.next = next;
        } else {
            head = next;
        }
        if (next != null) {
            next.prev = prev;
        } else {
            tail = prev;
        }
        node.prev = null;
        node.next = null;
    }

    private void linkLast(Node node) {
        if (tail == null) {
            head = node;
            tail = node;
        } else {
            tail.next = node;
            node.prev = tail;
            tail = node;
        }
    }
}
