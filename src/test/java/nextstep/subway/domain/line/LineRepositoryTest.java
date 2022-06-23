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

    private Line 신분당선;
    private Station 논현역;
    private Station 정자역;
    private static final String 노선명 = "신분당선";
    private static final String 노선색상 = "bg-red-600";
    private static final int 구간거리 = 10;

    @BeforeEach
    void setUp() {
        논현역 = stationGenerator.savedStation(new Station("논현역"));
        정자역 = stationGenerator.savedStation(new Station("정자역"));
    }

    @Test
    @DisplayName("지하철 노선 Entity 저장")
    public void save() {
        // Given
        final Line given = new Line(노선명, 노선색상, 논현역, 정자역, 구간거리);

        // When
        Line actual = lineRepository.save(given);

        // Then
        assertAll(
            () -> assertThat(actual.getId()).isNotNull(),
            () -> assertThat(actual.getColor()).isEqualTo(노선색상),
            () -> assertThat(actual.getFinalUpStation()).isEqualTo(논현역),
            () -> assertThat(actual.getFinalDownStation()).isEqualTo(정자역),
            () -> assertThat(actual.getTotalDistance()).isEqualTo(구간거리),
            () -> assertThat(actual.getCreatedDate()).isNotNull(),
            () -> assertThat(actual.getModifiedDate()).isNotNull()
        );
    }

    @Test
    @DisplayName("지하철 노선 중복 생성 시 예외 발생 검증")
    public void throwException_WhenSaveDuplicatedLine() {
        // Given
        final Line line = new Line(노선명, 노선색상, 논현역, 정자역, 구간거리);
        final Line duplicatedLine = new Line(노선명, 노선색상, 논현역, 정자역, 구간거리);
        lineRepository.save(line);

        // When & Then
        assertThatExceptionOfType(DataIntegrityViolationException.class)
            .isThrownBy(() -> lineRepository.save(duplicatedLine));
    }

    @Test
    @DisplayName("특정 지하철 노선 Entity 조회")
    public void findById() {
        // Given
        final Line given = new Line(노선명, 노선색상, 논현역, 정자역, 구간거리);
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

        final Line given = new Line(노선명, 노선색상, 논현역, 정자역, 구간거리);
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
        final Line given = new Line(노선명, 노선색상, 논현역, 정자역, 구간거리);
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

    @Test
    @DisplayName("변경 감지에 의한 신규 상행종점역 구간 추가")
    public void addFinalUpSection() {
        // Given
        신분당선 = lineRepository.saveAndFlush(new Line(노선명, 노선색상, 논현역, 정자역, 구간거리));

        final Station 신사역 = stationGenerator.savedStation(new Station("신사역"));
        final int 구간_길이 = 5;

        final Section 신규_상행종점역_구간 = Section.of(신분당선, 신사역, 논현역, 구간_길이);

        // When
        신분당선.addSection(신규_상행종점역_구간);
        entityManager.flush();

        // Then
        assertAll(
            () -> assertThat(신분당선.getFinalUpStation()).as("노선의 상행종점역 조회").isEqualTo(신사역),
            () -> assertThat(신분당선.getFinalDownStation()).as("노선의 하행종점역 조회").isEqualTo(정자역),
            () -> assertThat(신분당선.getFinalUpSection()).isEqualTo(신규_상행종점역_구간),
            () -> assertThat(신분당선.getFinalUpSection().getId()).as("영속성 전이 여부 검증").isNotNull(),
            () -> assertThat(신분당선.getFinalDownSection().getId()).as("영속성 전이 여부 검증").isNotNull(),
            () -> assertThat(신분당선.getAllSections()).hasSize(2),
            () -> assertThat(신분당선.getAllStations())
                .as("노선에 포함된 정렬된 지하철 역 목록 조회")
                .containsExactly(신사역, 논현역, 정자역)
        );
    }

    @Test
    @DisplayName("변경 감지에 의한 신규 하행종점역 구간 추가")
    public void addFinalDownSection() {
        // Given
        신분당선 = lineRepository.saveAndFlush(new Line(노선명, 노선색상, 논현역, 정자역, 구간거리));

        final Station 미금역 = stationGenerator.savedStation(new Station("미금역"));
        final int 구간_길이 = 5;

        final Section 신규_하행종점역_구간 = Section.of(신분당선, 정자역, 미금역, 구간_길이);

        // When
        신분당선.addSection(신규_하행종점역_구간);
        entityManager.flush();

        // Then
        assertAll(
            () -> assertThat(신분당선.getFinalUpStation()).as("노선의 상행종점역 조회").isEqualTo(논현역),
            () -> assertThat(신분당선.getFinalDownStation()).as("노선의 하행종점역 조회").isEqualTo(미금역),
            () -> assertThat(신분당선.getFinalDownSection()).isEqualTo(신규_하행종점역_구간),
            () -> assertThat(신분당선.getFinalUpSection().getId()).as("영속성 전이 여부 검증").isNotNull(),
            () -> assertThat(신분당선.getFinalDownSection().getId()).as("영속성 전이 여부 검증").isNotNull(),
            () -> assertThat(신분당선.getAllSections()).hasSize(2),
            () -> assertThat(신분당선.getAllStations())
                .as("노선에 포함된 정렬된 지하철 역 목록 조회")
                .containsExactly(논현역, 정자역, 미금역)
        );
    }

    @Test
    @DisplayName("신규 구간 추가")
    public void addNewSection() {
        // Given
        신분당선 = lineRepository.saveAndFlush(new Line(노선명, 노선색상, 논현역, 정자역, 구간거리));

        final Station 신논현역 = stationGenerator.savedStation(new Station("신논현역"));
        final int 구간_길이 = 5;

        final Section 신규_구간 = Section.of(신분당선, 논현역, 신논현역, 구간_길이);

        // When
        신분당선.addSection(신규_구간);
        entityManager.flush();

        // Then
        assertAll(
            () -> assertThat(신분당선.getFinalUpStation()).as("노선의 상행종점역 조회").isEqualTo(논현역),
            () -> assertThat(신분당선.getFinalDownStation()).as("노선의 하행종점역 조회").isEqualTo(정자역),
            () -> assertThat(신분당선.getFinalUpSection().getId()).as("영속성 전이 여부 검증").isNotNull(),
            () -> assertThat(신분당선.getFinalDownSection().getId()).as("영속성 전이 여부 검증").isNotNull(),
            () -> assertThat(신분당선.getAllSections()).hasSize(2),
            () -> assertThat(신분당선.getAllStations())
                .as("노선에 포함된 정렬된 지하철 역 목록 조회")
                .containsExactly(논현역, 신논현역, 정자역)
        );
    }
}
