package nextstep.subway.domain;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class SectionTest {
    private Station 강남역 = new Station("강남역");;
    private Station 정자역 = new Station("정자역");
    private Station 미금역 = new Station("미금역");;

    @DisplayName("새로운 구간이 상행역 기준으로 추가될때 기존구간의 상행역과 거리가 변경된다.")
    @Test
    void updateUpStation_test() {
        Section 기존_구간 = 구간_생성(강남역, 미금역, 10);
        Section 새_구간 = 구간_생성(강남역, 정자역, 5);
        기존_구간.updateUpStation(새_구간);

        assertAll(
                ()-> assertEquals(정자역, 기존_구간.getUpStation()),
                ()-> assertEquals(Distance.from(5), 기존_구간.getDistance())
        );
    }

    public static Section 구간_생성(Station 상행역, Station 하행역, int 거리){
        return new Section(상행역, 하행역, 거리);
    }

}