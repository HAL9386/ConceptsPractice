public class Copy {
  public static void main(String[] args) {
    Person a = new Person(18, "Alice", new Address("New York"));
    Person b = new Person(a.age, a.name, a.address);
    b.age = 20;
    b.name = "Bob";
    b.address.city = "Los Angeles";
    System.out.println(a.age);
    System.out.println(a.name);
    System.out.println(a.address.city);
  }
  static class Person {
    public int age;
    public String name;
    public Address address;
    public Person(int age, String name, Address address) {
      this.age = age;
      this.name = name;
      this.address = address;
    }
  }
  static class Address {
    public String city;
    public Address(String city) {
      this.city = city;
    }
  }
}
