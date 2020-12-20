package nextstep.subway.line.application;

import nextstep.subway.line.application.exceptions.LineNotFoundException;
import nextstep.subway.line.domain.*;
import nextstep.subway.line.domain.exceptions.StationNotFoundException;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import org.hibernate.exception.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
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

    @Mock
    private SafeStationDomainService safeStationDomainService;

    @BeforeEach
    void setup() {
        lineService = new LineService(lineRepository, safeStationDomainService);
    }

    @DisplayName("Line 생성 시 Section도 같이 생성된다.")
    @Test
    void createNewLineTest() {
        String lineName = "9호선";
        String lineColor = "금색";
        Long upStationId = 1L;
        Long downStationId = 2L;
        Long distance = 3L;
        LocalDateTime now = LocalDateTime.now();
        given(lineRepository.save(any())).willReturn(new Line(lineName, lineColor));
        given(safeStationDomainService.getStationSafely(upStationId))
                .willReturn(new SafeStationInfo(upStationId, "up", now, null));
        given(safeStationDomainService.getStationSafely(downStationId))
                .willReturn(new SafeStationInfo(downStationId, "down", now, null));

        LineResponse lineResponse = lineService.saveLine(
                new LineRequest(lineName, lineColor, upStationId, downStationId, distance));

        List<Long> stationIds = lineResponse.getStations().stream()
                .map(SafeStationInfo::getId)
                .collect(Collectors.toList());
        assertThat(stationIds).contains(upStationId, downStationId);
    }

    @DisplayName("존재하지 않는 역을 종점역으로 Line 생성 시 예외가 발생한다.")
    @Test
    void createNewLineWithNotExistStationTest() {
        String lineName = "9호선";
        String lineColor = "금색";
        Long upStationId = 4L;
        Long downStationId = 44L;
        Long distance = 3L;
        given(safeStationDomainService.getStationSafely(upStationId)).willThrow(StationNotFoundException.class);

        assertThatThrownBy(() -> lineService.saveLine(
                new LineRequest(lineName, lineColor, upStationId, downStationId, distance))
        ).isInstanceOf(StationNotFoundException.class);
    }

    @DisplayName("이미 존재하는 라인을 또 생성 시 예외가 발생한다.")
    @Test
    void createNewLineFailTest() {
        String lineName = "alreadyExist";
        String lineColor = "alreadyExist";
        Long upStationId = 1L;
        Long downStationId = 2L;
        Long distance = 3L;
        LocalDateTime now = LocalDateTime.now();
        given(safeStationDomainService.getStationSafely(upStationId))
                .willReturn(new SafeStationInfo(upStationId, "up", now, null));
        given(safeStationDomainService.getStationSafely(downStationId))
                .willReturn(new SafeStationInfo(downStationId, "down", now, null));
        given(lineRepository.save(any())).willThrow(ConstraintViolationException.class);

        assertThatThrownBy(() -> lineService.saveLine(
                new LineRequest(lineName, lineColor, upStationId, downStationId, distance))
        ).isInstanceOf(ConstraintViolationException.class);
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

    @DisplayName("신규 노선을 안전하게 생성할 수 있다.")
    @Test
    void createLineTest() {
        String lineName = "2호선";
        String lineColor = "green";
        Long upStationId = 1L;
        Long downStationId = 2L;
        Long distance = 3L;
        int expectedSize = 1;

        Line line = lineService.createLine(lineName, lineColor, upStationId, downStationId, distance);

        assertThat(line.getSectionsSize()).isEqualTo(expectedSize);
    }
}