public class Singleton {
  private static final Object lock = new Object();
  private static volatile Singleton instance;

  private Singleton() {
  }

  public static Singleton getInstance() {
    if (instance == null) {
      synchronized (lock) {
        if (instance == null) {
          instance = new Singleton();
        }
      }
    }
    return instance;
  }
}
