package nextstep.subway.application;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.StationRepository;
import nextstep.subway.dto.CreateLineDto;
import nextstep.subway.dto.LineResponse;
import nextstep.subway.dto.StationResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.AdditionalAnswers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@DisplayName("노선 서비스 테스트")
@ExtendWith(MockitoExtension.class)
class LineServiceTest {

    @Mock
    private LineRepository lineRepository;

    @Mock
    private StationRepository stationRepository;

    @InjectMocks
    private LineService lineService;

    @DisplayName("노선 등록")
    @Test
    void register() {
        String name = "신분당선";
        String color = "Red";
        Station 강남역 = new Station(1L, "강남역");
        Station 판교역 = new Station(2L, "판교역");
        CreateLineDto dto = new CreateLineDto(name, color, 강남역.getId(), 판교역.getId(), 10);

        when(stationRepository.findById(1L)).thenReturn(Optional.of(강남역));
        when(stationRepository.findById(2L)).thenReturn(Optional.of(판교역));
        when(lineRepository.save(any(Line.class))).then(AdditionalAnswers.returnsFirstArg());

        LineResponse response = lineService.register(dto);
        assertAll(
                () -> assertThat(name).isEqualTo(response.getName()),
                () -> assertThat(color).isEqualTo(response.getColor()),
                () -> {
                    List<String> stationNames = response.getStations()
                            .stream()
                            .map(StationResponse::getName)
                            .collect(Collectors.toList());
                    assertThat(stationNames).containsExactly(강남역.getName(), 판교역.getName());
                }
        );
    }

    @DisplayName("등록된 모든 노선 조회")
    @Test
    void fetchAllLines() {
        Line 신분당선 = new Line("신분당선", "RED");
        Line 분당선 = new Line("분당선", "YELLOW");
        List<Line> lines = Arrays.asList(신분당선, 분당선);

        when(lineRepository.findAll()).thenReturn(lines);

        List<LineResponse> responses = lineService.fetchAllLines();
        assertThat(responses.size()).isEqualTo(lines.size());
    }

    @DisplayName("등록된 노선 조회")
    @Test
    void fetchLine() {
        Line 신분당선 = new Line(1L, "신분당선", "RED");

        when(lineRepository.findById(1L)).thenReturn(Optional.of(신분당선));

        LineResponse lineResponse = lineService.fetchLine(신분당선.getId());
        assertAll(
                () -> assertThat(lineResponse.getId()).isEqualTo(신분당선.getId()),
                () -> assertThat(lineResponse.getName()).isEqualTo(신분당선.getName()),
                () -> assertThat(lineResponse.getColor()).isEqualTo(신분당선.getColor()),
                () -> assertThat(lineResponse.getStations().size()).isEqualTo(0)
        );
    }
}