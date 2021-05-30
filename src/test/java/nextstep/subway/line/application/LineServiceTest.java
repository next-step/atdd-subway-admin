package nextstep.subway.line.application;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LineServiceTest {

    @Mock
    private LineRepository lineRepository;

    private LineService lineService;

    @BeforeEach
    void setUp() {
        lineService = new LineService(lineRepository);
    }

    @DisplayName("라인을 생성요청하면, 생성된 라인을 리턴한다.")
    @Test
    void createLine() {
        // given
        final LineRequest lineRequest = new LineRequest("2호선", "green");
        final Line line = new Line("2호선", "green");
        when(lineRepository.save(any(Line.class)))
                .thenReturn(line);

        // when
        LineResponse actual = lineService.saveLine(lineRequest);

        // then
        assertThat(actual).extracting("name").isEqualTo("2호선");
        assertThat(actual).extracting("color").isEqualTo("green");
    }
}
