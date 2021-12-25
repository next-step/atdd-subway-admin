package nextstep.subway;

import org.junit.jupiter.api.Test;

class SubwayApplicationTests {

	@Test
	void contextLoads() {
		for (int x = 0; x < 10000; x++) {
			int temp = x * x + 4;
			if (temp * x == 8) {
				System.out.println(x);
			}
		}
		System.out.println("Done");
	}

}
