package client;

import general.Answer;
import general.BufferSize;
import general.Serializer;
import server.GlobalLogger;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class GetAnswer {
    public static Answer getAnswer(SocketChannel socketChannel) throws IOException, ClassNotFoundException {
        ByteBuffer byteBuffer = ByteBuffer.allocate(BufferSize.BUFFER_SIZE);
        boolean endFlag = false;
        while (!endFlag) {
            int bytesRead;
            do {
                bytesRead = socketChannel.read(byteBuffer);
                if (bytesRead == -1) {
                    endFlag = true;
                    break;
                }
            } while(bytesRead == 0);

            try {
                return (Answer) Serializer.deserialize(byteBuffer);
            } catch (EOFException ignored) {}
        }

        GlobalLogger.logger.warn("соединение внезапно прервалось");

        return null; //Этого не произойдет, но Леша сказал, что может
    }
}
