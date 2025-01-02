package hellojpa;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("A")
public class Album extends Item{

    private String artist;

    public Album(String name, int price) {
        super(name, price);
    }

    public Album(String name, int price, String artist) {
        super(name, price);
        this.artist = artist;
    }
}
