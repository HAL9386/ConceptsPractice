package ProducerConsumer;

import java.util.ArrayDeque;
import java.util.Queue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class PCLockCond {
  private final Queue<Integer> buffer = new ArrayDeque<>();
  private final int capacity;
  private final Lock lock = new ReentrantLock();
  private final Condition notFull = lock.newCondition();
  private final Condition notEmpty = lock.newCondition();

  public PCLockCond(int capacity) {
    this.capacity = capacity;
  }

  public void produce(int value) {
    lock.lock();
    try {
      while (buffer.size() == capacity) {
        System.out.println("Buffer full, producer is waiting...");
        notFull.await();
      }
      buffer.offer(value);
      System.out.println("Produced: " + value + ", Buffer size: " + buffer.size());
      notEmpty.signal();
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    } finally {
      lock.unlock();
    }
  }

  public int consume() {
    lock.lock();
    try {
      while (buffer.isEmpty()) {
        System.out.println("Buffer empty, consumer is waiting...");
        notEmpty.await();
      }
      int value = buffer.poll();
      System.out.println("Consumed: " + value + ", Buffer size: " + buffer.size());
      notFull.signal();
      return value;
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      return -1;
    } finally {
      lock.unlock();
    }
  }

  public static void main(String[] args) {
    PCLockCond pc = new PCLockCond(5);

    // producer thread
    Thread producer = new Thread(() -> {
      for (int i = 0; i < 10; i++) {
        pc.produce(i);
        try {
          Thread.sleep(10); // Simulate time taken to produce
        } catch (InterruptedException e) {
          Thread.currentThread().interrupt();
        }
      }
    });

    // consumer thread
    Thread consumer = new Thread(() -> {
      for (int i = 0; i < 10; i++) {
        int value = pc.consume();
        if (value == -1) {
          break;
        }
        try {
          Thread.sleep(2000); // Simulate time taken to consume
        } catch (InterruptedException e) {
          Thread.currentThread().interrupt();
        }
      }
    });

    // start threads
    producer.start();
    consumer.start();

    // wait for threads to finish
    try {
      producer.join();
      consumer.join();
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }
  }
}
