package nextstep.subway.line.application;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.station.application.exceptions.AlreadyExistLineException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class LineServiceTest {
    private LineService lineService;

    @Mock
    private LineRepository lineRepository;

    @BeforeEach
    void setup() {
        lineService = new LineService(lineRepository);
    }

    @DisplayName("이미 존재하는 라인을 또 생성 시 예외가 발생한다.")
    @Test
    void createNewLineFailTest() {
        String lineName = "testLine";
        String lineColor = "gold";
        given(lineRepository.existsByNameAndColor(lineName, lineColor)).willReturn(true);

        assertThatThrownBy(() -> lineService.saveLine(new LineRequest(lineName, lineColor)))
                .isInstanceOf(AlreadyExistLineException.class);
    }
}