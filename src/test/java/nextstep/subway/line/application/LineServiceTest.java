package nextstep.subway.line.application;

import nextstep.subway.PreExecutionTest;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.line.dto.LineResponses;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class LineServiceTest extends PreExecutionTest {

    @Autowired
    private LineService lineService;

    @Test
    @DisplayName("노선 목록 조회")
    void findAllTest() {
        LineRequest request = new LineRequest("신분당선", "red", 강남역.getId(), 역삼역.getId(), 10);
        LineResponse except = lineService.save(request);
        LineResponses actual = lineService.findAll();

        assertThat(actual.toList()).hasSize(1).contains(except);
    }

}