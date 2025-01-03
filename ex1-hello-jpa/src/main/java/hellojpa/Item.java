package hellojpa;

import javax.persistence.*;

@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
//@DiscriminatorColumn
public abstract class Item {

    @Id @GeneratedValue
    private Long id;

    private String name;
    private int price;

    public Item() {
    }

    public Item(String name, int price) {
        this.name = name;
        this.price = price;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getPrice() {
        return price;
    }
}
