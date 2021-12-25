package nextstep.subway.section;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import nextstep.subway.section.domain.Section;
import nextstep.subway.station.domain.Station;

public class SectionTest {
	Station 강남역;
	Station 판교역;
	Section 강남_판교;

	@BeforeEach
	void setUp() {
		강남역 = Station.of(1L, "강남역");
		판교역 = Station.of(2L, "판교역");
		강남_판교 = Section.of(강남역, 판교역, 10);
	}

	@Test
	@DisplayName("상행역이 같은지 아닌지 테스트")
	void 상행역_테스트() {
		// given
		Station 양재역 = Station.of(3L, "양재역");
		Section 강남_양재 = Section.of(강남역, 양재역, 3);
		Section 양재_판교 = Section.of(양재역, 판교역, 7);

		// when
		// then
		assertThat(강남_판교.isSameUpstation(강남_양재)).isTrue();
		assertThat(강남_판교.isSameUpstation(양재_판교)).isFalse();
	}

	@Test
	@DisplayName("하행역이 같은지 아닌지 테스트")
	void 하행역_테스트() {
		// given
		Station 양재역 = Station.of(3L, "양재역");
		Section 강남_양재 = Section.of(강남역, 양재역, 3);
		Section 양재_판교 = Section.of(양재역, 판교역, 7);

		// when
		// then
		assertThat(강남_판교.isSameDownStation(강남_양재)).isFalse();
		assertThat(강남_판교.isSameDownStation(양재_판교)).isTrue();
	}

	@Test
	@DisplayName("거리차이 값 테스트")
	void 거리_계산_테스트() {
		// given
		Station 양재역 = Station.of(3L, "양재역");
		Section 강남_양재 = Section.of(강남역, 양재역, 3);

		// when
		// then
		assertThat(강남_판교.diffDistance(강남_양재)).isEqualTo(7);
		assertThat(강남_판교.addDistance(강남_양재)).isEqualTo(13);
	}

	@Test
	@DisplayName("상행역 같은 역에 대해서 구간을 둘로 나누는 테스트")
	void 상행구간_업데이트_테스트() {
		// given
		Station 양재역 = Station.of(3L, "양재역");
		Section 강남_양재 = Section.of(강남역, 양재역, 3);

		// when
		강남_판교.update(강남_양재);
		// then
		assertThat(강남_판교.getUpStation()).isEqualTo(양재역);
	}

	@Test
	@DisplayName("하행역 같은 역에 대해서 구간을 둘로 나누는 테스트")
	void 하행구간_업데이트_테스트() {
		// given
		Station 양재역 = Station.of(3L, "양재역");
		Section 양재_판교 = Section.of(양재역, 판교역, 7);

		// when
		강남_판교.update(양재_판교);
		// then
		assertThat(강남_판교.getDownStation()).isEqualTo(양재역);
	}
}
