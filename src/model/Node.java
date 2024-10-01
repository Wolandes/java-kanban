package model;

public class Node {
    public Task data;
    public Node prev;
    public Node next;

    public Node(Task data) {
        this.data = data;
    }
}

