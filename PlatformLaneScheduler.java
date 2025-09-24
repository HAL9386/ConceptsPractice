import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

// https://zhuanlan.zhihu.com/p/1930741774827098466
public class PlatformLaneScheduler {
  public static void main(String[] args) {
    PlatformLaneScheduler scheduler = new PlatformLaneScheduler();
    System.out.printf("[%tT.%tL] 调度器启动%n", System.currentTimeMillis(), System.currentTimeMillis());
    scheduler.start();

    // 运行一段时间后停止调度器
    try {
      Thread.sleep(20000); // 运行20秒
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }
    scheduler.stop();
  }
  static class Platform {
    final String name;
    final long interval;
    long nextAllowedTime;

    public Platform(String name, long intervalSeconds) {
      this.name = name;
      this.interval = intervalSeconds * 1000;
      this.nextAllowedTime = System.currentTimeMillis();
    }
  }

  private Map<String, Platform> platformMap = new HashMap<>();
  private long lastExecutionTime = 0;
  private final ScheduledExecutorService scheduler;

  public PlatformLaneScheduler() {
    this.scheduler = Executors.newSingleThreadScheduledExecutor();
    addPlatform("A", 5);
    addPlatform("B", 3);
    addPlatform("C", 1);
  }

  public void addPlatform(String name, long intervalSeconds) {
    platformMap.put(name, new Platform(name, intervalSeconds));
  }

  public void start() {
    scheduler.scheduleAtFixedRate(this::checkPlatforms, this.lastExecutionTime, 100, TimeUnit.MILLISECONDS);
  }

  public void stop() {
    scheduler.shutdown();
  }

  private void checkPlatforms() {
    long now = System.currentTimeMillis();
    if (now - this.lastExecutionTime < 1000) {
      return;
    }
    Platform earliestPlatform = platformMap.values().stream()
      .filter(platform -> now >= platform.nextAllowedTime)
      .min((p1, p2) -> Long.compare(p1.nextAllowedTime, p2.nextAllowedTime))
      .orElse(null);
    if (earliestPlatform != null) {
      executePlatformRequest(earliestPlatform, now);
    }
  }

  private void executePlatformRequest(Platform platform, long now) {
    System.out.printf("[%tT.%tL] %s平台执行 | 设定间隔: %ds | 实际间隔: %.3fs%n",
                now, now, platform.name, platform.interval / 1000,
                (now - platform.nextAllowedTime + platform.interval) / 1000.0);
    platform.nextAllowedTime = now + platform.interval;
    this.lastExecutionTime = now;
  }
}