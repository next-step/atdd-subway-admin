package nextstep.subway.section;

import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

public class SectionTest {

    private Station 강남역;
    private Station 건대입구역;

    @BeforeEach
    public void setup() {
        강남역 = new Station("강남역");
        건대입구역 = new Station("건대입구역");
    }

    @DisplayName("구간 생성 테스트")
    @Test
    void createSectionTest() {
        //when 구간을 생성하면
        Section section = new Section(강남역, 건대입구역, 10);

        //then 구간이 생성되고 생성한 upStation의 이름, downStation의 이름, 거리의 값과 동일하다.
        assertAll(
                () -> assertThat(section.getUpStation().getName()).isEqualTo("강남역"),
                () -> assertThat(section.getDownStation().getName()).isEqualTo("건대입구역"),
                () -> assertThat(section.getDistance()).isEqualTo(10)
        );

    }

    @DisplayName("구간의 upstation과 distance 업데이트 테스트")
    @Test
    void updateUpStationAndDistanceTest() {
        //given 구간을 생성하고
        Section section = new Section(강남역, 건대입구역, 10);

        //when 구간의 upstation과 distance를 업데이트하면
        Station 신림역 = new Station("신림역");
        section.updateUpStationAndDistance(신림역, 8);

        //then 구간의 upstation과 distance가 변경된다.
        assertAll(
                () -> assertThat(section.getUpStation()).isEqualTo(신림역),
                () -> assertThat(section.getDistance()).isEqualTo(8)
        );
    }

}
