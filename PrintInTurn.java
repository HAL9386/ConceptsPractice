import java.util.concurrent.atomic.AtomicInteger;

// https://www.bilibili.com/video/BV1jG3LzSEsy/
public class PrintInTurn {
  static AtomicInteger counter = new AtomicInteger(0);
  static volatile int runToken = 0;
  private static final Object lock = new Object();
  private static final int TOTAL = 10000;

  public static void main(String[] args) throws InterruptedException {
    Thread threadA = new Thread(() -> {
      while (counter.get() <= TOTAL) {
        if (runToken == 0) {
          counter.getAndIncrement();
          runToken = 1;
        }
      }
    });
    Thread threadB = new Thread(() -> {
      while (counter.get() <= TOTAL) {
        if (runToken == 1) {
          counter.getAndIncrement();
          runToken = 2;
        }
      }
    });
    Thread threadC = new Thread(() -> {
      while (counter.get() <= TOTAL) {
        if (runToken == 2) {
          counter.getAndIncrement();
          runToken = 0;
        }
      }
    });

    Thread threadD = new Thread(() -> {
      while (counter.get() <= TOTAL) {
        synchronized (lock) {
          if (runToken == 0) {
            counter.getAndIncrement();
            runToken = 1;
            lock.notifyAll();
          } else {
            try {
              lock.wait(); // 等待其他线程完成
            } catch (InterruptedException e) {
              Thread.currentThread().interrupt();
            }
          }
        }
      } 
    });

    Thread threadE = new Thread(() -> {
      while (counter.get() <= TOTAL) {
        synchronized (lock) {
          if (runToken == 1) {
            counter.getAndIncrement();
            runToken = 2;
            lock.notifyAll();
          } else {
            try {
              lock.wait(); // 等待其他线程完成
            } catch (InterruptedException e) {
              Thread.currentThread().interrupt();
            }
          }
        }
      }
    });

    Thread threadF = new Thread(() -> {
      while (counter.get() <= TOTAL) {
        synchronized (lock) {
          if (runToken == 2) {
            counter.getAndIncrement();
            runToken = 0;
            lock.notifyAll();
          } else {
            try {
              lock.wait(); // 等待其他线程完成
            } catch (InterruptedException e) {
              Thread.currentThread().interrupt();
            }
          }
        }
      }
    });
  // 记录开始时间
    long startTime = System.currentTimeMillis();
    threadA.start();
    threadB.start();
    threadC.start();
    threadA.join();
    threadB.join();
    threadC.join();
    long endTime = System.currentTimeMillis();
    System.out.println("Thread A, B, C : " + (endTime - startTime) + " ms");
    counter.set(0);
    runToken = 0;
    startTime = System.currentTimeMillis();
    threadD.start();
    threadE.start();
    threadF.start();
    threadD.join();
    threadE.join();
    threadF.join();
    endTime = System.currentTimeMillis();
    System.out.println("Thread D, E, F : " + (endTime - startTime) + " ms");
  }
}
