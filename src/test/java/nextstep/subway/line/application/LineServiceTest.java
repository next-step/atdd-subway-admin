package nextstep.subway.line.application;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.station.application.exceptions.AlreadyExistLineException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
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

    @DisplayName("등록된 라인 목록을 조회할 수 있다.")
    @ParameterizedTest
    @MethodSource("getAllLinesTestResource")
    void getAllLinesTest(List<Line> lines, int expectedSize) {
        given(lineRepository.findAll()).willReturn(lines);

        List<LineResponse> lineResponses = lineService.getAllLines();

        assertThat(lineResponses).hasSize(expectedSize);
    }
    public static Stream<Arguments> getAllLinesTestResource() {
        return Stream.of(
                Arguments.of(
                        Arrays.asList(new Line("1호선", "파란색"), new Line("2호선", "초록색")),
                        2
                ),
                Arguments.of(
                        new ArrayList<Line>(),
                        0
                )
        );
    }
}