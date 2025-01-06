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
            fetchJoin2(em);
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

    private static void innerJoin(EntityManager em) {
        Team team = new Team("team");
        em.persist(team);

        Member member = new Member();
        member.setAge(10);
        member.setUsername("name" + 1);
        member.changeTeam(team);
        em.persist(member);

        em.flush();
        em.clear();

        List<Member> result = em.createQuery("SELECT m FROM Member m join m.team t ", Member.class)
            .getResultList();

    }

    private static void leftJoin(EntityManager em) {
        Team team = new Team("team");
        em.persist(team);

        Member member = new Member();
        member.setAge(10);
        member.setUsername("name" + 1);
        member.changeTeam(team);
        em.persist(member);

        em.flush();
        em.clear();

        List<Member> result = em.createQuery("SELECT m FROM Member m left join m.team t ", Member.class)
                .getResultList();
    }

    public static void jpqlType(EntityManager em) {
        Member member = new Member();
        member.setAge(10);
        member.setUsername("name" + 1);
        member.setMemberType(MemberType.ADMIN);
        em.persist(member);

        em.flush();
        em.clear();

        List<Object[]> resultList = em.createQuery("SELECT 'HELLO', TRUE, m.username FROM Member m " +
                        "where m.memberType = jpql.MemberType.ADMIN")
                .getResultList();

        for (Object[] objects : resultList) {
            System.out.println("objects = " + objects[0]);
            System.out.println("objects = " + objects[1]);
            System.out.println("objects = " + objects[2]);
            
        }
    }

    /*
        기본 CASE
     */
    private static void case1(EntityManager em) {
        Member member = new Member();
        member.setAge(10);
        member.setUsername("name" + 1);
        member.setMemberType(MemberType.ADMIN);
        em.persist(member);

        em.flush();
        em.clear();

        String query = "select " +
                " case when m.age <= 10 then '학생'" +
                " when m.age >= 60 then '경로'" +
                " else '일반' " +
                "end " +
                " from Member m";
        List<String> resultList = em.createQuery(query)
                .getResultList();

        for (String result : resultList) {
            System.out.println(result);
        }
    }

    private static void coalesce(EntityManager em) {
        Member member = new Member();
        member.setAge(10);
        member.setMemberType(MemberType.ADMIN);
        em.persist(member);

        em.flush();
        em.clear();

        String query = "select coalesce(m.username, '이름없는 회원') from Member m";
        List<String> resultList = em.createQuery(query)
                .getResultList();

        for (String result : resultList) {
            System.out.println(result);
        }
    }

    private static void nullif(EntityManager em) {
        Member member = new Member();
        member.setUsername("관리자");
        member.setAge(10);
        member.setMemberType(MemberType.ADMIN);
        em.persist(member);

        em.flush();
        em.clear();

        String query = "select nullif(m.username, '관리자') from Member m";
        List<String> resultList = em.createQuery(query)
                .getResultList();

        for (String result : resultList) {
            System.out.println(result);
        }
    }

    private static void routeExpression(EntityManager em) {
        Team team = new Team("team");
        em.persist(team);

        Member member = new Member();
        member.setUsername("관리자");
        member.setAge(10);
        member.changeTeam(team);
        member.setMemberType(MemberType.ADMIN);
        em.persist(member);

        em.flush();
        em.clear();

        String query1 = "select m.username from Member m";  // 상태필드
        String query2 = "select m.team from Member m";      // 단일값 연관 경로
        String query3 = "select t.members from Team t";     // 컬렉션값연관경로
//        String query3 = "select t.members.name from Team t";     // 컬렉션값연관경로 - X
        Integer result = em.createQuery(query3, Integer.class).getSingleResult();
        System.out.println(result);

    }

    private static void fetchJoin1(EntityManager em) {
        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");

        em.persist(teamA);
        em.persist(teamB);

        Member member1 = new Member("member1");
        member1.setTeam(teamA);
        Member member2 = new Member("member2");
        member2.setTeam(teamA);
        Member member3 = new Member("member3");
        member3.setTeam(teamB);

        em.persist(member1);
        em.persist(member2);
        em.persist(member3);

        em.flush();
        em.clear();

        String query = "select m from Member m";
        List<Member> result = em.createQuery(query, Member.class).getResultList();  // 1
        System.out.println(result.size());
        for (Member member : result) {
            System.out.println(member.getUsername() + " , " + member.getTeam().getName());  // N (member에 대한 Team조회)
            // 즉시, 지연로딩 모두 발생
        }

        em.flush();
        em.clear();
        System.out.println(" ===== fetch join ===== ");

        String query2 = "select m from Member m join fetch m.team";
        List<Member> result2 = em.createQuery(query2, Member.class).getResultList(); // 1번에 조회
        System.out.println(result2.size());
        for (Member member : result2) {
            System.out.println(member.getUsername() + " , " + member.getTeam().getName());
        }
    }

    private static void fetchJoin2(EntityManager em) {
        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");

        em.persist(teamA);
        em.persist(teamB);

        Member member1 = new Member("member1");
        member1.setTeam(teamA);
        Member member2 = new Member("member2");
        member2.setTeam(teamA);
        Member member3 = new Member("member3");
        member3.setTeam(teamB);

        em.persist(member1);
        em.persist(member2);
        em.persist(member3);

        em.flush();
        em.clear();

        String query = "select t from Team t join fetch t.members";
        List<Team> result = em.createQuery(query, Team.class).getResultList();  // 1
        System.out.println(result.size());
        for (Team team : result) {
            System.out.println(team.getName() + " , " + team.getMemberList().size());  // N (member에 대한 Team조회)
            // 즉시, 지연로딩 모두 발생
        }

    }
}
