package nextstep.subway.station;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import nextstep.subway.station.domain.Station;

@DisplayName("역 관련 테스트")
public class StationTest {

	public static final Station 노포역 = Station.of(2L, "노포역");
	public static final Station 다대포해수욕장역 = Station.of(1L, "다대포해수욕장");

	@Test
	@DisplayName("생성 테스트")
	void createTest() {
		// given
		String name = "부산역";

		// when
		Station station = Station.of(name);

		// then
		assertThat(station).isEqualTo(Station.of(name));
	}

}
