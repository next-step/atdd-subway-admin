package nextstep.subway.line;

import nextstep.subway.line.domain.Line;
import nextstep.subway.section.Section;
import nextstep.subway.station.domain.Station;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class LineTest {

    @DisplayName("노선 생성")
    @Test
    void 노선생성(){
        //Given+When
        Line line = new Line("4호선","blue");

        //Then
        assertThat(line).isNotNull();
        assertThat(line.getName()).isEqualTo("4호선");
    }

    @DisplayName("노선 수정")
    @Test
    void 노선수정(){
        //Given
        Line line = new Line("4호선","blue");

        //When
        line.update(new Line("2호선", "green"));

        //Then
        assertThat(line.getName()).isEqualTo("2호선");
        assertThat(line.getColor()).isEqualTo("green");
    }

    @DisplayName("노선에 구간 등록")
    @Test
    void 노선에_구간등록(){
        //Given
        Line line = new Line("4호선","blue");
        Section section = new Section(new Station("서울역"), new Station("명동역"), 30);

        //When
        line.addSection(section);

        //Then
        assertThat(line.getSections().contains(section)).isTrue();
    }
}
