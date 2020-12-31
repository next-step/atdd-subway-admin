package nextstep.subway.line.application;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
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

    @Test
    void createLine() {
        // given
        LineService lineService = new LineService(lineRepository);
        LineRequest lineRequest = new LineRequest("2호선", "녹색");
        when(lineRepository.save(any())).thenReturn(new Line(1L, "2호선", "녹색"));

        // when
        LineResponse lineResponse = lineService.save(lineRequest);

        // then
        assertThat(lineResponse).isNotNull();
        assertThat(lineResponse.getId()).isNotNull();
    }
}