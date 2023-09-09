package be.cardoeng.example.controllers;

import be.cardoeng.example.entities.Member;
import be.cardoeng.example.repositories.MemberRepository;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * <p class="en">A simple REST controller to manage the members. The corresponding endpoint is `/api/members`</p>
 * <p class="fr">Un simple contrôleur REST pour gérer les membres. Le point de terminaison correspondant est `/api/members`</p>
 */
@RestController
// @CrossOrigin(origins = "http://localhost:5173") // Allows requests from localhost:5173 (frontend debugging)
@RequestMapping(path = "/api/members")
public class MemberController {

    /**
     * <p class="en">The repository to use to manage the members.</p>
     * <p class="fr">Le dépôt à utiliser pour gérer les membres.</p>
     */
    private final MemberRepository repository;

    /**
     * <p class="en">The object mapper to use to map the JSON to the {@link Member} object (and vice versa).</p>
     * <p class="fr">Le mappeur d'objets à utiliser pour mapper le JSON vers l'objet {@link Member} (et vice versa).</p>
     */
    @Autowired
    private ObjectMapper objectMapper;

    /**
     * <p class="en">The constructor of the controller.</p>
     * <p class="fr">Le constructeur du contrôleur.</p>
     * @param repository <p class="en">The repository to use to manage the members.</p>
     * <p class="fr">Le dépôt à utiliser pour gérer les membres.</p>
     */
    public MemberController(MemberRepository repository) {
        this.repository = repository;
    }

    /**
     * <p class="en">GetMapping (GET /api/members) that returns all the members we find. The status code should be
     * 200 except in case of unexpected errors.</p>
     * <p class="fr">GetMapping (GET /api/members) qui renvoie tous les membres que nous trouvons. Le code d'état
     * devrait être 200 sauf en cas d'erreurs inattendues.</p>
     * @return <p class="en">A {@link ResponseEntity} containing the list of members and the status code.</p>
     * <p class="fr">Un {@link ResponseEntity} contenant la liste des membres et le code d'état.</p>
     */
    @GetMapping(path = "")
    public ResponseEntity<List<Member>> getMembers() {
        return ResponseEntity.ok(repository.findAll());
    }

    /**
     * <p class="en">PostMapping (POST /api/members) that creates a new member. The status code should be 201 if
     * the member is created and 409 if the member already exists (i.e. the id is already in the database). Use
     * a PATCH request if you want to modify a member.</p>
     * <p class="fr">PostMapping (POST /api/members) qui crée un nouveau membre. Le code d'état devrait être 201 si
     * le membre est créé et 409 si le membre existe déjà (c'est-à-dire que l'id est déjà dans la base de données). Utilisez
     * une requête PATCH si vous voulez le modifier.</p>
     * @param member <p class="en">The member to create.</p> <p class="fr">Le membre à créer.</p>
     * @return <p class="en">A {@link ResponseEntity} containing the created member and the status code.</p>
     * <p class="fr">Un {@link ResponseEntity} contenant le membre créé et le code d'état.</p>
     */
    @PostMapping(path = "")
    public ResponseEntity<Member> newMember(@RequestBody Member member) {
        if (repository.existsById(member.getId())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(repository.save(member));
    }

    /**
     * <p class="en">GetMapping (GET /api/members/{id}) that returns the member with the given id. The status code
     * should be 200 if the member is found and 404 if the member is not found.</p>
     * <p class="fr">GetMapping (GET /api/members/{id}) qui renvoie le membre avec l'id donné. Le code d'état
     * devrait être 200 si le membre est trouvé et 404 si le membre n'est pas trouvé.</p>
     * @param id <p class="en">The id of the member to find.</p> <p class="fr">L'id du membre à trouver.</p>
     * @return <p class="en">A {@link ResponseEntity} containing the member and the status code.</p>
     * <p class="fr">Un {@link ResponseEntity} contenant le membre et le code d'état.</p>
     */
    @GetMapping(path = "/{id}")
    public ResponseEntity<Member> getMember(@PathVariable int id) {
        try {
            Member m = repository.findById(id).orElseThrow();
            return ResponseEntity.ok(m);
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * <p class="en">PatchMapping (PATCH /api/members/{id}) that modifies the member with the given id. The status
     * code should be 200 if the member is modified and 404 if the member is not found. </p>
     * <p class="fr">PatchMapping (PATCH /api/members/{id}) qui modifie le membre avec l'id donné. Le code d'état
     * devrait être 200 si le membre est modifié et 404 si le membre n'est pas trouvé.</p>
     * @param id <p class="en">The id of the member to modify.</p> <p class="fr">L'id du membre à modifier.</p>
     * @param m <p class="en">The modifications to apply to the member.</p> <p class="fr">Les modifications à
     *         appliquer au membre.</p>
     * @return <p class="en">A {@link ResponseEntity} containing the modified member and the status code.</p>
     * <p class="fr">Un {@link ResponseEntity} contenant le membre modifié et le code d'état.</p>
     */
    @PatchMapping(path = "/{id}")
    public ResponseEntity<Member> patchMember(@PathVariable int id, @RequestBody Map<String, Object> m) {
        try {
            if (m.containsKey("id"))
                m.remove("id");
            
            Member member = repository.findById(id).orElseThrow();
            objectMapper.updateValue(member, m);
            return ResponseEntity.ok(repository.save(member));
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        } catch (JsonMappingException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * <p class="en">DeleteMapping (DELETE /api/members/{id}) that deletes the member with the given id. The status
     * code should be 204 if the member is deleted and 404 if the member is not found.</p>
     * <p class="fr">DeleteMapping (DELETE /api/members/{id}) qui supprime le membre avec l'id donné. Le code d'état
     * devrait être 204 si le membre est supprimé et 404 si le membre n'est pas trouvé.</p>
     * @param id <p class="en">The id of the member to delete.</p> <p class="fr">L'id du membre à supprimer.</p>
     * @return <p class="en">A {@link ResponseEntity} containing the status code.</p>
     * <p class="fr">Un {@link ResponseEntity} contenant le code d'état.</p>
     */
    @DeleteMapping(path = "/{id}")
    public ResponseEntity<?> deleteMember(@PathVariable int id) {
        try {
            Member m = repository.findById(id).orElseThrow();
            repository.delete(m);
            return ResponseEntity.noContent().build();
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        }
    }

}
