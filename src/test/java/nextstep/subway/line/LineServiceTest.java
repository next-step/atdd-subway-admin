package nextstep.subway.line;

import nextstep.subway.exception.DuplicateDataException;
import nextstep.subway.exception.NoSuchDataException;
import nextstep.subway.line.application.LineService;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LinesSubResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class LineServiceTest {
    @Mock
    LineRepository lineRepository;

    @InjectMocks
    LineService lineService;

    @DisplayName("노선이름 중복검사: 중복시 예외발생")
    @Test
    public void 노선이름_중복검사_중복시_예외발생() throws Exception {
        //given
        LineRequest lineRequest = new LineRequest("testName", "testColor");
        when(lineRepository.existsByName("testName")).thenReturn(true);

        //when
        assertThatThrownBy(() -> lineService.validateDuplicatedName(lineRequest))
                .isInstanceOf(DuplicateDataException.class);
    }

    @DisplayName("노선 조회")
    @Test
    public void 노선조회시_노선확인() throws Exception {
        //given
        Long lineId = 1L;
        Line line = new Line(lineId, "testName", "testColor");
        when(lineRepository.findById(1L)).thenReturn(Optional.ofNullable(line));

        //when
        LinesSubResponse linesSubResponse = lineService.readLine(1L);

        //then
        assertThat(linesSubResponse.getName()).isEqualTo("testName");
    }

    @DisplayName("존재하지 않는 노선 ID로 조회시 예외")
    @Test
    public void 존재하지않는노선아이디로_조회시_예외발생() throws Exception {
        //given
        Long lineId = 1L;
        when(lineRepository.findById(1L)).thenReturn(Optional.ofNullable(null));

        //when
        //then
        assertThatThrownBy(() -> lineService.readLine(1L)).isInstanceOf(NoSuchDataException.class);
    }
}
