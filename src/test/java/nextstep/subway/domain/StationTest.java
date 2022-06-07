package nextstep.subway.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("지하철역 도메인")
public class StationTest {

	@Test
	void create() {
		Station station = new Station("신도림역");

		assertEquals(station.getName(), "신도림역");
	}
}
