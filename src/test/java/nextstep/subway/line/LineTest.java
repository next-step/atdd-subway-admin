package nextstep.subway.line;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class LineTest {


    @DisplayName("라인 생성 테스트")
    @Test
    void createLineTest() {
        //when 라인 생성시
        Line line = new Line("2호선", "bg-green-600", new Station("강남역"), new Station("대림역"));

        //then 생성한 라인의 name과 color로 검증
        assertThat(line.getName()).isEqualTo("2호선");
        assertThat(line.getColor()).isEqualTo("bg-green-600");
    }

    @DisplayName("라인의 name, color 수정 테스트")
    @Test
    void updateLineTest() {
        //given 라인 생성 후
        Line line = new Line("신분당선", "bg-red-600", new Station("판교역"), new Station("정자역"));

        //when name과 color를 수정하면
        line.updateLine("분당선", "bg-yellow-600");

        //then line의 name과 color는 수정된다.
        assertThat(line.getName()).isEqualTo("분당선");
        assertThat(line.getColor()).isEqualTo("bg-yellow-600");
    }
}
