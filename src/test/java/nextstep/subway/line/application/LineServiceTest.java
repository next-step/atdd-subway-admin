package nextstep.subway.line.application;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.dto.LineResponse;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;

/**
 * LineService 클래스 기능 검증 테스트
 */
@ExtendWith(MockitoExtension.class)
class LineServiceTest {
    @Mock
    private LineRepository repository;

    @InjectMocks
    private LineService service;

    @Test
    @DisplayName("신규노선 저장")
    void save_line() {
        // given
        LineRequest lineRequest = new LineRequest("1호선", "blue");
        Line line = lineRequest.toLine();
        given(repository.save(any(Line.class))).willReturn(line);

        // when
        LineResponse lineResponse = service.saveLine(lineRequest);

        // then
        assertAll(
                () -> assertThat(lineResponse.getName()).isEqualTo(line.getName()),
                () -> assertThat(lineResponse.getColor()).isEqualTo(line.getColor())
        );
    }

    @Test
    @DisplayName("모든 노선 조회")
    void find_all_lines() {
        // given
        List<Line> lines = Arrays.asList(new Line("1호선", "blue"), new Line("2호선", "green"));
        List<String> lineNames = Arrays.asList("1호선", "2호선");
        given(repository.findAll()).willReturn(lines);

        // when
        List<LineResponse> lineResponses = service.findAllLines();

        // then
        assertThat(lineResponses).extracting("name").containsAll(lineNames);
    }

    @Test
    @DisplayName("ID로 노선 찾기")
    void find_line_by_id() {
        // given
        Line line = new Line("1호선", "blue");
        given(repository.findById(1L)).willReturn(Optional.of(line));

        // when
        LineResponse lineResponse = service.findLineById(1L);

        // then
        assertThat(lineResponse.getName()).isEqualTo(line.getName());
    }

    @Test
    @DisplayName("노선 정보 수정")
    void line_info_update() {
        // given
        Line line = new Line("1호선", "blue");
        LineRequest updateLineRequest = new LineRequest("2호선", "green");
        given(repository.findById(1L)).willReturn(Optional.of(line));

        // when
        service.updateLine(1L, updateLineRequest);
        LineResponse lineResponse = service.findLineById(1L);

        // then
        assertThat(lineResponse.getName()).isEqualTo(updateLineRequest.getName());
    }

    @Test
    @DisplayName("ID기준 노선 삭제")
    void delete_line_by_id() {
        // given
        Line line = new Line("1호선", "blue");
        given(repository.findById(1L)).willReturn(Optional.of(line));

        // when
        service.deleteLineById(1L);

        // then
        verify(repository).delete(line);
    }

    @Test
    @DisplayName("중복등록 예외처리")
    void duplicate_key_exception() {
        // given
        LineRequest lineRequest = new LineRequest("1호선", "blue");
        Line line = lineRequest.toLine();
        given(repository.save(any(Line.class))).willThrow(DataIntegrityViolationException.class);

        // when
        assertThatThrownBy(() -> service.saveLine(lineRequest))
                .isInstanceOf(DuplicateKeyException.class)
                .hasMessage("노선 생성에 실패했습니다. 이미 존재하는 노선입니다.");
    }

    @Test
    @DisplayName("노선 조회 실패 예외처리")
    void findLine_by_id_exception() {
        // given
        Line line = new Line("1호선", "blue");
        given(repository.findById(1L)).willReturn(Optional.empty());

        // then
        assertThatThrownBy(() -> service.findLineById(1L))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessage("노선이 존재하지 않습니다.");
    }

    @Test
    @DisplayName("존재하지 않는 노선 수정 시도 예외처리")
    void update_line_info_by_id_exception() {
        // given
        Line line = new Line("1호선", "blue");
        LineRequest updateLineRequest = new LineRequest("2호선", "green");
        given(repository.findById(1L)).willReturn(Optional.empty());

        // when
        assertThatThrownBy(() -> service.updateLine(1L, updateLineRequest))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessage("수정 대상 노선이 존재하지 않습니다.");
    }

    @Test
    @DisplayName("수정하려는 노선이름이 이미 존재할 경우 예외처리")
    void update_line_info_by_name_exception() {
        // given
        Line line = new Line("1호선", "blue");
        given(repository.findById(anyLong())).willReturn(Optional.of(line));
        given(repository.findByName(anyString())).willReturn(Optional.of(line));

        // when
        assertThatThrownBy(() -> service.updateLine(1L, new LineRequest("1호선", "green")))
                .isInstanceOf(DuplicateKeyException.class)
                .hasMessage("동일한 이름의 노선이 존재합니다.");
    }

    @Test
    @DisplayName("존재하지 않는 노선 삭제 시도 예외처리")
    void delete_line_by_id_exception() {
        // given
        Line line = new Line("1호선", "blue");
        given(repository.findById(1L)).willReturn(Optional.empty());

        // then
        assertThatThrownBy(() -> service.deleteLineById(1L))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessage("삭제 대상 노선이 존재하지 않습니다.");
    }
}
