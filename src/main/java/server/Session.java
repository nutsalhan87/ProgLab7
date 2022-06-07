package server;

import general.*;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.*;

import static server.GlobalLogger.logger;

public class Session implements Runnable {
    private final Socket socket;
    private RouteCollection dataCollection;
    public Session(Socket socket, RouteCollection dataCollection) {
        this.dataCollection = dataCollection;
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            logger.info("Клиент подключился к серверу");

            ForkJoinPool requestReader = new ForkJoinPool();
            ForkJoinTask<Request> request;
            while (true) {
                request = requestReader.submit(new GetRequest(socket));
                if (request.get().getCommand().equals(CommandList.NEW_USER)) {
                    if (Account.newUser(socket, request.get())) {
                        break;
                    }
                } else {
                    if (Account.logIn(socket, request.get())) {
                        break;
                    }
                }
            }

            ForkJoinPool requestResponser = new ForkJoinPool();
            ForkJoinTask<Answer> answer;
            ExecutorService fixedThreadPool = Executors.newFixedThreadPool(5);
            while (!Thread.interrupted()) {
                request = requestReader.submit(new GetRequest(socket));
                logger.info("Запрос от клиента получен");
                answer = requestResponser.submit(new ResponseToRequest(request.get().getCommand().getExecutableCommand(),
                        request.get().getArguments(), dataCollection, request.get().getUser()));
                fixedThreadPool.submit(new SendAnswer(answer.get(), socket));
                logger.info("Отправлен ответ клиенту");
            }
        } catch (SocketException exs) {
            logger.warn("Соединение с клиентом потеряно");
        } catch (ExecutionException | InterruptedException ignored) {
        } catch (IOException ignored) {
            logger.warn("Мы не должны были придти сюда.");
        } finally {
            try {
                if (!socket.isClosed())
                    socket.close();
            } catch (IOException ignored) {}
        }
    }
}
