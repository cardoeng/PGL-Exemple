package be.cardoeng.example.requests;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

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

/**
 * <p class="en">A test class to test the HTTP requests.</p>
 * <p class="fr">Une classe de test pour tester les requêtes HTTP.</p>
 */
@SpringBootTest
@AutoConfigureMockMvc
public class HttpRequestsTest {

    /**
     * <p class="en">The repository to use to manage the members.</p>
     * <p class="fr">Le dépôt à utiliser pour gérer les membres.</p>
     */
    @Autowired
    private MemberRepository memberRepository;

    /**
     * <p class="en">The mock MVC to use to test the HTTP requests.</p>
     * <p class="fr">Le mock MVC à utiliser pour tester les requêtes HTTP.</p>
     */
    @Autowired
    private MockMvc mockMvc;

    /**
     * <p class="en">The object mapper to use to map the JSON to the {@link Member} object (and vice versa).</p>
     * <p class="fr">Le mappeur d'objets à utiliser pour mapper le JSON vers l'objet {@link Member} (et vice versa).</p>
     */
    @Autowired
    private ObjectMapper objectMapper;

    /**
     * <p class="en">The first member to use in the tests.</p>
     * <p class="fr">Le premier membre à utiliser dans les tests.</p>
     */
    private Member member1;
    /**
     * <p class="en">The second member to use in the tests.</p>
     * <p class="fr">Le deuxième membre à utiliser dans les tests.</p>
     */
    private Member member2;

    /**
     * <p class="en">Setup the tests. Executed before each test.</p>
     * <p class="fr">Configure les tests. Exécuté avant chaque test.</p>
     * @throws ParseException <p class="en">If the date cannot be parsed.</p>
     * <p class="fr">Si la date ne peut pas être parsée.</p>
     */
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

    /**
     * <p class="en">Teardown the tests. Executed after each test.</p>
     * <p class="fr">Déconfigure les tests. Exécuté après chaque test.</p>
     */
    @AfterEach
    public void teardown() {
        memberRepository.deleteAll();
    }
    
    /**
     * <p class="en">Test the GET request to get all the members.</p>
     * <p class="fr">Teste la requête GET pour obtenir tous les membres.</p>
     * @throws Exception <p class="en">If the request fails.</p>
     * <p class="fr">Si la requête échoue.</p>
     */
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

    /**
     * <p class="en">Transform a {@link Member} object to a JSON string.</p>
     * <p class="fr">Transforme un objet {@link Member} en une chaîne JSON.</p>
     * @param m <p class="en">The member to transform.</p> <p class="fr">Le membre à transformer.</p>
     * @return <p class="en">The JSON string.</p> <p class="fr">La chaîne JSON.</p>
     */
    private String toJson(Member m) {
        return objectMapper.valueToTree(m).toString();
    }

    /**
     * <p class="en">Transform a JSON string to a {@link Member} object.</p>
     * <p class="fr">Transforme une chaîne JSON en un objet {@link Member}.</p>
     * @param s <p class="en">The JSON string.</p> <p class="fr">La chaîne JSON.</p>
     * @return <p class="en">The corresponding {@link Member}.</p> <p class="fr">Le {@link Member} correspondant.</p>
     * @throws JsonMappingException
     * @throws JsonProcessingException
     */
    private Member fromJson(String s) throws JsonMappingException, JsonProcessingException {
        return objectMapper.readValue(s, Member.class);
    }

    /**
     * <p class="en">Test the POST request to add a member.</p>
     * <p class="fr">Teste la requête POST pour ajouter un membre.</p>
     * @throws Exception <p class="en">If the request fails.</p>
     */
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

    /**
     * <p class="en">Test the POST request to add a member that already exists.</p>
     * <p class="fr">Teste la requête POST pour ajouter un membre qui existe déjà.</p>
     * @throws Exception <p class="en">If the request fails.</p>
     */
    @Test
    public void testConflictAddMember() throws Exception {
        memberRepository.save(member1);
        memberRepository.save(member2);
        Member member3 = new Member( // Values are not important, only id is
            1,
            "",
            "",
            "",
            MemberStatus.Assistant,
            new Date(),
            null
        );
        this.mockMvc.perform(
            post("/api/members")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(member3)))
            .andExpect(status().isConflict());

    }

    /**
     * <p class="en">Test the PATCH request to update a member.</p>
     * <p class="fr">Teste la requête PATCH pour mettre à jour un membre.</p>
     * @throws Exception <p class="en">If the request fails.</p>
     */
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

    /**
     * <p class="en">Test the DELETE request to delete a member.</p>
     * <p class="fr">Teste la requête DELETE pour supprimer un membre.</p>
     * @throws Exception <p class="en">If the request fails.</p> <p class="fr">Si la requête échoue.</p>
     */
    @Test
    public void testDeleteMember() throws Exception  {
        memberRepository.save(member1);
        memberRepository.save(member2);
        this.mockMvc.perform(
            delete("/api/members/%d".formatted(member1.getId())))
            .andExpect(status().isNoContent());
        assertEquals(1, memberRepository.count());
        assertEquals(member2, memberRepository.findAll().get(0));
    }

    /*
     * There are obviously other tests to do (delete on a non existing member, patch on a non existing member, ...)
     * However, this is just an example.
     */

}
