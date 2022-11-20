package nextstep.subway.line;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import nextstep.subway.line.exception.IllegalDistanceException;
import nextstep.subway.station.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SectionTest {

    private Station 강남역;
    private Station 역삼역;
    private Station 블루보틀역;

    @BeforeEach
    void setUp() {
        강남역 = new Station("강남역");
        역삼역 = new Station("역삼역");
        블루보틀역 = new Station("블루보틀역");
    }

    @Test
    void 구간사이에_신규_구간_추가시_길이가_기존길이보다_같거나_긴경우_에러() {
        Section section = new Section(강남역, 역삼역, 7);
        assertThatThrownBy(() -> section.insertBetween(new Section(강남역, 블루보틀역, 7)))
            .isInstanceOf(IllegalDistanceException.class)
            .hasMessage("역 사이에 새로운 역을 등록할 경우 기존 역 사이 길이보다 크거나 같으면 등록을 할 수 없습니다.");
    }

    @Test
    void 기존구간과_신규구간의_상행선이_동일한경우_기존구간의_상행선은_신규구간의_하행선이다() {
        Section section = new Section(강남역, 역삼역, 7);
        section.insertBetween(new Section(강남역, 블루보틀역, 3));
        assertThat(section).isEqualTo(new Section(블루보틀역, 역삼역, 4));
    }

    @Test
    void 기존구간과_신규구간의_하행선이_동일한경우_기존구간의_하행선은_신규구간의_상행선이다() {
        Section section = new Section(강남역, 역삼역, 7);
        section.insertBetween(new Section(블루보틀역, 역삼역, 3));
        assertThat(section).isEqualTo(new Section(강남역, 블루보틀역, 4));
    }

    @Test
    void 기존구간_사이에_신규구간_추가시_자동_길이계산() {
        Section section = new Section(강남역, 역삼역, 7);
        section.insertBetween(new Section(블루보틀역, 역삼역, 3));
        assertThat(section.getDistance()).isEqualTo(4);
    }


}