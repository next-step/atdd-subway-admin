package nextstep.subway.line.application;

import nextstep.subway.line.application.exceptions.LineNotFoundException;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineFixtures;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

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

    @DisplayName("등록된 특정 라인을 조회할 수 있다.")
    @Test
    void getLineTest() {
        Long lineId = 1L;
        given(lineRepository.findById(lineId)).willReturn(Optional.of(LineFixtures.ID1_LINE));

        LineResponse response = lineService.getLine(lineId);

        assertThat(response.getId()).isEqualTo(lineId);
    }

    @DisplayName("등록되지 않은 특정 라인을 조회 시도 시 예외 발생")
    @Test
    void getLineFailTest() {
        Long lineId = 1L;

        assertThatThrownBy(() -> lineService.getLine(lineId)).isInstanceOf(LineNotFoundException.class);
    }

    @DisplayName("특정 라인의 정보를 수정할 수 있다.")
    @Test
    void updateLineTest() {
        Long lineId = 1L;
        String changeName = "비 내리는 호남선";
        String changeColor = "남행열차색";
        Line mockLine = new Line("원본", "원본");

        given(lineRepository.findById(lineId)).willReturn(Optional.of(mockLine));

        Line updatedLine = lineService.updateLine(lineId, changeName, changeColor);

        assertThat(updatedLine.getName()).isEqualTo(changeName);
        assertThat(updatedLine.getColor()).isEqualTo(changeColor);
    }

    @DisplayName("존재하지 않는 특정 라인의 정보를 수정 시도할 경우 예외가 발생한다.")
    @Test
    void updateLineWhenLineNotExistTest() {
        Long notExistLineId = 0L;
        String name = "notExist";
        String color = "notExist";

        assertThatThrownBy(() -> lineService.updateLine(notExistLineId, name, color))
                .isInstanceOf(LineNotFoundException.class);
    }

    @DisplayName("특정 라인을 삭제할 수 있다.")
    @Test
    void deleteLineTest() {
        Long deleteTargetId = 1L;
        given(lineRepository.findById(deleteTargetId)).willReturn(Optional.ofNullable(LineFixtures.ID1_LINE));

        lineService.deleteLine(deleteTargetId);

        verify(lineRepository).deleteById(deleteTargetId);
    }

    @DisplayName("존재하지 않는 특정 라인을 삭제 시도할 경우 예외가 발생한다.")
    @Test
    void deleteWithNotExistLine() {
        Long notExistId = 4L;

        assertThatThrownBy(() -> lineService.deleteLine(notExistId)).isInstanceOf(LineNotFoundException.class);
    }
}