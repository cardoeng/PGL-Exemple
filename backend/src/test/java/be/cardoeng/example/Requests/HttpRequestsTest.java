package be.cardoeng.example.Requests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;

import be.cardoeng.example.entities.Member;
import be.cardoeng.example.enums.MemberStatus;
import be.cardoeng.example.repositories.MemberRepository;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class HttpRequestsTest {

    @Value("${local.server.port}")
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;
    

    public String getBaseUrl() {
        return "http://localhost:" + port;
    }


    // Should be [{"id":1,"firstName":"Guillaume","lastName":"Cardoen","email":"Guillaume.CARDOEN@umons.ac.be","status":"Assistant","beginDate":"2023-09-14T22:00:00.000+00:00","endDate":null},
    // {"id":2,"firstName":"Tom","lastName":"Mens","email":"Tom.MENS@umons.ac.be","status":"Professor","beginDate":"2023-10-01T00:00:00.000+00:00","endDate":null}]
    @Test
    public void testGetMembers() {
        String url = getBaseUrl() + "/api/members";
        // json to Members
        Member[] members = restTemplate.getForObject(url, Member[].class);

        assertEquals(2, members.length);

        // Test first object
        assertEquals(1, members[0].getId());
        assertEquals("Guillaume", members[0].getFirstName());
        assertEquals("Cardoen", members[0].getLastName());
        assertEquals("Guillaume.CARDOEN@umons.ac.be", members[0].getEmail());
        assertEquals(MemberStatus.Assistant, members[0].getStatus());
        assertEquals("Fri Sep 15 00:00:00 CEST 2023", members[0].getBeginDate().toString());
        assertNull(members[0].getEndDate());

        // Test second object
        assertEquals(2, members[1].getId());
        assertEquals("Tom", members[1].getFirstName());
        assertEquals("Mens", members[1].getLastName());
        assertEquals("Tom.MENS@umons.ac.be", members[1].getEmail());
        assertEquals(MemberStatus.Professor, members[1].getStatus());
        assertEquals("Sun Oct 01 02:00:00 CEST 2023", members[1].getBeginDate().toString());
        assertNull(members[1].getEndDate());


    }

}
