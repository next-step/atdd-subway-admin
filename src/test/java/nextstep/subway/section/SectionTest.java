package nextstep.subway.section;

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
    private Station 선릉역;
    private Station 종합운동장역;

    private Section initSection;
    private int distance;

    @BeforeEach()
    void setUp() {
        강남역 = new Station("강남역");
        역삼역 = new Station("역삼역");
        선릉역 = new Station("선릉역");
        종합운동장역 = new Station("종합운동장역");
        initSection = new Section(강남역, 역삼역, distance);
        distance = 10;
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
    void isValidateExistStation() {
        //given
        Section newSction = new Section(강남역, 역삼역, distance);
        Sections sections = new Sections();

        sections.addSection(initSection);

        //when && then
        assertThatThrownBy(() -> sections.addSection(newSction))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("기존과 동일한 상행/하행선 등록 불가 합니다.");

    }
}
