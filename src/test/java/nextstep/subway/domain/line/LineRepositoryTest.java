package nextstep.subway.domain.line;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import javax.persistence.EntityManager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestConstructor;

import nextstep.subway.domain.station.Station;
import nextstep.subway.dto.line.LineUpdateRequest;
import nextstep.subway.generator.StationGenerator;

@DataJpaTest
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@Import(StationGenerator.class)
@DisplayName("Line Repository 테스트")
class LineRepositoryTest {

	private final LineRepository lineRepository;
	private final EntityManager entityManager;
	private final StationGenerator stationGenerator;

	public LineRepositoryTest(
		LineRepository lineRepository,
		EntityManager entityManager,
		StationGenerator stationGenerator
	) {
		this.lineRepository = lineRepository;
		this.entityManager = entityManager;
		this.stationGenerator = stationGenerator;
	}

	private Station upStation;
	private Station downStation;
	private static final String NAME = "2호선";
	private static final String COLOR = "green";
	private static final int DISTANCE = 10;

	@BeforeEach
	void setUp() {
		upStation = stationGenerator.savedStation("강남역");
		downStation = stationGenerator.savedStation("역삼역");
	}

	@Test
	@DisplayName("노선 저장 테스트")
	void saveTest() {
		// given
		Line line = Line.of(
			NAME,
			COLOR,
			upStation,
			downStation,
			DISTANCE
		);

		// when
		Line actual = lineRepository.save(line);

		// then
		assertAll(
			() -> assertThat(actual).isNotNull(),
			() -> assertThat(actual.getId()).isNotNull(),
			() -> assertThat(actual.getName()).isEqualTo(NAME),
			() -> assertThat(actual.getColor()).isEqualTo(COLOR),
			() -> assertThat(actual.getUpStation()).isEqualTo(upStation),
			() -> assertThat(actual.getDownStation()).isEqualTo(downStation),
			() -> assertThat(actual.getDistance()).isEqualTo(DISTANCE)
		);
	}

	@Test
	@DisplayName("노선 조회 테스트")
	void findTest() {
		// given
		Line line = Line.of(
			NAME,
			COLOR,
			upStation,
			downStation,
			DISTANCE
		);
		Line savedLine = lineRepository.save(line);

		// when
		Line actual = lineRepository.findById(savedLine.getId())
			.orElseThrow(IllegalArgumentException::new);

		// then
		assertThat(actual).isNotNull()
			.isSameAs(savedLine);
	}

	@Test
	@DisplayName("노선 수정 테스트")
	void updateLineTest() {
		// given
		Line line = Line.of(
			NAME,
			COLOR,
			upStation,
			downStation,
			DISTANCE
		);
		Line savedLine = lineRepository.save(line);

		// when
		Line actual = lineRepository.findById(savedLine.getId())
			.orElseThrow(IllegalArgumentException::new);
		LineUpdateRequest request = LineUpdateRequest.of("3호선", "yellow");
		actual.updateLine(request);
		entityManager.flush();

		// then
		assertThat(actual).isNotNull()
			.isSameAs(savedLine);
		assertThat(actual.getName()).isEqualTo("3호선");
		assertThat(actual.getColor()).isEqualTo("yellow");
	}

	@Test
	@DisplayName("노선 삭제 테스트")
	void deleteLineTest() {
		// given
		Line line = Line.of(
			NAME,
			COLOR,
			upStation,
			downStation,
			DISTANCE
		);
		Line savedLine = lineRepository.save(line);

		// when
		Line actual = lineRepository.findById(savedLine.getId())
			.orElseThrow(IllegalArgumentException::new);
		lineRepository.delete(actual);

		// then
		assertThat(lineRepository.findById(savedLine.getId())).isEmpty();
	}
}