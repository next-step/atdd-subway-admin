package nextstep.subway.section.domain;

import nextstep.subway.station.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertAll;

class SectionsTest {
    private static final Station 강남역 = Station.of(1L, "강남역");
    private static final Station 양재역 = Station.of(2L, "양재역");
    private static final Station 청계산입구역 = Station.of(3L, "청계산입구역");
    private static final Station 판교역 = Station.of(4L, "판교역");
    private static final Station 수지구청역 = Station.of(5L, "수지구청역");
    private static final Station 광교역 = Station.of(6L, "광교역");

    private Sections sections;

    @BeforeEach
    void setUp() {
        Sections sections = new Sections();
        sections.add(new Section(청계산입구역, 판교역, 2));
        sections.add(new Section(강남역, 양재역, 2));
        sections.add(new Section(수지구청역, 광교역, 2));
        sections.add(new Section(양재역, 청계산입구역, 2));
        sections.add(new Section(판교역, 수지구청역, 2));
        this.sections = sections;
    }

    @DisplayName("상행 -> 하행 순서에 맞게 역을 반환한다.")
    @Test
    void sortedStationsTest() {
        // given
        Set<Station> sortedStations = sections.getSortedStations();

        // when & then
        assertThat(sortedStations).containsExactly(강남역, 양재역, 청계산입구역, 판교역, 수지구청역, 광교역);
    }

    @DisplayName("이미 등록된 역 구간을 등록할 수 없다.")
    @Test
    void addFailTest() {
        // given
        Section given1 = new Section(청계산입구역, 판교역, 8);
        Section given2 = new Section(강남역, 광교역, 8);

        // when & then
        assertAll(
                () -> assertThatThrownBy(() -> sections.registerNewSection(given1)).isInstanceOf(IllegalArgumentException.class),
                () -> assertThatThrownBy(() -> sections.registerNewSection(given2)).isInstanceOf(IllegalArgumentException.class)
        );
    }

    @DisplayName("역 사이에 새로운 역을 등록한다.")
    @Test
    void registerNewSectionTest() {
        // given
        Station 양재시민의숲역 = Station.of(7L, "양재시민의숲역");
        Section given = new Section(양재역, 양재시민의숲역, 1);

        // when
        sections.registerNewSection(given);

        // then
        assertThat(sections.getSortedStations()).containsExactly(강남역, 양재역, 양재시민의숲역, 청계산입구역, 판교역, 수지구청역, 광교역);
    }

    @DisplayName("역 사이에 새로운 역을 등록할 때 기존의 역 간격보다 큰 간격을 등록할 수 없다.")
    @Test
    void registerNewSectionFailTest() {
        // given
        Station 양재시민의숲역 = Station.of(7L, "양재시민의숲역");
        Section given = new Section(양재역, 양재시민의숲역, 10);

        // when & then
        assertThatThrownBy(() -> sections.registerNewSection(given))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("존재하지 않는 역을 제거하려고 하면 예외가 발생한다.")
    @Test
    void canNotRemoveNotFoundStationTest() {
        // given
        Sections sections = new Sections();
        sections.add(new Section(강남역, 양재역, 2));
        sections.add(new Section(양재역, 청계산입구역, 2));

        // when & then
        assertThatThrownBy(() -> sections.removeSectionByStation(판교역))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("등록되어있지 않은 역으로 구간을 제거할 수 없습니다.");
    }

    @DisplayName("구간이 한개만 존재할때 구간을 제거하려고 하면 예외가 발생한다.")
    @Test
    void canNotRemoveExistOnlyOneSection() {
        // given
        Sections sections = new Sections();
        sections.add(new Section(강남역, 양재역, 2));

        // when & then
        assertThatThrownBy(() -> sections.removeSectionByStation(강남역))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("구간이 하나만 존재할 경우 제거할 수 없습니다.");
    }
}
