package nextstep.subway.section;

import nextstep.subway.domain.Distance;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Sections;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

public class SectionTest {
    private Station 강남역;
    private Station 역삼역;
    private Station 삼성역;
    private Station 종합운동장역;
    private Section initSection;
    private Sections sections;
    private int distance;

    @BeforeEach()
    void setUp() {
        강남역 = new Station("강남역");
        역삼역 = new Station("역삼역");
        삼성역 = new Station("삼성역");
        종합운동장역 = new Station("종합운동장역");

        distance = 10;
        initSection = new Section(강남역, 역삼역, new Distance(distance));

        sections = new Sections();
        sections.addSection(initSection);
    }

    @DisplayName("구간 생성")
    @Test
    void saveSection() {
        //then
        assertAll(
                () -> assertThat(initSection.getUpStation()).isEqualTo(강남역),
                () -> assertThat(initSection.getDownStation()).isEqualTo(역삼역),
                () -> assertThat(initSection.getDistance()).isEqualTo(distance)
        );

    }

    @DisplayName("기존구간의 상행/하행역이 모두 같으면 구간 생성 불가능")
    @Test
    void isValidExistStation() {
        //given
        Section newSection = new Section(강남역, 역삼역, new Distance(distance));

        //when && then
        assertThatThrownBy(() -> sections.addSection(newSection))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("기존과 동일한 상행/하행선 등록 불가 합니다.");
    }

    @DisplayName("상행역과 하행역 둘 중 하나도 포함되어 있지 않으면 구간 생성 불가능")
    @Test
    void isValidNotExistStation() {
        //given
        Section newSection = new Section(삼성역, 종합운동장역, new Distance(distance));

        //when && then
        assertThatThrownBy(() -> sections.addSection(newSection))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("기존 등록된 상행/하행선이 하나도 포함되어 있지 않습니다.");
    }
}