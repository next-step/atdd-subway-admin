package nextstep.subway.line.application;

import nextstep.subway.line.apllication.LineService;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class LineServiceTest {
    @Mock
    private LineRepository lineRepository;

    @InjectMocks
    private LineService lineService;

    @DisplayName("지하철노선 생성한다")
    @Test
    void create() {
        String lineName = "2호선";
        when(lineRepository.save(any(Line.class))).thenReturn(new Line(lineName));

        LineResponse lineResponse = lineService.create(LineRequest.of(lineName));

        assertThat(lineResponse.getName()).isEqualTo(lineName);
    }
}
