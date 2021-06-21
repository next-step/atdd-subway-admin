package nextstep.subway.section.domain;

import nextstep.subway.station.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SectionsTest {
    private Sections sections;
    private Station station0;
    private Station station1;
    private Station station2;
    private Station station3;
    private Station station4;
    private Section section1;

    @BeforeEach
    void setUp() {
        sections = new Sections();
        station0 = new Station("0번역");
        station1 = new Station("1번역");
        station2 = new Station("2번역");
        station3 = new Station("3번역");
        station4 = new Station("4번역");
        section1 = Section.of(station1, station3, 7);
        sections.add(section1);
    }

    @Test
    void 역사이에_새로운_역을_등록할_경우() {
        Section section2 = Section.of(station1, station2, 4);
        sections.add(section2);
        추가된_역순서와_비교(Arrays.asList(station1, station2, station3));
        assertThat(section1.getDistance()).isEqualTo(3);
        assertThat(section2.getDistance()).isEqualTo(4);

    }

    @Test
    void 새로운_역을_상행_종점으로_등록할_경우() {
        sections.add(Section.of(station0, station1, 4));
        추가된_역순서와_비교(Arrays.asList(station0, station1, station3));
    }

    @Test
    void 새로운_역을_하행_종점으로_등록할_경우() {
        sections.add(Section.of(station3, station4, 4));
        추가된_역순서와_비교(Arrays.asList(station1, station3, station4));
    }

    @Test
    void 역_사이에_새로운_역을_등록할_경우_기존_역_사이_길이보다_크거나_같으면_등록을_할_수_없음() {
        assertThatThrownBy(() -> sections.add(Section.of(station1, station2, 7)))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 상행역과_하행역이_이미_노선에_모두_등록되어_있다면_추가할_수_없음() {
        assertThatThrownBy(() -> sections.add(Section.of(station1, station3, 7)))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 상행역과_하행역_둘_중_하나도_포함되어있지_않으면_추가할_수_없음() {
        assertThatThrownBy(() -> sections.add(Section.of(station2, station4, 7)))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 노선의_중간역을_삭제할_경우() {
        //given 1=>2=>3 역 존재
        Section section2 = Section.of(station1, station2, 4);
        sections.add(section2);
        //when (1=>2=>3) - 1
        sections.removeSectionByStation(station2);
        //then 거리 계산확인
        assertThat(section2.getDistance()).isEqualTo(7);
        //then = 1=>2
        추가된_역순서와_비교(Arrays.asList(station1, station3));
    }

    @Test
    void 노선의_첫역을_삭제할_경우() {
        //given 1=>2=>3 역 존재
        Section section2 = Section.of(station1, station2, 4);
        sections.add(section2);
        //when (1=>2=>3) - 1
        sections.removeSectionByStation(station1);
        //then = 2=>3
        추가된_역순서와_비교(Arrays.asList(station2, station3));
    }

    @Test
    void 노선의_마지막역을_삭제할_경우() {
        //given 1=>2=>3 역 존재
        Section section2 = Section.of(station1, station2, 4);
        sections.add(section2);
        //when (1=>2=>3) - 3
        sections.removeSectionByStation(station3);
        //then = 1=>2
        추가된_역순서와_비교(Arrays.asList(station1, station2));
    }

    @Test
    void 노선에_존재하지않는_역은_삭제할_수_없음() {
        //given 1=>2=>3 역 존재
        Section section2 = Section.of(station1, station2, 4);
        sections.add(section2);
        //when && then (1=>2=>3) - 4 실패
        assertThatThrownBy(() -> sections.removeSectionByStation(station4))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 노선에_상행역_하행역만_존자할경우_삭제할_수_없음() {
        //when && then
        assertThatThrownBy(() -> sections.removeSectionByStation(station2))
                .isInstanceOf(IllegalArgumentException.class);
    }

    private void 추가된_역순서와_비교(List<Station> stationList) {
        assertThat(sections.getSortedStations().stream().map(Station::getName).collect(Collectors.toList())).isEqualTo(stationList.stream().map(Station::getName).collect(Collectors.toList()));
    }
}