import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

class Person {
    String name;
    int age;

    Person(String name, int age) {
        this.name = name;
        this.age = age;
    }

    @Override
    public String toString() {
        return name;
    }
}

class Foo {
    String name;
    List<Bar> bars = new ArrayList<>();

    Foo(String name) {
        this.name = name;
    }
}

class Bar {
    String name;

    Bar(String name) {
        this.name = name;
    }
}

public class Main {

    static final List<Person> persons =
            Arrays.asList(
                    new Person("Max", 18),
                    new Person("Peter", 23),
                    new Person("Pamela", 23),
                    new Person("David", 12));

    public static void main(String[] args) {
        collections();
        streams();
        streamsProcessingOrder();
        reusingStreams();
        collect();
        flatMap();
    }

    private static void collections() {
        List strList = new ArrayList<String>();
        strList.add(0, "0");
        strList.add("1");
        strList.set(1, "new 1");
        strList.remove("new 1");
        System.out.println(strList);

        List newList = Arrays.asList("a", "b");
        System.out.println(newList);

        Map<String, String> map = new HashMap<>();
        map.put("C", "c");
        map.put("B", "b");
        map.put("Z", "z");
        map.forEach((i, v) -> System.out.println(i + "   " + v));

        String notFound = map.getOrDefault("A", "defaultValue");
        System.out.println(notFound);
    }

    private static void streams() {
        List<String> myList = Arrays.asList("a1", "a2", "b1", "c2", "c1");
        myList
                .stream()
                .filter(s -> s.startsWith("c"))
                .map(String::toUpperCase)
                .sorted()
                .forEach(System.out::println);

        Stream.of("a1", "a2", "a3")
                .findFirst()
                .ifPresent(System.out::println);

        IntStream.range(1, 3)
                .forEach(System.out::println);

        Arrays.stream(new int[] {1, 2, 3})
                .map(n -> 2 * n + 1)
                .average()
                .ifPresent(System.out::println);
    }

    private static void streamsProcessingOrder() {
        Stream.of("d2", "a2", "b1", "b3", "c")
                .filter(s -> {
                    System.out.println("filter: " + s);
                    return true;
                })
                .forEach(s -> System.out.println("forEach: " + s));

        System.out.println("=============");

        Stream.of("d2", "a2", "b1", "b3", "c")
                .filter(s -> {
                    System.out.println("filter: " + s);
                    return s.startsWith("a");
                })
                .map(s -> {
                    System.out.println("map: " + s);
                    return s.toUpperCase();
                })
                .forEach(s -> System.out.println("forEach: " + s));
    }

    private static void reusingStreams() {
        Supplier<Stream<String>> streamSupplier =
                () -> Stream.of("d2", "a2", "b1", "b3", "c")
                    .filter(s -> s.startsWith("a"));

        streamSupplier.get().anyMatch(s -> true);
        streamSupplier.get().anyMatch(s -> false);
    }

    private static void collect() {
        List<Person> filtered = persons
                .stream()
                .filter(p -> p.name.startsWith("P"))
                .collect(Collectors.toList());
        System.out.println(filtered);

        Map<Integer, List<Person>> personsByAge = persons
                .stream()
                .collect(Collectors.groupingBy(p -> p.age));
        personsByAge
                .forEach((age, p) -> System.out.format("age %s: %s\n", age, p));

        Double averageAge = persons
                .stream()
                .collect(Collectors.averagingInt(p -> p.age));
        System.out.println(averageAge);

        IntSummaryStatistics ageSummary = persons
                .stream()
                .collect(Collectors.summarizingInt(p -> p.age));
        System.out.println(ageSummary);

        Map<Integer, String> map = persons
                .stream()
                .collect(Collectors.toMap(
                        p -> p.age,
                        p -> p.name,
                        (name1, name2) -> name1 + ";" + name2));
        System.out.println(map);
    }

    private static void flatMap() {
        List<Foo> foos = new ArrayList<>();

        // create foos
        IntStream
                .range(1, 4)
                .forEach(i -> foos.add(new Foo("Foo" + i)));

        // create bars
        foos.forEach(f ->
                IntStream
                    .range(1, 4)
                    .forEach(i -> f.bars.add(new Bar("Bar" + i + " <- " + f.name))));

        foos.stream()
                .flatMap(f -> f.bars.stream())
                .forEach(b -> System.out.println(b.name));
    }

}
