import com.microsoft.azure.servicebus.*;
import com.microsoft.azure.servicebus.primitives.ConnectionStringBuilder;
import com.microsoft.azure.servicebus.primitives.ServiceBusException;

import java.time.Duration;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class AzureServiceBusTopic {

    public static final String connectionString =
            "";


    public static void main(String[] args) throws Exception {
        azureTopic();
    }


    public static void azureTopic() throws Exception {
        TopicClient sendClient;
        SubscriptionClient subscription1Client;

        //&lt;topicName&gt;/subscriptions/&lt;subscriptionName&gt;
        subscription1Client = new SubscriptionClient(new ConnectionStringBuilder(connectionString, "topic_test/subscriptions/subscription_test"), ReceiveMode.PEEKLOCK);

        registerMessageHandlerOnClient(subscription1Client);

        sendClient = new TopicClient(new ConnectionStringBuilder(connectionString, "topic_test"));

        sendMessages(sendClient);

        waitForEnter(5);
        sendClient.close();
        System.exit(0);

    }


    static void sendMessages(TopicClient sendClient) throws ServiceBusException, InterruptedException {

        Message message = new Message("message for topic");
        message.setMessageId(UUID.randomUUID().toString());
        message.setLabel("Scientist");
        message.setTimeToLive(Duration.ofMinutes(2));

        sendClient.send(message);
    }

    static void registerMessageHandlerOnClient(SubscriptionClient receiveClient) throws Exception {

        receiveClient.registerMessageHandler(new IMessageHandler() {
            @Override
            public CompletableFuture<Void> onMessageAsync(IMessage message) {

                byte[] body = message.getBody();
                String bodySting = new String(body);
                System.out.println(bodySting);

                return receiveClient.completeAsync(message.getLockToken());
            }

            @Override
            public void notifyException(Throwable throwable, ExceptionPhase exceptionPhase) {
                System.out.printf(exceptionPhase + "-" + throwable.getMessage());
            }
        }, new MessageHandlerOptions(1, false, Duration.ofMinutes(1)));
    }

    private static void waitForEnter(int seconds) {
        try {
            Thread.sleep(seconds * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

}
