package nextstep.subway.line;

import nextstep.subway.domain.Line;
import nextstep.subway.dto.LineRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

public class LineTest extends LineEntityTest {

    @DisplayName("라인은 이름, 컬러 정보를 가진다.")
    @Test
    void createTest() {
        assertThat(line)
                .isEqualTo(new Line("신분당선", "bg-red-600"));
    }


    @DisplayName("update 테스트")
    @Test
    void updateTest() {
        Line savedLine = lineRepository.save(line);
        LineRequest request = new LineRequest("test", null, null, null, null);
        savedLine.updateBy(request);
        assertThat(savedLine.getName()).isEqualTo(request.getName());
        assertThat(savedLine.getColor()).isNotEqualTo(request.getColor());
    }

}
