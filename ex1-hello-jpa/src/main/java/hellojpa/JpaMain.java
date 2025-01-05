package hellojpa;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class JpaMain {

    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");

        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        tx.begin();

        try {
            Address address = new Address("city", "state", "zip");

            Member member1 = new Member();
            member1.setName("name");
            member1.setHomeAddress(address);
            member1.setWorkPeriod(new Period(LocalDateTime.now(), LocalDateTime.now()));

            Member member2 = new Member();
            member2.setName("name");
            member2.setHomeAddress(address);
            member2.setWorkPeriod(new Period(LocalDateTime.now(), LocalDateTime.now()));

            em.persist(member1);
            em.persist(member2);

//            member1.getHomeAddress().setCity("NYC");

            tx.commit();
        } catch (Exception e) {
            tx.rollback();
        } finally {
            em.close();
        }
        emf.close();
    }
}
