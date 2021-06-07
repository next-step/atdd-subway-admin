package nextstep.subway.line.application;

import nextstep.subway.line.application.exception.LineDuplicatedException;
import nextstep.subway.line.application.exception.LineNotFoundException;
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

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LineServiceTest {
    private LineService lineService;

    @Mock
    private LineRepository lineRepository;
    private LineRequest lineRequest1;
    private LineRequest lineRequest2;
    private Line line1;
    private Line line2;

    @BeforeEach
    void setUp() {
        lineService = new LineService(lineRepository);
        lineRequest1 = new LineRequest("1호선", "blue");
        lineRequest2 = new LineRequest("2호선", "green");
        line1 = new Line("1호선", "blue");
        line2 = new Line("2호선", "green");
    }

    @DisplayName("요청한 지하철 노선을 저장하고 저장된 지하철 노선을 리턴한다.")
    @Test
    void saveLine() {
        //given
        when(lineRepository.save(any(Line.class))).thenReturn(line1);

        //when
        LineResponse actual = lineService.saveLine(lineRequest1);

        //then
        assertThat(actual.getName()).isEqualTo("1호선");
        assertThat(actual.getColor()).isEqualTo("blue");
    }

    @DisplayName("요청한 지하철 노선 이름이 이미 존재하면 예외를 발생시킨다.")
    @Test
    void saveLineDuplicatedException() {
        //given
        when(lineRepository.findByName(anyString())).thenReturn(Optional.of(line1));

        //when
        assertThatThrownBy(() -> lineService.saveLine(lineRequest1))
                .isInstanceOf(LineDuplicatedException.class) // then
                .hasMessage(LineService.LINE_DUPLICATED_EXCEPTION_MESSAGE);
    }

    @DisplayName("지하철 목록 조회를 하면 존재하는 모든 노선들을 리턴한다.")
    @Test
    void findAllLines() {
        //given
        when(lineRepository.findAll()).thenReturn(Arrays.asList(line1, line2));

        //when
        List<LineResponse> actual = lineService.findAllLines();

        //then
        assertThat(actual).containsAll(Arrays.asList(LineResponse.of(line1), LineResponse.of(line2)));
    }

    @DisplayName("노선 ID를 요청하면 ID에 맞는 노선을 리턴한다.")
    @Test
    void findLine() {
        //given
        when(lineRepository.findById(anyLong())).thenReturn(Optional.of(line1));

        //when
        LineResponse actual = lineService.findLine(anyLong());

        //then
        assertThat(actual.getName()).isEqualTo(line1.getName());
        assertThat(actual.getColor()).isEqualTo(line1.getColor());
    }

    @DisplayName("요청한 노선 ID가 존재하지 않는다면 예외를 발생시킨다.")
    @Test
    void findLineNotFoundException() {
        //given
        when(lineRepository.findById(anyLong())).thenReturn(Optional.empty());

        //when
        assertThatThrownBy(() -> lineService.findLine(anyLong()))
                .isInstanceOf(LineNotFoundException.class)
                .hasMessage(LineService.LINE_NOT_FOUND_EXCEPTION_MESSAGE);
    }

    @DisplayName("요청한 ID를 새로운 이름과 색깔로 대체한다.")
    @Test
    void update() {
        //given
        when(lineRepository.findById(anyLong())).thenReturn(Optional.of(line1));

        //when
        lineService.updateLine(anyLong(), lineRequest2);

        //then
        assertThat(line1.getName()).isEqualTo(lineRequest2.getName());
        assertThat(line1.getColor()).isEqualTo(lineRequest2.getColor());
    }


}