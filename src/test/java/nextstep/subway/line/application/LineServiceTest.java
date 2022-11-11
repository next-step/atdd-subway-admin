package nextstep.subway.line.application;

import nextstep.subway.exception.NotFoundException;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.line.exception.LineExceptionCode;
import nextstep.subway.station.application.StationService;
import nextstep.subway.station.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LineServiceTest {

    @Mock
    private LineRepository lineRepository;

    @Mock
    private StationService stationService;

    @InjectMocks
    private LineService lineService;

    private LineRequest request;
    private Station upStation;
    private Station downStation;

    @BeforeEach
    void setUp() {
        request = new LineRequest("2호선", "bg-green-600", 1L, 2L, 10);
        upStation = new Station("강남역");
        downStation = new Station("역삼역");
    }

    @Test
    void 지하철_노선_저장() {
        when(stationService.findById(1L)).thenReturn(upStation);
        when(stationService.findById(2L)).thenReturn(downStation);
        when(lineRepository.save(request.toLineWithSection(upStation, downStation)))
                .thenReturn(request.toLineWithSection(upStation, downStation));

        LineResponse response = lineService.saveLine(request);

        assertAll(
                () -> assertEquals("2호선", response.getName()),
                () -> assertEquals("bg-green-600", response.getColor()),
                () -> assertThat(response.getStations()).hasSize(2)
        );
    }

    @Test
    void id로_지하철_노선_검색() {
        when(lineRepository.findById(1L))
                .thenReturn(Optional.of(new Line("신분당선", "bg-red-600")));

        Line line = lineService.findById(1L);

        assertEquals("신분당선", line.getName());
    }

    @Test
    void id로_지하철_노선_검색시_데이터가_존재하지_않으면_NotFoundException_발생() {
        assertThatThrownBy(() -> {
            lineService.findById(1L);
        }).isInstanceOf(NotFoundException.class)
                .hasMessage(LineExceptionCode.NOT_FOUND_BY_ID.getMessage());
    }

    @Test
    void 전쳬_지하철_노선_목록_검색() {
        Line line = new Line("2호선", "bg-greed-600");
        Line line2 = new Line("신분당선", "bg-red-600");
        when(lineRepository.findAll()).thenReturn(Arrays.asList(line, line2));

        List<LineResponse> responses = lineService.findAllLines();

        assertAll(
                () -> assertThat(responses).hasSize(2),
                () -> assertThat(responses.stream().map(LineResponse::getName))
                        .contains("2호선", "신분당선")
        );
    }
}
