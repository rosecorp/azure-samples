import com.microsoft.azure.servicebus.*;
import com.microsoft.azure.servicebus.primitives.ConnectionStringBuilder;
import com.microsoft.azure.servicebus.primitives.ServiceBusException;

import java.time.Duration;
import java.util.concurrent.CompletableFuture;

public class AzureBusQueue {

    public static final String storageConnectionString =
            "";

    public static void main(String[] args) throws Exception {

        run(storageConnectionString);
    }

    public static void run(String connectionString) throws Exception {
        QueueClient sendClient = new QueueClient(new ConnectionStringBuilder(connectionString, "test"), ReceiveMode.PEEKLOCK);
        sendMessages(sendClient);
        sendClient.close();

        QueueClient receiveClient = new QueueClient(new ConnectionStringBuilder(connectionString, "test"), ReceiveMode.PEEKLOCK);
        registerReceiver(receiveClient);
        waitForEnter(5);
        receiveClient.close();

        System.exit(0);
        }

    static void sendMessages(QueueClient sendClient) throws ServiceBusException, InterruptedException {
        Message message = new Message("body");
        message.setLabel("Scientist");
        sendClient.send(message);
    }

    static void registerReceiver(QueueClient queueClient) throws Exception {
        // register the RegisterMessageHandler callback
        queueClient.registerMessageHandler(new IMessageHandler() {
            @Override
            public CompletableFuture<Void> onMessageAsync(IMessage message) {
                System.out.println("out: " + new String(message.getBody()));
                return CompletableFuture.completedFuture(null);
            }

            @Override
            public void notifyException(Throwable exception, ExceptionPhase phase) {
                System.out.println("some problem");
            }
        }, new MessageHandlerOptions(1, true, Duration.ofMinutes(1)));
    }

    private static void waitForEnter(int seconds) {
        try {
            Thread.sleep(seconds * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
}
