package nextstep.subway.line.application;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.dto.LineStationResponse;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.section.domain.Section;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@SpringBootTest
@Transactional
class LineQueryServiceTest {
    @Autowired
    private LineQueryService lineQueryService;

    @Autowired
    private StationRepository stationRepository;

    @Autowired
    private LineRepository lineRepository;

    private Station upStation;
    private Station downStation;
    private int distance;

    @BeforeEach
    void setUp() {
        upStation = stationRepository.save(Station.from("신사역"));
        downStation = stationRepository.save(Station.from("광교역"));
        distance = 10;
    }

    @DisplayName("지하철 노선 목록 조회")
    @Test
    void findAllLines() {
        lineRepository.save(Line.of("신분당선", "red", Section.of(upStation, downStation, distance)));

        List<LineResponse> allLines = lineQueryService.findAllLines();

        assertAll(
                () -> assertThat(allLines).hasSize(1),
                () -> assertThat(allLines.get(0).getName()).isEqualTo("신분당선"),
                () -> assertThat(allLines.get(0).getColor()).isEqualTo("red")
        );
    }

    @DisplayName("지하철 노선 조회")
    @Test
    void findLine() {
        Line line = Line.of("신분당선", "red", Section.of(upStation, downStation, distance));
        lineRepository.save(line);

        LineResponse lineResponse = lineQueryService.findLine(line.getId());

        assertAll(
                () -> assertThat(lineResponse.getId()).isEqualTo(line.getId()),
                () -> assertThat(lineResponse.getName()).isEqualTo("신분당선"),
                () -> assertThat(lineResponse.getColor()).isEqualTo("red"),
                () -> assertThat(lineResponse.getStations()).containsExactlyInAnyOrder(
                        LineStationResponse.of(upStation.getId(), upStation.getName()),
                        LineStationResponse.of(downStation.getId(), downStation.getName())
                )
        );
    }
}
