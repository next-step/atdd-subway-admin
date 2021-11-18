package nextstep.subway.section.domain;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

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
        판교_정자_구간 = Section.of(1L, 판교역, 정자역, distance);
        정자_미금_구간 = Section.of(2L, 정자역, 미금역, distance);
        미금_동천_구간 = Section.of(3L, 미금역, 동천역, distance);
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

    @DisplayName("중간역을 제거한다.")
    @Test
    void removeMiddleStation() {
        // given
        List<Section> sectionsList = new ArrayList<>();
        sectionsList.add(판교_정자_구간);
        sectionsList.add(정자_미금_구간);
        sectionsList.add(미금_동천_구간);

        Sections sections = Sections.from(sectionsList);

        // when
        sections.removeMiddleStation(정자역);

        // then
        assertFalse(sections.contains(정자역));
    }

    @DisplayName("상행 종점역을 제거한다.")
    @Test
    void removeFirstEndStation() {
        // given
        List<Section> sectionsList = new ArrayList<>();
        sectionsList.add(판교_정자_구간);
        sectionsList.add(정자_미금_구간);
        sectionsList.add(미금_동천_구간);

        Sections sections = Sections.from(sectionsList);

        // when
        sections.removeEndStation(판교역);

        // then
        assertFalse(sections.contains(판교역));
    }

    @DisplayName("하행 종점역을 제거한다.")
    @Test
    void removeLastEndStation() {
        // given
        List<Section> sectionsList = new ArrayList<>();
        sectionsList.add(판교_정자_구간);
        sectionsList.add(정자_미금_구간);
        sectionsList.add(미금_동천_구간);

        Sections sections = Sections.from(sectionsList);

        // when
        sections.removeEndStation(동천역);

        // then
        assertFalse(sections.contains(동천역));
    }

    @DisplayName("구간이 1개만 존재할 때 역을 삭제할 수 없다.")
    @Test
    void removeStationWhenOnlySection() {
        // given
        List<Section> sectionsList = new ArrayList<>();
        sectionsList.add(판교_정자_구간);

        Sections sections = Sections.from(sectionsList);

        // when & then
        assertThatIllegalArgumentException().isThrownBy(() -> sections.removeEndStation(판교역))
                                            .withMessageContaining("노선의 구간이 1개인 경우 지하철 역을 삭제 할 수 없습니다.");
    }

    @DisplayName("존해하지 않는 종점역을 삭제할 수 없다.")
    @Test
    void removeNonExistentEndStation() {
        // given
        List<Section> sectionsList = new ArrayList<>();
        sectionsList.add(판교_정자_구간);
        sectionsList.add(정자_미금_구간);
        sectionsList.add(미금_동천_구간);

        Sections sections = Sections.from(sectionsList);

        // when
        Station 수원역 = Station.from(10L, "수원역");

        // then
        assertThatIllegalArgumentException().isThrownBy(() -> sections.removeEndStation(수원역))
                                            .withMessageContaining("존재하지 않는 지하철 역입니다.");
    }

    @DisplayName("존해하지 않는 중간역을 삭제할 수 없다.")
    @Test
    void removeNonExistentMiddleStation() {
        // given
        List<Section> sectionsList = new ArrayList<>();
        sectionsList.add(판교_정자_구간);
        sectionsList.add(정자_미금_구간);
        sectionsList.add(미금_동천_구간);

        Sections sections = Sections.from(sectionsList);

        // when
        Station 수원역 = Station.from(10L, "수원역");

        // then
        assertThatIllegalArgumentException().isThrownBy(() -> sections.removeEndStation(수원역))
                                            .withMessageContaining("존재하지 않는 지하철 역입니다.");
    }
}