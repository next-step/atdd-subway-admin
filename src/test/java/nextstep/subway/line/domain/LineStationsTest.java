package nextstep.subway.line.domain;

import static nextstep.subway.line.LineStationFixture.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("노선 역들")
class LineStationsTest {
	@DisplayName("노선 역들을 생성한다.")
	@Test
	void of() {
		// given
		List<LineStation> values = Arrays.asList(
			강남역(),
			역삼역(),
			선릉역(),
			삼성역());

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
			선릉역(),
			삼성역(),
			강남역(),
			역삼역()));

		// when
		List<LineStation> lineStationsInOrder = lineStations.getLineStationsInOrder();

		// then
		assertThat(lineStationsInOrder).isEqualTo(Arrays.asList(
			강남역(),
			역삼역(),
			선릉역(),
			삼성역()));
	}
}
