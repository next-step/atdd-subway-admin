package nextstep.subway.line.domain;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import nextstep.subway.common.ErrorCode;
import nextstep.subway.line.exception.SectionException;
import nextstep.subway.station.domain.Station;

@DisplayName("구간에 관한 단위 테스트")
class SectionTest {

	private Section section;
	private Station upStation;
	private Line line;
	private Station downStation;

	@BeforeEach
	void setup() {
		upStation = new Station("강남역");
		downStation = new Station("광교역");
		line = new Line("신분당선", "red");
		section = new Section(line, upStation, downStation, 10);

		line.addSection(section);
	}

	@DisplayName("같은 상행 종점인지 확인하는 테스트")
	@Test
	void isSameUpStation() {
		// then
		assertThat(section.isSameUpStation(upStation)).isTrue();
	}

	@DisplayName("같은 하행 종적인지 확인하는 테스트")
	@Test
	void isSameDownStation() {
		// then
		assertThat(section.isSameDownStation(downStation)).isTrue();
	}

	@DisplayName("같은 상, 하행 종점을 가지는 구간 예외처리 테스트")
	@Test
	void validSameStation() {
		// given
		Station expectUpStation = new Station("강남역");
		Station expectDownStation = new Station("광교역");
		Section expectSection = new Section(line, expectUpStation, expectDownStation, 10);

		// when // then
		assertThatThrownBy(() -> {
			section.validSection(expectSection);
		}).isInstanceOf(SectionException.class)
			.hasMessageContaining(ErrorCode.VALID_SAME_STATION_ERROR.getErrorMessage());
	}

	@DisplayName("상, 하행 종점 중 하나라도 없는 구간에 대한 예외처리 테스트")
	@Test
	void validNotInStations() {
		// given
		Station expectUpStation = new Station("성수역");
		Station expectDownStation = new Station("왕십리역");
		Section expectSection = new Section(line, expectUpStation, expectDownStation, 10);

		// when // then
		assertThatThrownBy(() -> {
			section.validNotInStations(expectSection);
		}).isInstanceOf(SectionException.class)
			.hasMessageContaining(ErrorCode.VALID_NOT_IN_STATIONS_ERROR.getErrorMessage());
	}

	@DisplayName("상행 종점이 구간 상, 하행 종점 사이에 존재할 때 구간을 재설정하는 테스트")
	@Test
	void upStationBetweenStations() {
		// given
		Station expectUpStation = new Station("강남역");
		Station expectDownStation = new Station("양재역");
		Section expectSection = new Section(line, expectUpStation, expectDownStation, 2);

		// when
		section.reSettingSection(expectSection);

		// then
		assertAll(
			() -> assertThat(section.getDistance().getDistance())
				.isEqualTo(10 - expectSection.getDistance().getDistance()),
			() -> assertThat(section.getUpStation()).isEqualTo(expectSection.getDownStation())
		);
	}

	@DisplayName("하행 종점이 구간 상, 하행 종점 사이에 존재할 때 구간을 재설정하는 테스트")
	@Test
	void downStationBetweenStations() {
		// given
		Station expectUpStation = new Station("정자역");
		Station expectDownStation = new Station("광교역");
		Section expectSection = new Section(line, expectUpStation, expectDownStation, 3);

		// when
		section.reSettingSection(expectSection);

		// then
		assertAll(
			() -> assertThat(section.getDistance().getDistance())
				.isEqualTo(10 - expectSection.getDistance().getDistance()),
			() -> assertThat(section.getDownStation()).isEqualTo(expectSection.getUpStation())
		);
	}
}