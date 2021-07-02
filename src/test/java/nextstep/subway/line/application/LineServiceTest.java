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

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

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

    @Test
    void findAllLines() {
        //given
        Line line1 = new Line("2호선", "green");
        Line line2 = new Line("3호선", "orange");
        List<Line> expected = Arrays.asList(line1, line2);
        when(lineRepository.findAll())
                .thenReturn(expected);
        //when
        List<LineResponse> actual = lineService.findAllLines();

        //then
        assertThat(actual).containsAll(expected.stream().map(LineResponse::of).collect(Collectors.toList()));
    }

    @Test
    void findLine() {
        //given
        Line line = new Line("2호선", "green");
        when(lineRepository.findById(anyLong())).thenReturn(Optional.of(line));

        //when
        LineResponse lineResponse = lineService.findLine(1L);

        //then
        assertThat(lineResponse.getName()).isEqualTo(line.getName());
        assertThat(lineResponse.getColor()).isEqualTo(line.getColor());
    }

    @Test
    void updateLine() {
        //given
        LineRequest lineRequest = new LineRequest("2호선", "green");
        Line line = new Line("12호선", "green");
        when(lineRepository.findById(any())).thenReturn(Optional.of(line));

        //when
        lineService.updateLine(1L, lineRequest);
        LineResponse lineResponse = lineService.findLine(1L);

        //then
        assertThat(lineResponse.getName()).isEqualTo(lineRequest.getName());
    }

    @Test
    void deleteLine() {
        //given
        Line line = new Line("2호선", "green");
        when(lineRepository.findById(1L)).thenReturn(Optional.of(line));

        //when
        lineService.deleteLine(1L);

        //then
        verify(lineRepository).delete(any(Line.class));
    }
}