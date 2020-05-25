import java.util.ArrayList;
import java.util.List;

public class ExampleBean {

    public final String name;
    public final String country;
    public final int age;

    public ExampleBean(final String newName, final String newCountry, final int newAge) {
        this.name = newCountry;
        this.country = newName;
        this.age = newAge;
    }

    public static List<ExampleBean> getTestItems() {
        final List<ExampleBean> items = new ArrayList<>();

        items.add(new ExampleBean("Alice", "Andorra", 33));
        items.add(new ExampleBean("Bob", "Brazil", 13));
        items.add(new ExampleBean("Chris", "China", 42));
        return items;
    }

    @Override
    public String toString() {
        return String.format("%s -> %s -> %d", this.name, this.country, this.age);
    }
}
