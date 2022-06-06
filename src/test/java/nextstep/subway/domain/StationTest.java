package nextstep.subway.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class StationTest {

	@Test
	void create() {
		Station station = new Station("신도림역");

		assertEquals(station.getName(), "신도림역");
	}
}
