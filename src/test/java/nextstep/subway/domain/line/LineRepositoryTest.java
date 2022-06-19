package nextstep.subway.domain.line;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.assertAll;

import javax.persistence.EntityManager;
import nextstep.subway.domain.station.Station;
import nextstep.subway.dto.UpdateLineRequest;
import nextstep.subway.generator.StationGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.TestConstructor;
import org.springframework.test.context.TestConstructor.AutowireMode;

@DataJpaTest
@TestConstructor(autowireMode = AutowireMode.ALL)
@Import(StationGenerator.class)
@DisplayName("Repository:Line")
class LineRepositoryTest {

    private final LineRepository lineRepository;
    private final StationGenerator stationGenerator;
    private final EntityManager entityManager;

    public LineRepositoryTest(
        LineRepository lineRepository,
        StationGenerator stationGenerator,
        EntityManager entityManager
    ) {
        this.lineRepository = lineRepository;
        this.stationGenerator = stationGenerator;
        this.entityManager = entityManager;
    }

    private Station upStation;
    private Station downStation;
    private static final String name = "신분당선";
    private static final String color = "bg-red-600";
    private static final int distance = 10;

    @BeforeEach
    void setUp() {
        upStation = stationGenerator.savedStation(new Station("강남역"));
        downStation = stationGenerator.savedStation(new Station("판교역"));
    }

    @Test
    @DisplayName("지하철 노선 Entity 저장")
    public void save() {
        // Given
        final Line given = new Line(name, color, upStation, downStation, distance);

        // When
        Line actual = lineRepository.save(given);

        // Then
        assertAll(
            () -> assertThat(actual.getId()).isNotNull(),
            () -> assertThat(actual.getColor()).isEqualTo(color),
            () -> assertThat(actual.getUpStation()).isEqualTo(upStation),
            () -> assertThat(actual.getDownStation()).isEqualTo(downStation),
            () -> assertThat(actual.getDistance()).isEqualTo(distance),
            () -> assertThat(actual.getCreatedDate()).isNotNull(),
            () -> assertThat(actual.getUpStation()).isNotNull()
        );
    }

    @Test
    @DisplayName("지하철 노선 중복 생성 시 예외 발생 검증")
    public void asf() {
        // Given
        final Line line = new Line(name, color, upStation, downStation, distance);
        final Line duplicatedLine = new Line(name, color, upStation, downStation, distance);
        lineRepository.save(line);

        // When & Then
        assertThatExceptionOfType(DataIntegrityViolationException.class)
            .isThrownBy(() -> lineRepository.save(duplicatedLine));
    }

    @Test
    @DisplayName("특정 지하철 노선 Entity 조회")
    public void findById() {
        // Given
        final Line given = new Line(name, color, upStation, downStation, distance);
        lineRepository.saveAndFlush(given);

        // When
        Line actual = lineRepository.findById(given.getId())
            .orElseThrow(IllegalArgumentException::new);

        // Then
        assertThat(actual).isSameAs(given);
    }

    @Test
    @DisplayName("특정 지하철 노선 Entity 수정")
    public void updateNameAndColor() {
        // Given
        final UpdateLineRequest updateLineRequest = new UpdateLineRequest("분당선", "bg-yellow-600");

        final Line given = new Line(name, color, upStation, downStation, distance);
        lineRepository.saveAndFlush(given);

        // When
        given.updateColor(updateLineRequest);
        entityManager.flush();

        // Then
        assertAll(
            () -> assertThat(given.getName()).isEqualTo(updateLineRequest.getName()),
            () -> assertThat(given.getColor()).isEqualTo(updateLineRequest.getColor())
        );
    }

    @Test
    @DisplayName("특정 지하철 노선 Entity 삭제")
    public void deleteById() {
        // Given
        final Line given = new Line(name, color, upStation, downStation, distance);
        lineRepository.saveAndFlush(given);

        // When
        lineRepository.deleteById(given.getId());
        entityManager.flush();

        // Then
        assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() -> lineRepository.findById(given.getId())
                .orElseThrow(IllegalArgumentException::new)
            );
    }
}
