package nextstep.subway.section;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import nextstep.subway.section.domain.Section;
import nextstep.subway.station.StationTest;

@DisplayName("구간 도메인 테스트")
public class SectionTest {

	public static final Section SECTION_1 = Section.of(StationTest.노포역, StationTest.다대포해수욕장역, 40);

	@Test
	@DisplayName("생성한다")
	void create() {
		// given
		int distance = 40;

		// when
		Section section = Section.of(StationTest.노포역, StationTest.다대포해수욕장역, distance);

		// then
		assertThat(section).isEqualTo(Section.of(StationTest.노포역, StationTest.다대포해수욕장역, distance));
	}
}
