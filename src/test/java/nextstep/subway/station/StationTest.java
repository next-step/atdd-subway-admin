package nextstep.subway.station;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import nextstep.subway.station.domain.Station;

@DisplayName("역 관련 테스트")
public class StationTest {

	public static final Station 노포역 = Station.of(1L, "노포역");
	public static final Station 서면역 = Station.of(2L, "서면역");
	public static final Station 범내골역 = Station.of(3L, "범내골역");
	public static final Station 다대포해수욕장역 = Station.of(4L, "다대포해수욕장");
	public static final Station 범어사역 = Station.of(5L, "범어사역");

	@Test
	@DisplayName("생성 테스트")
	void createTest() {
		// given, when
		Station station = Station.of(1L, "노포역");

		// then
		assertThat(station).isEqualTo(노포역);
	}

}
