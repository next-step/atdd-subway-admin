package nextstep.subway.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class LineTest {
    private Station 상행종점역;
    private Station 하행종점역;
    private Station 새로운역;
    private Line initLine;

    @BeforeEach
    void setUp() {
        상행종점역 = new Station("상행종점역");
        하행종점역 = new Station("하행종점역");
        새로운역 = new Station("새로운역");
        initLine = Line.of("기본노선","red",10, 상행종점역, 하행종점역 );
    }

    @Test
    @DisplayName("Line update")
    void updateLine() {
        Line newLine = new Line("새로운노선","blue");
        initLine.update(newLine);
        assertThat(initLine.getName()).isEqualTo("새로운노선");
        assertThat(initLine.getColor()).isEqualTo("blue");
    }

    @Test
    @DisplayName("Line add Section 이미 존재하는 section 추가하면 error 발생")
    void addSectionLineExist() {
        Section section = Section.of(상행종점역, 하행종점역, 8);
        assertThrows(IllegalArgumentException.class,() -> initLine.addSection(section));
    }

    @Test
    @DisplayName("Line add Section 모든 stations 다르면 error 발생")
    void addSectionLineNoStations() {
        Station 더새로운역 = new Station("더새로운역");
        Section section = Section.of(새로운역, 더새로운역, 8);
        assertThrows(IllegalArgumentException.class,() -> initLine.addSection(section));
    }
}
