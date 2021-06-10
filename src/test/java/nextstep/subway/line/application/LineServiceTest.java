package nextstep.subway.line.application;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import nextstep.subway.exception.ConflictException;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;

@DataJpaTest
class LineServiceTest {

    @Autowired
    private LineRepository lineRepository;

    private LineService lineService;

    @BeforeEach
    void init() {
        lineService = new LineService(lineRepository);
    }

    @Test
    @DisplayName(value = "새로운 Line 을 저장하면 DB에 반영된다")
    void createLine() {
        LineRequest request = new LineRequest("1호선", "blue");
        LineResponse response = lineService.saveLine(request);
        assertThat(response.getColor()).isEqualTo("blue");
    }

    @Test
    @DisplayName(value = "이미 저장된 Line 을 저장하면 error 을 반환한다")
    void errorWhenAlreadyExist() {
        LineRequest request = new LineRequest("1호선", "blue");
        lineService.saveLine(request);

        assertThrows(ConflictException.class, () -> lineService.saveLine(request));
    }

    @Test
    @DisplayName(value = "Line 수정을 실행하면 DB에 반영되어 수정된 결과가 나타난다")
    void updateLine() {
        LineRequest request = new LineRequest("1호선", "blue");
        LineResponse line = lineService.saveLine(request);

        lineService.updateLine(line.getId(), new LineRequest("1호선", "bg-blue-100"));

        Line updated = lineRepository.findById(line.getId()).get();
        assertThat(updated.getColor()).isEqualTo("bg-blue-100");
    }

}