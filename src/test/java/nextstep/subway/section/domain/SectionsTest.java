package nextstep.subway.section.domain;

import nextstep.subway.station.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static nextstep.subway.exception.CommonExceptionMessage.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Sections 도메인 비즈니스 로직 테스트")
class SectionsTest {

	private Station 강남역;
	private Station 구디역;
	private Station 사당역;
	private Station 판교역;

	@BeforeEach
	void setUp() {
		강남역 = new Station("강남역");
		구디역 = new Station("구디역");
		사당역 = new Station("사당역");
		판교역 = new Station("판교역");
	}

	@Test
	@DisplayName("등록된 지하철 구간 정렬 테스트")
	void sortedTest() {
		// given
		Sections sections = new Sections();
		sections.add(Section.of(강남역, 구디역, 10));
		sections.add(Section.of(구디역, 판교역, 5));
		sections.add(Section.of(판교역, 사당역, 3));

		// when
		List<Station> sortedStations = sections.stationsBySorted();

		// then
		assertThat(sortedStations)
			.isNotEmpty()
			.containsExactly(강남역, 구디역, 판교역, 사당역);

	}

	@Test
	@DisplayName("상행, 하행 둘다 없는 경우 테스트")
	void addTestWithoutUpStationAndDownStation() {
		// given
		Sections sections = new Sections();
		sections.add(Section.of(강남역, 구디역, 10));

		// when
		assertThatThrownBy(() -> sections.add(Section.of(판교역, 사당역, 3)))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessageContaining(NOT_EXISTS_STATIONS.message());
	}

	@Test
	@DisplayName("상행, 하행 둘다 등록되어 있는 경우 테스트")
	void addTestWithUpStationAndDownStation() {
		// given
		Sections sections = new Sections();
		sections.add(Section.of(강남역, 구디역, 10));

		// when
		assertThatThrownBy(() -> sections.add(Section.of(구디역, 강남역, 3)))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessageContaining(EXISTS_ALL_STATIONS.message());
	}

	@Test
	@DisplayName("지하철 구간 중간지점 삭제 테스트")
	void removeSectionTest() {
		// given
		Sections sections = new Sections();
		sections.add(Section.of(강남역, 구디역, 10));
		sections.add(Section.of(구디역, 판교역, 5));

		// when
		sections.removeStation(구디역);

		// then
		assertThat(sections.stationsBySorted())
			.isNotEmpty()
			.containsExactly(강남역, 판교역);

	}

	@Test
	@DisplayName("지하철 첫 구간 삭제 테스트")
	void removeFirstSectionTest() {
		// given
		Sections sections = new Sections();
		sections.add(Section.of(강남역, 구디역, 10));
		sections.add(Section.of(구디역, 판교역, 5));

		// when
		sections.removeStation(강남역);

		// then
		assertThat(sections.stationsBySorted())
			.isNotEmpty()
			.containsExactly(구디역, 판교역);

	}

	@Test
	@DisplayName("지하철 마지막 구간 삭제 테스트")
	void removeLastSectionTest() {
		// given
		Sections sections = new Sections();
		sections.add(Section.of(강남역, 구디역, 10));
		sections.add(Section.of(구디역, 판교역, 5));

		// when
		sections.removeStation(판교역);

		// then
		assertThat(sections.stationsBySorted())
			.isNotEmpty()
			.containsExactly(강남역, 구디역);

	}

	@Test
	@DisplayName("지하철 하나뿐인 구간 삭제 테스트")
	void removeOnlyOneSectionTest() {
		// given
		Sections sections = new Sections();
		sections.add(Section.of(강남역, 구디역, 10));

		// when
		assertThatThrownBy(() -> sections.removeStation(강남역))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessageContaining(CANNOT_DELETE_LAST_SECTION.message());
	}

	@Test
	@DisplayName("지하철 없는 구간 삭제 테스트")
	void removeNotExistsSectionTest() {
		// given
		Sections sections = new Sections();
		sections.add(Section.of(강남역, 구디역, 10));
		sections.add(Section.of(구디역, 판교역, 5));

		// when
		assertThatThrownBy(() -> sections.removeStation(사당역))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessageContaining(NOT_EXISTS_STATIONS.message());
	}

}
