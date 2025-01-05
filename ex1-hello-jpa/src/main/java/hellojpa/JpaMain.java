package hellojpa;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class JpaMain {

    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");

        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        tx.begin();

        try {

            Member member = new Member();
            member.setName("member");
            member.setHomeAddress(new Address("homeCity", "state", "zip"));

            member.getFavoriteFoods().add("foodA");
            member.getFavoriteFoods().add("foodB");
            member.getFavoriteFoods().add("foodC");

            member.getAddressHistory().add(new AddressEntity(new Address("city!", "state", "zip")));
            member.getAddressHistory().add(new AddressEntity(new Address("city@", "state", "zip")));

            em.persist(member);

            em.flush();
            em.clear();

            System.out.println(" ========== ");

            Member findMember = em.find(Member.class, member.getId());

//            findMember.setHomeAddress(new Address("new-homeCity", "new-state", "new-zip")); // 값타입은 immutable해야함

            // 컬렉션 수정
//            findMember.getFavoriteFoods().remove("foodA");   // DELETE
//            findMember.getFavoriteFoods().add("치킨");            // INSERT

            System.out.println(" ADDRESS update !!");
            findMember.getAddressHistory().remove(new AddressEntity(new Address("city!", "state", "zip"))); // 찾을 때 equals를 사용하므로 반드시 재정의해둬야함
            findMember.getAddressHistory().add(new AddressEntity(new Address("newCity", "state", "zip"))); // INSERT

            tx.commit();
        } catch (Exception e) {
            tx.rollback();
        } finally {
            em.close();
        }
        emf.close();
    }
}
