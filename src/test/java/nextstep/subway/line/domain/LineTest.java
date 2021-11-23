package nextstep.subway.line.domain;

import nextstep.subway.line.LineFixture;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("노선 도메인 관련 기능")
@DataJpaTest
class LineTest {
    @Autowired
    private LineRepository lineRepository;

    @Autowired
    private StationRepository stationRepository;

    private Line line;

    @BeforeEach
    void setUp() {
        Station upStation = stationRepository.save(new Station("강남역"));
        Station downStation = stationRepository.save(new Station("역삼역"));
        line = lineRepository.save(LineFixture.of("2호선", "green", upStation, downStation, 9));
    }

    @DisplayName("노선을 생성한다.")
    @Test
    void createLine() {
        // then
        assertAll(
                () -> assertThat(line.getId()).isNotNull(),
                () -> assertThat(line.getName()).isEqualTo("2호선"),
                () -> assertThat(line.getColor()).isEqualTo("green")
        );
    }

    @DisplayName("노선을 수정한다.")
    @Test
    void updateLine() {
        // when
        final Line updateLine = lineRepository.save(new Line("3호선", "orange"));
        line.update(updateLine);

        // then
        assertAll(
                () -> assertThat(line.getName()).isEqualTo("3호선"),
                () -> assertThat(line.getColor()).isEqualTo("orange")
        );
        lineRepository.deleteById(line.getId()); // for section cascade
    }

    @DisplayName("노선을 삭제한다.")
    @Test
    void deleteLine() {
        // when
        lineRepository.deleteById(line.getId());

        // then
        assertThat(lineRepository.findAll().size()).isZero();
    }

    @AfterEach
    void tearDown() {
        stationRepository.flush();
        lineRepository.flush();
    }
}