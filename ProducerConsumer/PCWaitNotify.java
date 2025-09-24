package ProducerConsumer;

import java.util.ArrayDeque;
import java.util.Queue;

public class PCWaitNotify {
  private final Queue<Integer> buffer = new ArrayDeque<>();
  private final int capacity;

  public PCWaitNotify(int capacity) {
    this.capacity = capacity;
  }

  public void produce(int value) {
    synchronized (buffer) {
      while (buffer.size() == capacity) {
        try {
          System.out.println("Buffer full, producer is waiting...");
          buffer.wait();
        } catch (InterruptedException e) {
          Thread.currentThread().interrupt();
        }
      }
      buffer.offer(value);
      System.out.println("Produced: " + value + ", Buffer size: " + buffer.size());
      buffer.notifyAll();
    }
  }

  public int consume() {
    synchronized (buffer) {
      while (buffer.isEmpty()) {
        try {
          System.out.println("Buffer empty, consumer is waiting...");
          buffer.wait();
        } catch (InterruptedException e) {
          Thread.currentThread().interrupt();
        }
      }
      int value = buffer.poll();
      System.out.println("Consumed: " + value + ", Buffer size: " + buffer.size());
      buffer.notifyAll();
      return value;
    }
  }

  public static void main(String[] args) {
    PCWaitNotify pc = new PCWaitNotify(5);

    Thread producer = new Thread(() -> {
      for (int i = 0; i < 10; i++) {
        pc.produce(i);
        try {
          Thread.sleep(100); // Simulate time taken to produce
        } catch (InterruptedException e) {
          Thread.currentThread().interrupt();
        }
      }
    });

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

    producer.start();
    consumer.start();

    try {
      producer.join();
      consumer.join();
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }
  }
}
