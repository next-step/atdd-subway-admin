package nextstep.subway.line.application;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.dto.LineInfoResponse;

@DisplayName("지하철 노선 서비스 관련 기능")
@ExtendWith(MockitoExtension.class)
public class LineServiceTest {
    @Mock
    LineRepository lineRepository;

    @InjectMocks
    LineService lineService;

    @DisplayName("지하철 노선정보 목록을 조회")
    @Test
    void find_allLineInfos() {
        // given
        List<Line> sample = List.of(new Line("신분당선", "bg-red-600"), new Line("2호선", "bg-green-600"));
        
        when(lineRepository.findAll()).thenReturn(sample);

        // when
        List<LineInfoResponse> LineInfoResponses = lineService.findAllForLineInfo();

        // then
        Assertions.assertThat(LineInfoResponses).hasSize(sample.size());

        assertAll(
            () -> Assertions.assertThat(LineInfoResponses.get(0).getName()).isEqualTo(sample.get(0).getName()),
            () -> Assertions.assertThat(LineInfoResponses.get(0).getColor()).isEqualTo(sample.get(0).getColor()),
            () -> Assertions.assertThat(LineInfoResponses.get(0).getStations()).isEqualTo(sample.get(0).getStations())
        );

        assertAll(
            () -> Assertions.assertThat(LineInfoResponses.get(1).getName()).isEqualTo(sample.get(1).getName()),
            () -> Assertions.assertThat(LineInfoResponses.get(1).getColor()).isEqualTo(sample.get(1).getColor()),
            () -> Assertions.assertThat(LineInfoResponses.get(1).getStations()).isEqualTo(sample.get(1).getStations())
        );
    }

    @DisplayName("지하철 노선정보를 조회")
    @Test
    void find_lineInfo() {
        // given
        Line sample = new Line("신분당선", "bg-red-600");
        
        when(lineRepository.findById(anyLong())).thenReturn(Optional.of(sample));

        // when
        LineInfoResponse LineInfoResponses = lineService.findLineInfo(1L);

        // then
        assertAll(
            () -> Assertions.assertThat(LineInfoResponses.getName()).isEqualTo(sample.getName()),
            () -> Assertions.assertThat(LineInfoResponses.getColor()).isEqualTo(sample.getColor()),
            () -> Assertions.assertThat(LineInfoResponses.getStations()).isEqualTo(sample.getStations())
        );
    }

    @DisplayName("지하철 노선정보를 변경")
    @Test
    void update_lineInfo() {
        // given
        Line sample = new Line("신분당선", "bg-red-600");
        
        when(lineRepository.findById(anyLong())).thenReturn(Optional.of(sample));

        // when
        lineService.updateLineInfo(1L, new Line("구분당선", "bg-blue-600"));

        // then
        LineInfoResponse lineResponse = lineService.findLineInfo(1L);

        assertAll(
            () -> Assertions.assertThat(lineResponse.getName()).isEqualTo("구분당선"),
            () -> Assertions.assertThat(lineResponse.getColor()).isEqualTo("bg-blue-600")
        );
    }

    @DisplayName("지하철 노선정보를 삭제")
    @Test
    void delete_line() {
        // given

        // when
        lineService.deleteLineInfo(1L);

        // then
        verify(lineRepository, times(1)).deleteById(anyLong());
    }
}
