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
        while (current != null) {
            listHistory.add(current.data);
            current = current.next;
        }
        return listHistory;
    }

    @Override
    public void remove(int id) {
        Node deleteNode = nodeMap.get(id);
        if (deleteNode != null) {
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
            if (head != null) {
                head.prev = null;
            }
        }
        if (next != null) {
            next.prev = prev;
        } else {
            tail = prev;
            if (tail != null) {
                tail.next = null;
            }
        }
    }

    private void linkLast(Node node) {
        if (tail == null) {
            head = node;
        } else {
            tail.next = node;
            node.prev = tail;
        }
        tail = node;
    }
}
