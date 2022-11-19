package nextstep.subway.domain;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class SectionTest {

    private Section section;
    private Station 강남역 = new Station("강남역");;
    private Station 미금역 = new Station("미금역");;
    private Station 광교역 = new Station("광교역");;

    @BeforeEach
    void setUp() {
        section = new Section(강남역, 광교역, 10);
    }


    @DisplayName("새로운 구간이 상행역 기준으로 추가될때 기존구간의 상행역과 거리가 변경된다.")
    @Test
    void updateUpStation_test() {

        section.updateUpStation(new Section(강남역, 미금역, 5));

        assertAll(
                ()-> assertEquals(미금역, section.getUpStation()),
                ()-> assertEquals(5, section.getDistance())
        );
    }


    @DisplayName("새로운 구간이 추가될때 추가될 거리가 현재 거리보다 길면 IllegalArgumentException이 발생한다.")
    @Test
    void updateUpStation_distance_exception() {
        assertThatThrownBy(() -> section.updateUpStation(new Section(강남역, 미금역, 10)))
                .isInstanceOf(IllegalArgumentException.class);
    }

}