package nextstep.subway.line.application;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
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

    @Test
    void saveLine() {
        // given
        LineRequest lineRequest = new LineRequest("3호선", "orange");
        Line line = new Line("3호선", "orange");
        when(lineRepository.save(any(Line.class)))
                .thenReturn(line);

        // when
        LineResponse actual = lineService.saveLine(lineRequest);

        // then
        assertAll(
                () -> assertThat(actual.getName()).isEqualTo(line.getName()),
                () -> assertThat(actual.getColor()).isEqualTo(line.getColor())
        );

    }
}