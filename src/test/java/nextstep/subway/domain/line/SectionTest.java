package nextstep.subway.domain.line;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import nextstep.subway.domain.station.Station;

@DisplayName("구간 테스트")
class SectionTest {

	@Test
	@DisplayName("구간 생성 검증")
	void createSection() {
		// When
		Section 생성된_구간 = new Section(null, Station.from("논현역"), Station.from("신논현역"), 10);

		// Then
		assertThat(생성된_구간).isNotNull();
	}

}