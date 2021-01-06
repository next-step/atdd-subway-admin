package nextstep.subway.line.domain;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;

@DataJpaTest
class LineRepositoryTest {
	@Autowired
	private LineRepository lineRepository;
	@Autowired
	private StationRepository stationRepository;

	@DisplayName("DB: Line 저장 테스트")
	@Test
	void saveTest() {
		// given
		Line given = 라인_2호선_생성("강남역", "양재역");

		// when
		Line line = lineRepository.save(given);

		// then
		assertAll(
			() -> assertThat(line).isNotNull(),
			() -> assertThat(line.getId()).isNotNull(),
			() -> assertThat(line.sections()).hasSize(1),
			() -> assertThat(line.stations()).hasSize(2)
		);
	}

	@DisplayName("DB: Line 중복된 노선이름 저장 테스트")
	@Test
	void saveDuplicateNameTest() {
		// given
		라인_2호선_생성("강남역", "양재역");

		// when //then
		assertThatThrownBy(() -> lineRepository.save(라인_2호선_생성("강남역", "양재역"))).isInstanceOf(RuntimeException.class);
	}

	@DisplayName("DB: Line 조회 테스트")
	@Test
	void findLineTest() {
		// given
		Line expected = 라인_2호선_생성("강남역", "양재역");

		// when
		Line actual = lineRepository.findById(expected.getId()).get();

		// then
		assertAll(
			() -> assertThat(actual).isNotNull(),
			() -> assertThat(actual).isEqualTo(expected)
		);
	}

	@DisplayName("DB: Line 조회 실패 테스트")
	@Test
	void findLineFailTest() {
		// given
		Line expected = 라인_2호선_생성("강남역", "양재역");

		// when // then
		assertThatThrownBy(() -> lineRepository.findById(expected.getId() + 1).get())
			.isInstanceOf(RuntimeException.class);
	}

	@DisplayName("DB: Line 삭제 실패 테스트")
	@Test
	void deleteLineTest() {
		// given
		Line expected = 라인_2호선_생성("강남역", "양재역");

		// when // then
		assertThatThrownBy(
			() -> lineRepository.deleteById(expected.getId() + 1)
		).isInstanceOf(RuntimeException.class);
	}

	private Line 라인_2호선_생성(String upStationName, String downStationName) {
		Station upStation = stationRepository.save(new Station(upStationName));
		Station downStation = stationRepository.save(new Station(downStationName));
		return lineRepository.save(new Line("2호선", "green", upStation, downStation, 100));
	}
}
