import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class Generic {
  public static void main(String[] args) throws Exception {
    List<Integer> list = new ArrayList<>();
    list.add(1);

    String str = "Hello";

    Method addMethod = list.getClass().getMethod("add", Object.class);
    addMethod.invoke(list, str);
  
    System.out.println("list = " + list);

    // 读取为 Integer 时会在运行时发生 ClassCastException
    try {
      Integer second = list.get(1); // 编译器在这里插入了对 Integer 的 checkcast
      System.out.println("second = " + second);
    } catch (ClassCastException e) {
      System.out.println("读取时发生 ClassCastException: " + e.getMessage());
    }

    // 如果用原始类型读取，可以看到实际对象类型
    @SuppressWarnings({ "rawtypes" })
    List raw = list; // 原始类型（不建议使用）
    Object o = raw.get(1);
    System.out.println("raw get(1) = " + o + " (" + o.getClass().getName() + ")");
  }
}
