package nextstep.subway.line.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import javassist.NotFoundException;
import nextstep.subway.line.dto.LineRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.dto.LineResponse;
import org.springframework.data.crossstore.ChangeSetPersister;

/**
 * LineRepository 클래스 기능 검증 테스트
 */
@ExtendWith(MockitoExtension.class)
class LineServiceTest {
    @Mock
    private LineRepository repository;

    @InjectMocks
    private LineService service;

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
    @DisplayName("Id로 노선 찾기")
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
}
