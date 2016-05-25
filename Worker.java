import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.QueueingConsumer;
import com.rabbitmq.client.MessageProperties;
import org.json.JSONObject;

public class Worker {

  private static final String TASK_RECEIVE_QUEUE_NAME = "bakery.stroopwafel.order";
  private static final String TASK_DONE_QUEUE_NAME = "bakery.sweet.done";
  private static final int WORK_LOAD = 4500;

  public static void main(String[] argv) throws Exception {

    ConnectionFactory factory = new ConnectionFactory();
    factory.setHost("rabbitmq.cloud66.local");
    Connection connection = factory.newConnection();
    Channel channel = connection.createChannel();
	channel.queueDeclare(TASK_RECEIVE_QUEUE_NAME, true, false, false, null);
	channel.queueDeclare(TASK_DONE_QUEUE_NAME, true, false, false, null);


	System.out.println("--------------------------------");
	System.out.println("stroopwafel minion v1.0 ready to rock!");
	System.out.println("--------------------------------");
	System.out.println(System.getenv("HOSTNAME") + ":-- [*] Waiting for stroopwafel back orders (backing time = "+ WORK_LOAD/1000 +")");

	channel.basicQos(1);

    QueueingConsumer consumer = new QueueingConsumer(channel);
    channel.basicConsume(TASK_RECEIVE_QUEUE_NAME, false, consumer);

    while (true) {
      QueueingConsumer.Delivery delivery = consumer.nextDelivery();
      String message = new String(delivery.getBody(), "UTF-8");

      // payload = {"flavour":"brown","order":"1"}
      JSONObject json = new JSONObject(message);

    
	  System.out.println(System.getenv("HOSTNAME") + ":-- [.] Start backing a '"+ json.get("flavour") +"' stroopwafel");

      doWork(message);
 	  System.out.println(System.getenv("HOSTNAME") + ":-- [x] Backing a '"+ json.get("flavour") +"' stroopwafel done");
      channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
 
      channel.basicPublish( "", TASK_DONE_QUEUE_NAME,
            MessageProperties.PERSISTENT_TEXT_PLAIN,
            message.getBytes());

    }
  }

  private static void doWork(String task) throws InterruptedException {
     Thread.sleep(WORK_LOAD);
  }
}
