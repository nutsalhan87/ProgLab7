package server;

import general.Request;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.concurrent.Callable;
import java.util.concurrent.locks.ReentrantLock;

public class GetRequest implements Callable<Request> {
    private Socket socket;
    private ReentrantLock lock;
    public GetRequest(Socket socket) {
        this.socket = socket;
        lock = new ReentrantLock();
    }

    @Override
    public Request call() throws IOException, ClassNotFoundException {
        lock.lock();
        ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
        Request request = (Request) objectInputStream.readObject();
        lock.unlock();
        return request;
    }
}
