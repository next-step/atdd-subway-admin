package nextstep.subway.line.domain;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import nextstep.subway.common.ErrorCode;
import nextstep.subway.line.exception.SectionException;
import nextstep.subway.station.domain.Station;

@DisplayName("구간 일급콜렉션에 관한 단위 테스트")
class SectionsTest {

	private Section section;
	private Sections sections;
	private Line line;
	private Station givenUpStation;
	private Station givenDownStation;
	private Section givenSection;

	@BeforeEach
	void setup() {
		Station upStation = new Station("강남역");
		Station downStation = new Station("광교역");
		line = new Line("신분당선", "red");
		section = new Section(line, upStation, downStation, 10);
		line.addSection(section);

		sections = line.getSections();

		givenUpStation = new Station("강남역");
		givenDownStation = new Station("양재역");
		givenSection = new Section(line, givenUpStation, givenDownStation, 3);
	}

	@DisplayName("구간이 1개만 존재할 때 예외처리 테스트")
	@Test
	void validSectionsSize() {
		//given // when // then
		assertThatThrownBy(() -> {
			sections.validRemoveStation(new Station("test"));
		}).isInstanceOf(SectionException.class)
			.hasMessageContaining(ErrorCode.VALID_CAN_NOT_REMOVE_LAST_STATION.getErrorMessage());
	}

	@DisplayName("삭제하려는 역이 구간내에 존재하지 않을 때 예외처리 테스트")
	@Test
	void validStationInStations() {
		// given
		Station givenUpStation = new Station("강남역");
		Station givenDownStation = new Station("양재역");
		Section givenSection = new Section(line, givenUpStation, givenDownStation, 3);
		sections.add(givenSection);

		Station expectStation = new Station("합정역");

		// when // then
		assertThatThrownBy(() -> {
			sections.validRemoveStation(expectStation);
		}).isInstanceOf(SectionException.class)
			.hasMessageContaining(ErrorCode.VALID_CAN_NOT_REMOVE_NOT_IN_STATIONS.getErrorMessage());
	}

	@DisplayName("삭제하려는 역이 구간의 상, 하행 종점역 사이에 존재하는지 확인하는 메소드 테스트")
	@Test
	void isBetweenStations() {
		// given
		line.addSection(givenSection);
		Sections expectSections = line.getSections();

		// when
		boolean expect = expectSections.isBetweenStations(givenDownStation);

		// then
		assertThat(expect).isTrue();
	}

	@DisplayName("상행 종점역을 삭제하는 테스트")
	@Test
	void deleteUpStation() {
		// given
		line.addSection(givenSection);
		Sections expectSections = line.getSections();

		// when
		expectSections.removeLineStation(givenSection.getUpStation());

		// then
		assertAll(
			() -> assertThat(expectSections.getSectionList()).hasSize(1),
			() -> assertThat(expectSections.getStationList()).doesNotContain(givenSection.getUpStation())
		);
	}

	@DisplayName("하행 종점역을 삭제하는 테스트")
	@Test
	void deleteDownStation() {
		// given
		line.addSection(givenSection);
		Sections expectSections = line.getSections();

		// when
		expectSections.removeLineStation(section.getDownStation());

		// then
		assertAll(
			() -> assertThat(expectSections.getSectionList()).hasSize(1),
			() -> assertThat(expectSections.getStationList()).doesNotContain(section.getDownStation())
		);
	}

	@DisplayName("구간의 상, 하행 종점역 사이의 역을 삭제하는 테스트")
	@Test
	void removeBetweenStation() {
		// given
		line.addSection(givenSection);
		Sections expectSections = line.getSections();

		// when
		expectSections.removeLineStation(section.getUpStation());
		Section expectSection = expectSections.getSectionList().get(0);

		// then
		assertAll(
			() -> assertThat(expectSections.getSectionList()).hasSize(1),
			() -> assertThat(expectSections.getStationList()).doesNotContain(section.getUpStation()),
			() -> assertThat(expectSection.getDistance().getDistance()).isEqualTo(10),
			() -> assertThat(expectSection.getUpStation().getName()).isEqualTo(givenSection.getUpStation().getName()),
			() -> assertThat(expectSection.getDownStation().getName()).isEqualTo(section.getDownStation().getName())
		);
	}
}