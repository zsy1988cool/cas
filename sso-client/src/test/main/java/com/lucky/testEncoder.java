import static org.junit.Assert.*;

import org.junit.Test;
import org.springframework.security.crypto.password.StandardPasswordEncoder;

public class testEncoder {

	@Test
	public static void test() {
		//fail("Not yet implemented");
		
		StandardPasswordEncoder testEncoder = new StandPasswordEncoder();
		String en = testEncoder.encode("123");
		System.out.println(en);
	}

}
