package nextstep.subway.line.application;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@SpringBootTest
class LineServiceTest {

    @Autowired
    private LineService lineService;
    @Autowired
    private LineRepository lineRepository;

    @DisplayName("지하철 노선 등록")
    @Test
    void saveLine() {
        //when
        LineResponse actual = 지하철_노선_등록();

        //then
        Optional<Line> expectedEntity = lineRepository.findByName("2호선");
        LineResponse expected = LineResponse.of(expectedEntity.get());
        assertThat(actual).isEqualTo(expected);
    }

    @DisplayName("지하철 노선 등록 - 중복 오류")
    @Test
    void saveLine2() {
        //given
        지하철_노선_등록();

        //when
        LineResponse actual = 지하철_노선_등록();

        //then
        assertThat(actual.isFail()).isTrue();
    }

    private LineResponse 지하철_노선_등록() {
        return lineService.saveLine(new LineRequest("2호선", "green"));
    }
}