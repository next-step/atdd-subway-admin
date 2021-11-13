package nextstep.subway.line.application;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.when;

import java.util.List;

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
}
