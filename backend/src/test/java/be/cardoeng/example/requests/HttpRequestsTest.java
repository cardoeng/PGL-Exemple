package be.cardoeng.example.requests;

import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import be.cardoeng.example.entities.Member;
import be.cardoeng.example.enums.MemberStatus;
import be.cardoeng.example.repositories.MemberRepository;

@SpringBootTest
@AutoConfigureMockMvc
public class HttpRequestsTest {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private Member member1;
    private Member member2;

    @BeforeEach
    public void setup() throws ParseException {
        member1 = new Member(
            1,
            "Guillaume",
            "Cardoen",
            "Guillaume.CARDOEN@umons.ac.be",
            MemberStatus.Assistant,
            new SimpleDateFormat("dd/MM/yyyy").parse("15/09/2023"),
            new SimpleDateFormat("dd/MM/yyyy").parse("15/09/2029")
        );
        member2 = new Member(
            2,
            "Tom",
            "Mens",
            "Tom.MENS@umons.ac.be",
            MemberStatus.Professor,
            new SimpleDateFormat("MM/yyyy").parse("10/2003"),
            null
        );
    }

    @AfterEach
    public void teardown() {
        memberRepository.deleteAll();
    }
    

    @Test
    public void testGetMembers() throws Exception {
        memberRepository.save(member1);
        memberRepository.save(member2);
        ResultActions ra = this.mockMvc.perform(get("/api/members"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$").isArray());
        String s = ra.andReturn().getResponse().getContentAsString();
        Member[] members = objectMapper.readValue(s, Member[].class);
        assertEquals(2, members.length);
        assertEquals(member1, members[0]);
        assertEquals(member2, members[1]);
    }

    private String toJson(Member m) {
        return objectMapper.valueToTree(m).toString();
    }

    private Member fromJson(String s) throws JsonMappingException, JsonProcessingException {
        return objectMapper.readValue(s, Member.class);
    }

    @Test
    public void testAddMember() throws Exception {
        memberRepository.save(member1);
        
        String s = this.mockMvc.perform(
            post("/api/members")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(member2)))
            .andExpect(status().isCreated())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$").isMap())
            .andReturn().getResponse().getContentAsString();
        Member saved = fromJson(s);
        assertEquals(member2, saved);
    }

    @Test
    public void testUpdateMember() throws Exception {
        memberRepository.save(member1);
        memberRepository.save(member2);
        member1.setStatus(MemberStatus.Professor);
        String s = this.mockMvc.perform(
            patch("/api/members/%d".formatted(member1.getId()))
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"status\":\"Professor\"}"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$").isMap())
            .andReturn().getResponse().getContentAsString();
        Member saved = fromJson(s);
        assertEquals(member1, saved);
        saved = memberRepository.findById(member1.getId()).orElseThrow();
        assertEquals(member1, saved);
    }

    @Test
    public void testDeleteMember() throws UnsupportedEncodingException, Exception  {
        memberRepository.save(member1);
        memberRepository.save(member2);
        this.mockMvc.perform(
            delete("/api/members/%d".formatted(member1.getId())))
            .andExpect(status().isNoContent());
        assertEquals(1, memberRepository.count());
        assertEquals(member2, memberRepository.findAll().get(0));
    }

}
