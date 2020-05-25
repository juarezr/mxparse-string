import java.util.ArrayList;
import java.util.List;

public class ExampleBean {

    public final String name;
    public final String country;
    public final int age;

    public ExampleBean(final String newName, final String newCountry, final int newAge) {
        this.name = newName;
        this.country = newCountry;
        this.age = newAge;
    }

    public static List<ExampleBean> getTestItems() {
        final List<ExampleBean> items = new ArrayList<>();
        items.add(new ExampleBean("alice", "andorra", 33));
        items.add(new ExampleBean("bob", "brazil", 13));
        items.add(new ExampleBean("chris", "china", 42));
        return items;
    }

    @Override
    public String toString() {
        return String.format("name:%s, country:%s, age:%d", this.name, this.country, this.age);
    }
}
