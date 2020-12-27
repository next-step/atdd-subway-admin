package nextstep.subway.line.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

import java.util.List;
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

    @DisplayName("지하철 노선 목록 조회 - 등록된 목록이 없는 경우")
    @Test
    void findAll() {
        //when
        List<LineResponse> lines = lineService.findAllLines();

        //then
        assertThat(lines).isEmpty();
    }

    @DisplayName("노선 정보 수정")
    @Test
    void update() {
        //given
        LineResponse lineResponse = 지하철_노선_등록();

        //when
        LineRequest lineRequest = new LineRequest("bg-blue-600", "구분당선");
        lineService.updateLine(lineResponse.getId(), lineRequest);

        //then
        Optional<Line> actual = lineRepository.findById(lineResponse.getId());
        assertThat(actual.get().getName()).isEqualTo(lineRequest.getName());
        assertThat(actual.get().getColor()).isEqualTo(lineRequest.getColor());
    }

    @DisplayName("존재하지 않는 노선 정보 수정")
    @Test
    void update2() {
        //when, then
        assertThatIllegalArgumentException()
              .isThrownBy(() -> lineService.updateLine(0L, new LineRequest()))
              .withMessage("[id=0] 노선정보가 존재하지 않습니다.");
    }

    private LineResponse 지하철_노선_등록() {
        return lineService.saveLine(new LineRequest("2호선", "green"));
    }
}