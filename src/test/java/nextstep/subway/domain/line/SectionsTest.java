package nextstep.subway.domain.line;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import nextstep.subway.domain.station.Station;
import nextstep.subway.exception.InvalidSectionAddException;

@DisplayName("구간 테스트")
class SectionsTest {

	private Station 강남역;
	private Station 역삼역;
	private Station 선릉역;
	private Line 이호선;

	@BeforeEach
	void setUp() {
		강남역 = Station.from("강남역");
		역삼역 = Station.from("역삼역");
		선릉역 = Station.from("선릉역");
		이호선 = Line.of("2호선", "green", 강남역, 역삼역, 10);
	}

	@DisplayName("구간 생성 테스트")
	@Test
	void createSectionTest() {
		// given
		Section 구간 = new Section(이호선, 강남역, 역삼역, 10);

		// when
		Sections sections = Sections.initialSections(구간);

		// then
		assertThat(sections.getSections()).hasSize(1);
	}

	@DisplayName("모든 역 조회 테스트")
	@Test
	void findAllStationsTest() {
		// given
		Section 구간 = new Section(이호선, 강남역, 역삼역, 10);
		// Section 새로운_구간 = new Section(이호선, 역삼역, 선릉역, 10);

		Sections sections = Sections.initialSections(구간);
		// sections.add(새로운_구간);

		// when
		List<Station> stations = sections.allStations();

		// then
		assertThat(stations).containsExactly(강남역, 역삼역);
	}

	@DisplayName("상행 종점 조회 테스트")
	@Test
	void findFirstUpStationTest() {
		// given
		Section 구간 = new Section(이호선, 강남역, 역삼역, 10);
		Section 새로운_구간 = new Section(이호선, 역삼역, 선릉역, 10);

		Sections sections = Sections.initialSections(구간);
		sections.connect(새로운_구간, Collections.singletonList(구간));

		// when
		Station firstUpStation = sections.firstUpStation();

		// then
		assertThat(firstUpStation).isEqualTo(강남역);
	}

	@DisplayName("하행 종점 조회 테스트")
	@Test
	void findLastDownStationTest() {
		// given
		Section 구간 = new Section(이호선, 강남역, 역삼역, 10);
		Section 새로운_구간 = new Section(이호선, 역삼역, 선릉역, 10);

		Sections sections = Sections.initialSections(구간);
		// sections.add(새로운_구간);
		sections.connect(새로운_구간, Collections.singletonList(구간));

		// when
		Station lastDownStation = sections.lastDownStation();

		// then
		assertThat(lastDownStation).isEqualTo(선릉역);
	}

	@DisplayName("구간 추가 시 상행역, 하행역이 중복이면 예외 발생")
	@Test
	void addSectionWithDuplicateStationTest() {
		// given
		Section 구간 = new Section(이호선, 강남역, 역삼역, 10);
		Section 새로운_구간 = new Section(이호선, 강남역, 역삼역, 10);

		Sections sections = Sections.initialSections(구간);

		// when, then
		assertThatThrownBy(() -> sections.connect(새로운_구간, Collections.singletonList(구간)))
			.isInstanceOf(InvalidSectionAddException.class);
	}

	@DisplayName("구간 추가 시 상행역, 하행역이 모두 존재하지 않으면 예외 발생")
	@Test
	void addSectionWithNotExistsStationTest() {
		// given
		Section 구간 = new Section(이호선, 강남역, 역삼역, 10);
		Station 삼성역 = Station.from("삼성역");
		Section 새로운_구간 = new Section(이호선, 삼성역, 선릉역, 10);

		Sections sections = Sections.initialSections(구간);

		// when, then
		assertThatThrownBy(() -> sections.connect(새로운_구간, Collections.singletonList(구간)))
			.isInstanceOf(InvalidSectionAddException.class);
	}

	@DisplayName("구간 추가 시 상행역이 존재하고 하행역이 존재하지 않으면 구간 추가")
	@Test
	void addSectionWithExistsUpStationTest() {
		// given
		Section 구간 = new Section(이호선, 강남역, 역삼역, 10);
		Section 새로운_구간 = new Section(이호선, 강남역, 선릉역, 5);

		Sections sections = Sections.initialSections(구간);

		// when
		sections.connect(새로운_구간, Collections.singletonList(구간));

		// then
		assertAll(
			() -> assertThat(sections.getSections()).hasSize(2),
			() -> assertThat(sections.allStations()).containsExactly(강남역, 선릉역, 역삼역),
			() -> assertThat(sections.getSections().get(0).getDistance()).isEqualTo(5),
			() -> assertThat(sections.getSections().get(0).getUpStation()).isEqualTo(선릉역),
			() -> assertThat(sections.getSections().get(0).getDownStation()).isEqualTo(역삼역),
			() -> assertThat(sections.getSections().get(1).getDistance()).isEqualTo(5),
			() -> assertThat(sections.getSections().get(1).getUpStation()).isEqualTo(강남역),
			() -> assertThat(sections.getSections().get(1).getDownStation()).isEqualTo(선릉역)
		);

	}

