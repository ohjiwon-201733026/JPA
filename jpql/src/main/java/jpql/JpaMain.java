package jpql;

import javax.persistence.*;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;

public class JpaMain {

    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");

        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        tx.begin();

        try {
            paging(em);
            tx.commit();
        } catch (Exception e) {
            tx.rollback();
        } finally {
            em.close();
        }
        emf.close();
    }

    /*
        파라미터 세팅 방법
     */
    private void setParameter(EntityManager em) {
        Member member = new Member();
        member.setAge(10);
        member.setUsername("name");
        em.persist(member);

        Member findMember = em.createQuery("SELECT m FROM Member m where m.username=:username", Member.class)
                .setParameter("username", "name")
                .getSingleResult();
        System.out.println(findMember.getAge());
    }

    /*
        entity projection 하면 영속성 컨텍스트에서 모두 관리됨
     */
    private static void entityProjection1(EntityManager em) {
        Member member = new Member();
        member.setAge(10);
        member.setUsername("name");
        em.persist(member);

        em.flush();
        em.clear();

        List<Member> memberList = em.createQuery("SELECT m FROM Member m", Member.class)
                .getResultList();
        Member findMember = memberList.get(0);
        findMember.setAge(20);
        System.out.println(findMember.getAge());
    }

    private static void entityProjection2(EntityManager em) {
        Member member = new Member();
        member.setAge(10);
        member.setUsername("name");
        em.persist(member);

        em.flush();
        em.clear();

        // 나쁜 예 - join 발생하는 것을 명시적으로 나타내지 않음
        List<Team> teamList1 = em.createQuery("SELECT m.team FROM Member m", Team.class) // join 발생
                .getResultList();

        List<Team> teamList2 = em.createQuery("SELECT t FROM Member m join m.team t", Team.class) // join 발생
                .getResultList();
    }

    /*
        embedded projection
     */
    private static void embeddedProjection(EntityManager em) {
        Member member = new Member();
        member.setAge(10);
        member.setUsername("name");
        em.persist(member);

        em.flush();
        em.clear();

        em.createQuery("SELECT o.address FROM Order o", Address.class)
                .getResultList();
    }

    /*
        여러 값 조회
     */
    private static void scalarProjection(EntityManager em) {
        Member member = new Member();
        member.setAge(10);
        member.setUsername("name");
        em.persist(member);

        em.flush();
        em.clear();

        List<MemberDTO> list = em.createQuery("SELECT new jpql.MemberDTO(m.username, m.age) FROM Member m", MemberDTO.class)
                .getResultList();
        System.out.println(list.get(0).getAge());
    }

    private static void paging(EntityManager em) {
        for(int i = 1; i <= 100; i++) {
            Member member = new Member();
            member.setAge(i);
            member.setUsername("name" + i);
            em.persist(member);
        }

        em.flush();
        em.clear();

        List<Member> result = em.createQuery("SELECT m FROM Member m order by m.age desc", Member.class)
                .setFirstResult(0)
                .setMaxResults(10)
                .getResultList();

        System.out.println("size = " + result.size());
        for (Member member1 : result) {
            System.out.println(member1.getAge());
        }
    }
}
