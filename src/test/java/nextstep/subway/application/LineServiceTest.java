package nextstep.subway.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.when;

import java.util.Optional;
import nextstep.subway.domain.Distance;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import nextstep.subway.dto.LineRequest;
import nextstep.subway.dto.LineResponse;
import nextstep.subway.dto.StationResponse;
import nextstep.subway.exception.NotFoundException;
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
    LineRequest lineRequest2;
    LineResponse lineResponse;
    Line line;
    Line line2;
    Station upStation;
    Station downStation;
    Section section;

    @BeforeEach
    void setUp() {
        upStation = Station.builder("지하철역")
                .id(1L)
                .build();
        downStation = Station.builder("새로운지하철역")
                .id(2L)
                .build();
        section = Section.builder(upStation, downStation, Distance.valueOf(10))
                .build();
        line = Line.builder("신분당선", "bg-red-600")
                .id(1L)
                .build();
        line.addSection(section);
        line2 = Line.builder("신분당선", "bg-blue-600")
                .id(1L)
                .build();
        line2.addSection(section);
        lineRequest = new LineRequest("신분당선", "bg-red-600", 1L, 2L, 10);
        lineRequest2 = new LineRequest("신분당선", "bg-blue-600", 1L, 2L, 10);
        lineResponse = LineResponse.of(line);
    }

    @DisplayName("노선 저장 테스트")
    @Test
    void saveLine() {
        when(stationRepository.findById(lineRequest.getUpStationId())).thenReturn(Optional.of(upStation));
        when(stationRepository.findById(lineRequest.getDownStationId())).thenReturn(Optional.of(downStation));
        Line line = lineRequest.toLine(upStation, downStation);
        when(lineRepository.save(line)).thenReturn(this.line);
        LineResponse lineResponse = lineService.saveLine(lineRequest);
        assertAll(
                () -> assertThat(lineResponse.getId()).isNotNull(),
                () -> assertThat(lineResponse.getName()).isEqualTo("신분당선"),
                () -> assertThat(lineResponse.getStations().get(0)).isEqualTo(StationResponse.of(upStation))
        );
    }

    @DisplayName("노선에 등록하려는 지하철이 없는 경우 예외가 발생한다.")
    @Test
    void saveLineNotFoundException() {
        when(stationRepository.findById(lineRequest.getUpStationId())).thenReturn(Optional.of(upStation));
        when(stationRepository.findById(lineRequest.getDownStationId())).thenReturn(Optional.empty());
        assertThatThrownBy(() -> lineService.saveLine(lineRequest))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("등록된 지하철역이 없습니다.");
    }

    @DisplayName("노선 변경 테스트")
    @Test
    void updateLine() {
        when(lineRepository.findById(line.id())).thenReturn(Optional.of(line));
        when(stationRepository.findById(lineRequest2.getUpStationId())).thenReturn(Optional.of(upStation));
        when(stationRepository.findById(lineRequest2.getDownStationId())).thenReturn(Optional.of(downStation));
        Line line = Line.builder(lineRequest2.getName(), lineRequest2.getColor())
                .id(1L)
                .build();
        line.addSection(section);

        when(lineRepository.save(line)).thenReturn(this.line2);
        LineResponse lineResponse = lineService.updateLine(line.id(), lineRequest2);
        assertAll(
                () -> assertThat(lineResponse.getId()).isNotNull(),
                () -> assertThat(lineResponse.getName()).isEqualTo("신분당선"),
                () -> assertThat(lineResponse.getColor()).isEqualTo("bg-blue-600"),
                () -> assertThat(lineResponse.getStations().get(0)).isEqualTo(StationResponse.of(upStation))
        );
    }

    @DisplayName("노선 조회시 없을 경우 예외 테스트")
    @Test
    void findLineByIdException() {
        when(lineRepository.findById(1L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> lineService.findLineById(1L))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("등록된 노선이 없습니다.");
    }
}