	@DisplayName("구간 추가 시 하행역이 존재하고 상행역이 존재하지 않으면 구간 추가")
	@Test
	void addSectionWithExistsDownStationTest() {
		// given
		Section 구간 = new Section(이호선, 강남역, 역삼역, 10);
		Section 새로운_구간 = new Section(이호선, 선릉역, 역삼역, 5);

		Sections sections = Sections.initialSections(구간);

		// when
		sections.connect(새로운_구간, Collections.singletonList(구간));

		// then
		assertAll(
			() -> assertThat(sections.getSections()).hasSize(2),
			() -> assertThat(sections.allStations()).containsExactly(강남역, 선릉역, 역삼역),
			() -> assertThat(sections.getSections().get(0).getDistance()).isEqualTo(5),
			() -> assertThat(sections.getSections().get(0).getUpStation()).isEqualTo(강남역),
			() -> assertThat(sections.getSections().get(0).getDownStation()).isEqualTo(선릉역),
			() -> assertThat(sections.getSections().get(1).getDistance()).isEqualTo(5),
			() -> assertThat(sections.getSections().get(1).getUpStation()).isEqualTo(선릉역),
			() -> assertThat(sections.getSections().get(1).getDownStation()).isEqualTo(역삼역)
		);
	}

	@DisplayName("구간 추가 시 하행역이 상행 종점이면 구간 추가")
	@Test
	void addSectionWithDownStationIsFirstStationTest() {
		// given
		Section 구간 = new Section(이호선, 강남역, 역삼역, 10);
		Section 새로운_구간 = new Section(이호선, 선릉역, 강남역, 5);

		Sections sections = Sections.initialSections(구간);

		// when
		sections.connect(새로운_구간, Collections.singletonList(구간));

		// then
		assertAll(
			() -> assertThat(sections.getSections()).hasSize(2),
			() -> assertThat(sections.allStations()).containsExactly(선릉역, 강남역, 역삼역),
			() -> assertThat(sections.getSections().get(0).getDistance()).isEqualTo(10),
			() -> assertThat(sections.getSections().get(0).getUpStation()).isEqualTo(강남역),
			() -> assertThat(sections.getSections().get(0).getDownStation()).isEqualTo(역삼역),
			() -> assertThat(sections.getSections().get(1).getDistance()).isEqualTo(5),
			() -> assertThat(sections.getSections().get(1).getUpStation()).isEqualTo(선릉역),
			() -> assertThat(sections.getSections().get(1).getDownStation()).isEqualTo(강남역),
			() -> assertThat(sections.firstUpStation()).isEqualTo(선릉역)
		);
	}

	@DisplayName("구간 추가 시 상행역이 하행 종점이면 구간 추가")
	@Test
	void addSectionWithUpStationIsLastStationTest() {
		// given
		Section 구간 = new Section(이호선, 강남역, 역삼역, 10);
		Section 새로운_구간 = new Section(이호선, 역삼역, 선릉역, 5);

		Sections sections = Sections.initialSections(구간);

		// when
		sections.connect(새로운_구간, Collections.singletonList(구간));

		// then
		assertAll(
			() -> assertThat(sections.getSections()).hasSize(2),
			() -> assertThat(sections.allStations()).containsExactly(강남역, 역삼역, 선릉역),
			() -> assertThat(sections.getSections().get(0).getDistance()).isEqualTo(10),
			() -> assertThat(sections.getSections().get(0).getUpStation()).isEqualTo(강남역),
			() -> assertThat(sections.getSections().get(0).getDownStation()).isEqualTo(역삼역),
			() -> assertThat(sections.getSections().get(1).getDistance()).isEqualTo(5),
			() -> assertThat(sections.getSections().get(1).getUpStation()).isEqualTo(역삼역),
			() -> assertThat(sections.getSections().get(1).getDownStation()).isEqualTo(선릉역),
			() -> assertThat(sections.lastDownStation()).isEqualTo(선릉역)
		);
	}
}