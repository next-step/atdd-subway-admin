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

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LineServiceTest {

    @Mock
    private LineRepository lineRepository;

    private LineService lineService;

    private LineRequest lineRequest;
    private Line line;

    @BeforeEach
    void setUp() {
        lineService = new LineService(lineRepository);
        line = new Line("2호선", "green");
        lineRequest = new LineRequest("2호선", "green");
    }

    @DisplayName("노선을 생성요청하면, 생성된 노선을 리턴한다.")
    @Test
    void createLineWithValidLine() {
        // given
        when(lineRepository.save(any(Line.class)))
                .thenReturn(line);

        // when
        final LineResponse actual = lineService.saveLine(lineRequest);

        // then
        assertThat(actual).extracting("name").isEqualTo("2호선");
        assertThat(actual).extracting("color").isEqualTo("green");
    }

    @DisplayName("이미 존재하는 노선에 동일 노선 생성시, 예외를 던진다.")
    @Test
    void createLineWithDuplicatedLine() {
        // given
        when(lineRepository.findByName(anyString()))
                .thenReturn(Optional.of(line));

        // then
        assertThatThrownBy(() -> lineService.saveLine(lineRequest))
                .isInstanceOf(LineDuplicatedException.class);
    }

    @DisplayName("모든 노선을 리턴한다.")
    @Test
    void getLines() {
        when(lineRepository.findAll())
                .thenReturn(Collections.singletonList(line));

        List<LineResponse> lines = lineService.getLines();

        assertThat(lines).containsExactly(LineResponse.of(line));
    }
}
