package ProducerConsumer;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class PCBlockingQueue {
  private final BlockingQueue<Integer> buffer;

  public PCBlockingQueue(int capacity) {
    this.buffer = new ArrayBlockingQueue<>(capacity);
  }

  public void produce(int value) {
    try {
      buffer.put(value);
      System.out.println("Produced: " + value + ", Buffer size: " + buffer.size());
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }
  }
  
  public int consume() {
    try {
      int value = buffer.take();
      System.out.println("Consumed: " + value + ", Buffer size: " + buffer.size());
      return value;
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      return -1; // Indicate failure
    }
  }

  public static void main(String[] args) {
    PCBlockingQueue pc = new PCBlockingQueue(5);

    // producer thread
    Thread producer = new Thread(() -> {
      for (int i = 0; i < 10; i++) {
        pc.produce(i);
        try {
          Thread.sleep(1000); // Simulate time taken to produce
        } catch (InterruptedException e) {
          Thread.currentThread().interrupt();
        }
      }
    });
    // consumer thread
    Thread consumer = new Thread(() -> {
      for (int i = 0; i < 10; i++) {
        pc.consume();
        try {
          Thread.sleep(20); // Simulate time taken to consume
        } catch (InterruptedException e) {
          Thread.currentThread().interrupt();
        }
      }
    });
    // start threads
    producer.start();
    consumer.start();
    // wait for threads to finish
    // try {
    //   producer.join();
    //   consumer.join();
    // } catch (InterruptedException e) {
    //   Thread.currentThread().interrupt();
    // }
  }
}
