package nextstep.subway.line.domain;

import nextstep.subway.section.domain.Section;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DataJpaTest
class LineRepositoryTest {

    @Autowired
    private LineRepository lineRepository;

    @Autowired
    private StationRepository stationRepository;

    private Station upStation;
    private Station downStation;
    private int distance;

    @BeforeEach
    void setUp() {
        upStation = stationRepository.save(Station.from("신사역"));
        downStation = stationRepository.save(Station.from("광교역"));
        distance = 10;
    }

    @DisplayName("자히철 노선 생성")
    @Test
    void createLine() {
        Line line = Line.of("신분당선", "bg-red-500", Section.of(upStation, downStation, distance));

        Line savedLine = lineRepository.save(line);

        assertAll(
                () -> assertThat(savedLine).isNotNull(),
                () -> assertThat(savedLine.getId()).isEqualTo(line.getId())
        );
    }

    @DisplayName("지하철 노선 목록 조회")
    @Test
    void showLines() {
        Line line = Line.of("신분당선", "bg-red-500", Section.of(upStation, downStation, distance));
        lineRepository.save(line);

        List<Line> lines = lineRepository.findAll();

        Assertions.assertThat(lines).contains(line);
    }

    @DisplayName("지하철 노선 이름이 중복 시 지하철 노선 생성 예외발생")
    @Test
    void duplicateLineName() {
        Line line = Line.of("신분당선", "bg-red-500", Section.of(upStation, downStation, distance));
        lineRepository.save(line);

        Line duplicateLine = Line.of("신분당선", "bg-yellow-500", Section.of(upStation, downStation, distance));

        Assertions.assertThatThrownBy(() -> lineRepository.save(duplicateLine))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @DisplayName("지하철 노선 색상이 중복 시 지하철 노선 생성 예외발생")
    @Test
    void duplicateLineColor() {
        Line line = Line.of("신분당선", "bg-red-500", Section.of(upStation, downStation, distance));
        lineRepository.save(line);

        Line duplicateLine = Line.of("분당선", "bg-red-500", Section.of(upStation, downStation, distance));

        Assertions.assertThatThrownBy(() -> lineRepository.save(duplicateLine))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @DisplayName("지하철 노선 조회")
    @Test
    void showLine() {
        Line line = Line.of("신분당선", "bg-red-500", Section.of(upStation, downStation, distance));
        lineRepository.save(line);

        Optional<Line> findLine = lineRepository.findById(line.getId());

        Assertions.assertThat(findLine).isPresent();
    }

    @DisplayName("지하철 노선 수정")
    @Test
    void updateLine() {
        Line line = Line.of("신분당선", "bg-red-500", Section.of(upStation, downStation, distance));
        lineRepository.save(line);

        Line updateLine =
                lineRepository.save(Line.of(line.getId(), "신분당선2", "bg-red-600", Section.of(upStation, downStation, distance)));

        Optional<Line> findLine = lineRepository.findById(updateLine.getId());

        assertAll(
                () -> assertThat(findLine).isPresent(),
                () -> assertThat(findLine).contains(
                        Line.of(line.getId(), "신분당선2", "bg-red-600", Section.of(upStation, downStation, distance))
                )
        );
    }

    @DisplayName("지하철 노선 삭제")
    @Test
    void deleteLine() {
        Line line = Line.of("신분당선", "bg-red-500", Section.of(upStation, downStation, distance));
        lineRepository.save(line);

        lineRepository.deleteById(line.getId());

        Optional<Line> findLine = lineRepository.findById(line.getId());

        Assertions.assertThat(findLine).isNotPresent();
    }
}
