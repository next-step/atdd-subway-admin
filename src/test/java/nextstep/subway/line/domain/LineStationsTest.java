package nextstep.subway.line.domain;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import nextstep.subway.line.LineStationFixture;
import nextstep.subway.station.StationFixture;

@DisplayName("노선 역들")
class LineStationsTest {
	@DisplayName("노선 역들을 생성한다.")
	@Test
	void of() {
		// given
		List<LineStation> values = Arrays.asList(
			LineStationFixture.강남역(),
			LineStationFixture.역삼역(),
			LineStationFixture.선릉역(),
			LineStationFixture.삼성역());

		// when
		LineStations lineStations = LineStations.of(values);

		// then
		assertAll(
			() -> assertThat(lineStations).isNotNull(),
			() -> assertThat(lineStations.size()).isEqualTo(values.size())
		);
	}

	@DisplayName("노선 역들을 순서대로 가져온다.")
	@Test
	void getStations() {
		// given
		LineStations lineStations = LineStations.of(Arrays.asList(
			LineStationFixture.선릉역(),
			LineStationFixture.삼성역(),
			LineStationFixture.강남역(),
			LineStationFixture.역삼역()));

		// when
		List<LineStation> lineStationsInOrder = lineStations.getLineStationsInOrder();

		// then
		assertThat(lineStationsInOrder).isEqualTo(Arrays.asList(
			LineStationFixture.강남역(),
			LineStationFixture.역삼역(),
			LineStationFixture.선릉역(),
			LineStationFixture.삼성역()));
	}

	@DisplayName("노선 역들이 비어있을 경우, 구간을 등록한다.")
	@Test
	void addSectionOnEmpty() {
		// given
		LineStations lineStations = LineStations.of(new ArrayList<>());

		// when
		lineStations.addSection(Section.of(StationFixture.강남역(), StationFixture.역삼역(), 1));

		// then
		assertThat(lineStations.getStationIdsInOrder()).isEqualTo(
			Stream.of(LineStationFixture.강남역(), LineStationFixture.역삼역())
				.map(LineStation::getStationId)
				.collect(Collectors.toList()));
	}

	@DisplayName("상행역과 하행역 둘 중 하나도 포함되어 있지 않을 때, 구간을 등록할 수 없다.")
	@Test
	void addSectionFailOnBothStationsNotRegistered() {
		// given
		LineStations lineStations = LineStations.of(Arrays.asList(
			LineStationFixture.강남역(),
			LineStationFixture.역삼역()));

		// when & then
		assertThatThrownBy(() -> lineStations.addSection(Section.of(StationFixture.선릉역(), StationFixture.삼성역(), 1)))
			.isInstanceOf(SectionAddFailException.class);
	}

	@DisplayName("상행역과 하행역이 이미 노선에 모두 등록되어 있을 때, 구간을 등록할 수 없다.")
	@Test
	void addSectionFailOnBothStationsAlreadyRegistered() {
		// given
		LineStations lineStations = LineStations.of(Arrays.asList(
			LineStationFixture.강남역(),
			LineStationFixture.역삼역()));

		// when & then
		assertThatThrownBy(() -> lineStations.addSection(Section.of(StationFixture.강남역(), StationFixture.역삼역(), 1)))
			.isInstanceOf(SectionAddFailException.class);
	}
}
