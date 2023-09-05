package be.cardoeng.example;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import be.cardoeng.example.controllers.MemberController;

@SpringBootTest
class ExampleApplicationTests {

	@Autowired
	private MemberController controller;

	@Test
	void contextLoads() {
		assertNotNull(controller);
	}

}
