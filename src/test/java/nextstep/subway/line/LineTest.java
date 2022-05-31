package nextstep.subway.line;

import nextstep.subway.domain.Line;
import nextstep.subway.dto.LineRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

public class LineTest extends EntityTest {

    @DisplayName("라인은 이름, 컬러, 상행역 , 하행역, 거리 정보를 가진다.")
    @Test
    void createTest() {
        assertThat(line)
                .isEqualTo(new Line("신분당선", "bg-red-600", S1, S2, 10L));
    }


    @DisplayName("라인은 외부로 부터 upStation 과 downStation 을 업데이트 할수 있다.")
    @Test
    void updateByTest() {
        assertThat(line.upStationBy(S2)).isEqualTo(new Line(line.getId(), line.getName(), line.getColor(), S2, S2, line.getDistance()));
        assertThat(line.downStationBy(S1)).isEqualTo(new Line(line.getId(), line.getName(), line.getColor(), S2, S1, line.getDistance()));
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
