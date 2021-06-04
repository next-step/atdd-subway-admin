package nextstep.subway.line;

import nextstep.subway.exception.DuplicateDataException;
import nextstep.subway.line.application.LineService;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.dto.LineRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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
}
