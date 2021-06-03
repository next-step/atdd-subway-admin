package nextstep.subway.line.application;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.section.application.SectionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LineServiceTest {
    private static final Long NOT_EXIST_ID = 0L;
    public static final long EXIST_ID = 1L;

    @Mock
    private LineRepository lineRepository;

    @InjectMocks
    private SectionService sectionService;

    private LineService lineService;

    private LineRequest lineRequest;
    private LineRequest updateRequest;
    private Line line;

    @Mock
    private Line line2;

    @BeforeEach
    void setUp() {
        lineService = new LineService(lineRepository, sectionService);
        line = new Line("2호선", "green");
        lineRequest = new LineRequest("2호선", "green");
        updateRequest = new LineRequest("3호선", "orange");
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

    @DisplayName("노선 식별자에 해당하는 노선을 리턴한다.")
    @Test
    void getLineWithValidId() {
        when(lineRepository.findById(anyLong()))
                .thenReturn(Optional.of(line));

        LineResponse line = lineService.getLine(anyLong());

        assertThat(line).extracting("name").isEqualTo("2호선");
        assertThat(line).extracting("color").isEqualTo("green");
    }

    @DisplayName("존재하지않는 노선 식별자 요청시 예외를 던짐.")
    @Test
    void getLineWithNotExistedId() {
        when(lineRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> lineService.getLine(anyLong()))
                .isInstanceOf(LineNotFoundException.class);
    }

    @DisplayName("유효한 노선 갱신시 노선 정보가 변경된다.")
    @Test
    void updateLineWithValidRequest() {
        when(lineRepository.findById(EXIST_ID))
                .thenReturn(Optional.of(line2));

        lineService.updateLine(EXIST_ID, updateRequest);

        verify(line2, times(1)).update(any(Line.class));
    }

    @DisplayName("존재하지않는 노선 갱신시 예외를 던짐.")
    @Test
    void updateLineWithNotExistedId() {
        when(lineRepository.findById(eq(NOT_EXIST_ID)))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> lineService.updateLine(eq(NOT_EXIST_ID), lineRequest))
                .isInstanceOf(LineNotFoundException.class);
    }

    @DisplayName("유효한 노선 삭제시 노선이 삭제된다 .")
    @Test
    void deleteLineWithValidRequest() {
        when(lineRepository.findById(EXIST_ID))
                .thenReturn(Optional.of(line));

        lineService.deleteLine(EXIST_ID);

        verify(lineRepository).delete(any(Line.class));
    }

    @DisplayName("존재하지않는 노선 삭제시 예외를 던짐.")
    @Test
    void deleteLineWithNotExistedId() {
        when(lineRepository.findById(eq(NOT_EXIST_ID)))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> lineService.deleteLine(anyLong()))
                .isInstanceOf(LineNotFoundException.class);
    }
}
