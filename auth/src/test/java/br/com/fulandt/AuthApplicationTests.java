package br.com.fulandt;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
class AuthApplicationTests {

	@Test
	void contextLoads() {
	}
	@Test
	public void test(){
			System.out.println(new BCryptPasswordEncoder().encode("fulandt"));

	}

}
