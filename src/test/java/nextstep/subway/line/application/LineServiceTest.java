package nextstep.subway.line.application;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineNameDuplicatedException;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.orm.jpa.JpaObjectRetrievalFailureException;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@DisplayName("LineService 테스트")
class LineServiceTest {

    @Autowired
    private LineService lineService;

    @Autowired
    private LineRepository lineRepository;

    private LineResponse 이호선;

    @BeforeEach
    void setUp() {
        이호선 = lineService.saveLine(new LineRequest("2호선", "green"));
    }

    @Test
    @DisplayName("지하철 노선을 저장한다.")
    void saveLine() {
        // given
        LineRequest 사호선 = new LineRequest("4호선", "blue");

        // when
        LineResponse lineResponse = lineService.saveLine(사호선);

        // then
        assertAll(
                () -> assertThat(lineResponse.getName()).isEqualTo(사호선.getName()),
                () -> assertThat(lineResponse.getColor()).isEqualTo(사호선.getColor())
        );
    }

    @Test
    @DisplayName("이름이 동일한 지하철 노선을 저장하면 예외가 발생한다.")
    void saveLineThrowException() {
        // when & then
        assertThatExceptionOfType(LineNameDuplicatedException.class)
                .isThrownBy(() -> lineService.saveLine(new LineRequest(이호선.getName(), "blue")))
                .withMessageMatching(LineNameDuplicatedException.MESSAGE);
    }

    @Test
    @DisplayName("지하철 노선 목록을 조회한다.")
    void getLines() {
        // given
        Line 사호선 = lineRepository.save(new Line("4호선", "blue"));

        // when
        List<LineResponse> lineResponses = lineService.getLines();

        // then
        List<Long> lineIds = lineResponses.stream()
                .map(LineResponse::getId)
                .collect(Collectors.toList());
        assertThat(lineIds).containsExactly(이호선.getId(), 사호선.getId());
    }

    @Test
    @DisplayName("지하철 노선을 조회한다.")
    void getLine() {
        // when
        LineResponse lineResponse = lineService.getLine(이호선.getId());

        // then
        assertAll(
                () -> assertThat(lineResponse.getId()).isEqualTo(이호선.getId()),
                () -> assertThat(lineResponse.getName()).isEqualTo(이호선.getName()),
                () -> assertThat(lineResponse.getColor()).isEqualTo(이호선.getColor()),
                () -> assertThat(lineResponse.getCreatedDate()).isNotNull(),
                () -> assertThat(lineResponse.getModifiedDate()).isNotNull()
        );
    }

    @Test
    @DisplayName("지하철 노선을 수정한다.")
    void updateLine() {
        // given
        LineRequest updateLineRequest = new LineRequest("3호선", "orange");

        // when
        lineService.updateLine(이호선.getId(), updateLineRequest);

        // then
        Line line = lineRepository.getOne(이호선.getId());
        assertAll(
                () -> assertThat(line.getId()).isEqualTo(이호선.getId()),
                () -> assertThat(line.getName()).isEqualTo(updateLineRequest.getName()),
                () -> assertThat(line.getColor()).isEqualTo(updateLineRequest.getColor()),
                () -> assertThat(line.getCreatedDate()).isNotNull(),
                () -> assertThat(line.getModifiedDate()).isNotNull()
        );
    }

    @Test
    @DisplayName("지하철 노선을 삭제한다.")
    void deleteLine() {
        // when
        lineService.deleteLine(이호선.getId());

        // then
        assertThatExceptionOfType(JpaObjectRetrievalFailureException.class)
                .isThrownBy(() -> lineRepository.getOne(이호선.getId()));
    }
}
