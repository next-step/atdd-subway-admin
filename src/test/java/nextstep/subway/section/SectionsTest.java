package nextstep.subway.section;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import nextstep.subway.section.domain.Section;
import nextstep.subway.section.domain.Sections;
import nextstep.subway.station.domain.Station;

public class SectionsTest {
	Sections sections;
	Station 강남역;
	Station 판교역;
	Section 강남_판교;

	@BeforeEach
	void setUp() {
		강남역 = Station.of(1L, "강남역");
		판교역 = Station.of(2L, "판교역");
		강남_판교 = Section.of(강남역, 판교역, 10);

		sections = new Sections();
		sections.addSection(강남_판교);
	}

	@Test
	@DisplayName("구간 추가 테스트")
	void 구간추가() {
		// given
		Station 양재역 = Station.of(3L, "양재역");
		Section 강남_양재 = Section.of(강남역, 양재역, 3);

		// when
		sections.addSection(강남_양재);

		// then
		assertThat(sections.contains(강남_양재)).isTrue();
	}

	@Test
	@DisplayName("구간 삭제 테스트")
	void 구간삭제() {
		// given
		Station 양재역 = Station.of(3L, "양재역");
		Section 강남_양재 = Section.of(강남역, 양재역, 3);
		sections.addSection(강남_양재);

		// when
		sections.removeSection(양재역);

		// then
		assertThat(sections.contains(강남_판교)).isTrue();
	}

	@Test
	@DisplayName("동일구간 추가 테스트")
	void 동일구간추가() {
		// when
		// then
		assertThatIllegalArgumentException().isThrownBy(() -> {
			sections.addSection(강남_판교);
		});
	}

	@Test
	@DisplayName("현재 존재하는 구간보다 더 긴 구간 추가 테스트")
	void 긴구간추가() {
		// given
		Station 양재역 = Station.of(3L, "양재역");
		Section 강남_양재 = Section.of(강남역, 양재역, 15);

		// when
		// then
		assertThatIllegalArgumentException().isThrownBy(() -> {
			sections.addSection(강남_양재);
		});
	}

	@Test
	@DisplayName("현재 존재하는 구간에 아예 존재하지 않는 역 구간 추가 테스트")
	void 없는구간추가() {
		// given
		Station 양재역 = Station.of(3L, "양재역");
		Station 상현역 = Station.of(4L, "상현역");
		Section 양재_상현 = Section.of(양재역, 상현역, 15);

		// when
		// then
		assertThatIllegalArgumentException().isThrownBy(() -> {
			sections.addSection(양재_상현);
		});
	}

	@Test
	@DisplayName("구간들의 상행부터 하행까지의 역 리스트 출력 테스트")
	void 역리스트() {
		// given
		Station 양재역 = Station.of(3L, "양재역");
		Section 강남_양재 = Section.of(강남역, 양재역, 3);

		// when
		sections.addSection(강남_양재);

		// then
		assertThat(sections.getStations()).containsExactly(강남역, 양재역, 판교역);
	}
}
