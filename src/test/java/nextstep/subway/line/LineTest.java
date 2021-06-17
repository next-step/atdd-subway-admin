package nextstep.subway.line;

import nextstep.subway.line.domain.Line;
import nextstep.subway.section.domain.Section;
import nextstep.subway.station.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class LineTest {

    private Station 서울역;
    private Station 명동역;
    private Station 동역문역;

    @BeforeEach
    void setUp() {
        서울역 = new Station("서울역");
        명동역 = new Station("명동역");
        동역문역 = new Station("동역문역");
    }

    @DisplayName("노선 생성")
    @Test
    void 노선생성() {
        //Given+When
        Line line = new Line("4호선", "blue");

        //Then
        assertThat(line).isNotNull();
        assertThat(line.getName()).isEqualTo("4호선");
    }

    @DisplayName("노선 수정")
    @Test
    void 노선수정() {
        //Given
        Line line = new Line("4호선", "blue");

        //When
        line.update(new Line("2호선", "green"));

        //Then
        assertThat(line.getName()).isEqualTo("2호선");
        assertThat(line.getColor()).isEqualTo("green");
    }

    @DisplayName("노선에 구간 등록")
    @Test
    void 노선에_구간등록() {
        //Given
        Line line = new Line("4호선", "blue");
        Section section = new Section(서울역, 명동역, 30);

        //When
        line.addSection(section);

        //Then
        assertThat(line.getSections().contains(section)).isTrue();
    }

    @DisplayName("노선에서 구간 삭제")
    @Test
    void 노선에서_구간삭제(){
        //Given
        Line line = new Line("4호선", "blue");
        Section section = new Section(서울역, 명동역, 30);
        Section secondSection = new Section(명동역, 동역문역, 20);

        line.addSection(section);
        line.addSection(secondSection);

        //When
        line.removeSection(동역문역);

        //Then
        assertThat(line.getSections().contains(secondSection)).isFalse();
    }
}
