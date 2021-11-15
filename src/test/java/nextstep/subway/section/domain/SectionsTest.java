package nextstep.subway.section.domain;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import nextstep.subway.station.domain.Station;

class SectionsTest {
    private static final int DISTANCE = 10;

    private Station 판교역;
    private Station 정자역;
    private Station 미금역;
    private Station 동천역;
    private Distance distance;
    private Section 판교_정자_구간;
    private Section 정자_미금_구간;
    private Section 미금_동천_구간;

    @BeforeEach
    void setUp() {
        판교역 = Station.from(1L, "판교역");
        정자역 = Station.from(2L, "정자역");
        미금역 = Station.from(3L, "미금역");
        동천역 = Station.from(4L, "동천역");
        distance = Distance.from(DISTANCE);
        판교_정자_구간 = Section.of(판교역, 정자역, distance);
        정자_미금_구간 = Section.of(정자역, 미금역, distance);
        미금_동천_구간 = Section.of(미금역, 동천역, distance);
    }

    @DisplayName("Sections 을 Section 목록으로 생성한다.")
    @Test
    void create() {
        // when & then
        assertThatNoException().isThrownBy(
            () -> Sections.from(Arrays.asList(판교_정자_구간, 정자_미금_구간, 미금_동천_구간)));
    }

    @DisplayName("상행에서 하행순으로 지하철 역 목록을 반환한다.")
    @Test
    void sortStations() {
        // given
        Sections sections = Sections.from(Arrays.asList(정자_미금_구간, 미금_동천_구간, 판교_정자_구간));

        // when
        List<Station> stations = sections.getSortedStations();

        // then
        assertEquals(stations, Arrays.asList(판교역, 정자역, 미금역, 동천역));
    }
}