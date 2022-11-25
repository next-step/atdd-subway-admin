package nextstep.subway.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

public class SectionsTest {
    private Station upStation;
    private Station downStation;
    private Line line;
    private Section section;
    private Sections sections;

    @BeforeEach
    void setUp() {
        upStation = new Station(1L, "강남역");
        downStation = new Station(2L, "판교역");
        line = new Line(1L, "신분당선", "red");
        section = new Section(1L, upStation, downStation, new Distance(10));
        line.addSection(section);
    }

    @DisplayName("구간을 추가한다.")
    @Test
    void create() {
        sections = new Sections();
        sections.add(section);
        assertThat(sections.values()).hasSize(1);
    }

    @DisplayName("상행역과 하행역이 이미 구간에 모두 등록되어 있으면 예외가 발생한다.")
    @Test
    void exception01() {
        sections = new Sections();
        sections.add(section);
        assertThatIllegalArgumentException().isThrownBy(() -> sections.updateSection(upStation, downStation, 23));
    }

    @DisplayName("상행역과 하행역 둘 중 하나라도 포함되어있지 않으면 예외가 발생한다.")
    @Test
    void exception02() {
        sections = new Sections();
        sections.add(section);
        assertThatIllegalArgumentException().isThrownBy(() -> sections.updateSection(new Station("여의도역"), new Station("당산역"), 15));
    }

}
