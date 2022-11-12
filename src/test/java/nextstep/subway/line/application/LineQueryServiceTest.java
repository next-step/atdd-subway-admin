package nextstep.subway.line.application;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.dto.LineEndStationResponse;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;
import org.junit.jupiter.api.BeforeEach;
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

    @BeforeEach
    void setUp() {
        upStation = stationRepository.save(Station.from("신사역"));
        downStation = stationRepository.save(Station.from("광교역"));
    }

    @Test
    void findAllLines() {
        lineRepository.save(Line.of("신분당선", "red", upStation, downStation));

        List<LineResponse> allLines = lineQueryService.findAllLines();

        assertAll(
                () -> assertThat(allLines).hasSize(1),
                () -> assertThat(allLines.get(0).getName()).isEqualTo("신분당선"),
                () -> assertThat(allLines.get(0).getColor()).isEqualTo("red")
        );
    }

    @Test
    void findLine() {
        Line line = Line.of("신분당선", "red", upStation, downStation);
        lineRepository.save(line);

        LineResponse lineResponse = lineQueryService.findLine(line.getId());

        assertAll(
                () -> assertThat(lineResponse.getId()).isEqualTo(line.getId()),
                () -> assertThat(lineResponse.getName()).isEqualTo("신분당선"),
                () -> assertThat(lineResponse.getColor()).isEqualTo("red"),
                () -> assertThat(lineResponse.getStations()).containsExactlyInAnyOrder(
                        LineEndStationResponse.of(upStation.getId(), upStation.getName()),
                        LineEndStationResponse.of(downStation.getId(), downStation.getName())
                )
        );
    }
}
