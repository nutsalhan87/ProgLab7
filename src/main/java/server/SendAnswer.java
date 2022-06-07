package server;

import general.Answer;
import general.Serializer;

import java.io.*;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.concurrent.locks.ReentrantLock;

public class SendAnswer implements Runnable {
    private Answer answer;
    private Socket socket;
    private ReentrantLock lock;
    SendAnswer(Answer answer, Socket socket) {
        this.answer = answer;
        this.socket = socket;
        lock = new ReentrantLock();
    }

    @Override
    public void run() {
        try {
            lock.lock();
            OutputStream outputStream = socket.getOutputStream();
            ByteBuffer byteBuffer = Serializer.serialize(answer);
            outputStream.write(byteBuffer.array(), 0, byteBuffer.limit());
            outputStream.flush();
            lock.unlock();
        } catch (IOException ioException) {
            GlobalLogger.logger.error(ioException.getMessage());
            GlobalLogger.logger.error("Такое может произойти в случае разрыва соединения.");
            lock.unlock();
        }
    }
}
