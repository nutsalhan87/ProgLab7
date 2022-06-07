package server;

import general.route.Route;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

public class RouteCollection {
    private List<Route> data;
    private final ReentrantLock lock;

    public RouteCollection() {
        data = new LinkedList<>();
        lock = new ReentrantLock();
    }

    public void updateData(List<Route> newData) {
        lock.lock();
        data.clear();
        data.addAll(newData);
        lock.unlock();
    }

    public List<Route> getData() {
        return new LinkedList<>(data);
    }
}
