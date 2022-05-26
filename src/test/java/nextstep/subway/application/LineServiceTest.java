package nextstep.subway.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.when;

import java.util.Optional;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.Station;
import nextstep.subway.dto.LineRequest;
import nextstep.subway.dto.LineResponse;
import nextstep.subway.dto.StationResponse;
import nextstep.subway.repository.LineRepository;
import nextstep.subway.repository.StationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class LineServiceTest {
    @Mock
    private LineRepository lineRepository;

    @Mock
    private StationRepository stationRepository;

    @InjectMocks
    private LineService lineService;
    LineRequest lineRequest;
    LineResponse lineResponse;
    Line line;
    Station upStation;
    Station downStation;

    @BeforeEach
    void setUp() {
        upStation = Station.builder("지하철역")
                .id(1L)
                .build();
        downStation = Station.builder("새로운지하철역")
                .id(2L)
                .build();
        line = Line.builder("신분당선", "bg-red-600", 10)
                .id(1L)
                .build()
                .addUpStation(upStation)
                .addDownStation(downStation);
        lineRequest = new LineRequest("신분당선", "bg-red-600", 1L, 2L, 10);
        lineResponse = LineResponse.of(line);
    }

    @DisplayName("노선 저장 테스트")
    @Test
    void saveLine() {
        when(stationRepository.findById(lineRequest.getUpStationId())).thenReturn(Optional.of(upStation));
        when(stationRepository.findById(lineRequest.getDownStationId())).thenReturn(Optional.of(downStation));
        Line line = lineRequest.toLine();
        line.addUpStation(upStation);
        line.addDownStation(downStation);
        when(lineRepository.save(line)).thenReturn(this.line);
        LineResponse lineResponse = lineService.saveLine(lineRequest);
        assertAll(
                () -> assertThat(lineResponse.getId()).isNotNull(),
                () -> assertThat(lineResponse.getName()).isEqualTo("신분당선"),
                () -> assertThat(lineResponse.getStations().get(0)).isEqualTo(StationResponse.of(upStation))
        );
    }
}
