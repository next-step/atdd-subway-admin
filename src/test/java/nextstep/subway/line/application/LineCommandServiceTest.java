package nextstep.subway.line.application;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.dto.LineEndStationResponse;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.line.dto.LineUpdateRequest;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@SpringBootTest
@Transactional
class LineCommandServiceTest {
    @Autowired
    private LineCommandService lineCommandService;

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
    void saveLine() {
        LineRequest lineRequest =
                LineRequest.of("신분당선", "red", upStation.getId(), downStation.getId(), 10);

        LineResponse lineResponse = lineCommandService.saveLine(lineRequest);

        assertAll(
                () -> assertThat(lineResponse.getId()).isNotNull(),
                () -> assertThat(lineResponse.getName()).isEqualTo("신분당선"),
                () -> assertThat(lineResponse.getColor()).isEqualTo("red"),
                () -> assertThat(lineResponse.getStations()).containsExactlyInAnyOrder(
                        LineEndStationResponse.of(upStation.getId(), upStation.getName()),
                        LineEndStationResponse.of(downStation.getId(), downStation.getName())
                )
        );
    }

    @Test
    void updateLine() {
        LineRequest lineRequest =
                LineRequest.of("신분당선", "red", upStation.getId(), downStation.getId(), 10);
        LineResponse lineResponse = lineCommandService.saveLine(lineRequest);

        LineUpdateRequest lineUpdateRequest = LineUpdateRequest.of("분당선", "yellow");
        lineCommandService.updateLine(lineResponse.getId(), lineUpdateRequest);

        Optional<Line> findLine = lineRepository.findById(lineResponse.getId());

        assertAll(
                () -> assertThat(findLine).isPresent(),
                () -> assertThat(findLine.get().getName()).isEqualTo("분당선"),
                () -> assertThat(findLine.get().getColor()).isEqualTo("yellow")
        );
    }

    @Test
    void deleteLine() {
        LineRequest lineRequest =
                LineRequest.of("신분당선", "red", upStation.getId(), downStation.getId(), 10);
        LineResponse lineResponse = lineCommandService.saveLine(lineRequest);

        lineCommandService.deleteLine(lineResponse.getId());

        Optional<Line> findLine = lineRepository.findById(lineResponse.getId());
        assertThat(findLine).isNotPresent();
    }
}
