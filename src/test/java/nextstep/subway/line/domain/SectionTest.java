package nextstep.subway.line.domain;

import static nextstep.subway.station.StationFixture.*;
import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("구간")
public class SectionTest {

	@DisplayName("구간을 생성한다.")
	@Test
	void of() {
		// given & when
		Section section = Section.of(강남역().getId(), 역삼역().getId(), 1);

		// then
		assertThat(section).isNotNull();
	}

	@DisplayName("상행역과 하행역이 같은 경우 구간을 생성할 수 없다.")
	@Test
	void ofFailIfUpStationAndDownStationIsEqual() {
		// given & when & then
		assertThatThrownBy(() -> Section.of(강남역().getId(), 강남역().getId(), 0))
			.isInstanceOf(IllegalArgumentException.class);
	}
}
