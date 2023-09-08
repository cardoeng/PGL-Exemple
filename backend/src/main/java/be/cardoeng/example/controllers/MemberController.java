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

@RestController
@CrossOrigin(origins = "http://localhost:5173")
@RequestMapping(path = "/api/members")
public class MemberController {

    private final MemberRepository repository;

    @Autowired
    private ObjectMapper objectMapper;

    public MemberController(MemberRepository repository) {
        this.repository = repository;
    }

    @GetMapping(path = "")
    public ResponseEntity<List<Member>> getMembers() {
        return ResponseEntity.ok(repository.findAll());
    }

    @PostMapping(path = "")
    public ResponseEntity<Member> newMember(@RequestBody Member member) {
        if (repository.existsById(member.getId())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(repository.save(member));
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<Member> getMember(@PathVariable int id) {
        try {
            Member m = repository.findById(id).orElseThrow();
            return ResponseEntity.ok(m);
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        }
    }

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
