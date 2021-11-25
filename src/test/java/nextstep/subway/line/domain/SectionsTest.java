package nextstep.subway.line.domain;

import nextstep.subway.line.exception.DuplicateBothStationException;
import nextstep.subway.line.exception.NotMatchedStationException;
import nextstep.subway.station.domain.Station;
import org.assertj.core.api.ThrowableAssert;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

public class SectionsTest {
    @DisplayName("상행선-하행선 구간 먼저 추가된 순으로 정렬된 역들을 가져온다")
    @Test
    void testGetStations() {
        // given
        Station 강남역 = new Station("강남역");
        Station 광교역 = new Station("광교역");
        Station 광명역 = new Station("광명역");
        Station 영등포역 = new Station("영등포역");
        Sections sections = new Sections();
        sections.add(new Section(new Line(), 강남역, 광교역, 100));
        sections.add(new Section(new Line(), 광교역, 광명역, 100));
        sections.add(new Section(new Line(), 광명역, 영등포역, 100));

        // when
        List<Station> stations = sections.getStations();

        // then
        assertThat(stations).hasSize(4)
                .map(Station::getName)
                .containsExactly("강남역", "광교역", "광명역", "영등포역");
    }

    @DisplayName("첫번째 구간을 찾는다")
    @Test
    void testGetHeadSection() {
        // given
        Station 강남역 = new Station("강남역");
        Station 광교역 = new Station("광교역");
        Station 광명역 = new Station("광명역");
        Sections sections = new Sections();
        sections.add(new Section(new Line(), 강남역, 광교역, 100));
        sections.add(new Section(new Line(), 광교역, 광명역, 100));

        // when
        Section section = sections.getHeadSection();

        // then
        assertThat(section.getUpStation().getName()).isEqualTo("강남역");
    }

    @DisplayName("다음 구간을 찾는다")
    @Test
    void testGetNextSection() {
        // given
        Station 강남역 = new Station("강남역");
        Station 광교역 = new Station("광교역");
        Station 광명역 = new Station("광명역");
        Station 영등포역 = new Station("영등포역");
        List<Section> sectionList = getSectionList(강남역, 광교역, 광명역, 영등포역);

        Sections sections = new Sections();
        for (Section section : sectionList) {
            sections.add(section);
        }

        for (int i = 0, n = sectionList.size() - 1; i < n; i++) {
            Section section = sectionList.get(i);
            Section expectedNextSection = sectionList.get(i+1);

            // when
            Section nextSection = sections.getNextSection(section);

            // then
            assertThat(nextSection).isEqualTo(expectedNextSection);
        }
    }

    private List<Section> getSectionList(Station ... stations) {
        List<Section> sectionList = new ArrayList<>();
        Line line = new Line();
        for (int i = 0, n = stations.length - 1; i < n; i++) {
            sectionList.add(new Section(line, stations[i], stations[i+1], 100));
        }
        return sectionList;
    }

    @DisplayName("상행선이 같으면 구간을 사이에 추가한다")
    @Test
    void testInsertionSection() {
        // given
        Station 강남역 = new Station("강남역");
        Station 광교역 = new Station("광교역");
        Station 광명역 = new Station("광명역");
        Station 영등포역 = new Station("영등포역");
        Sections sections = new Sections();
        sections.add(new Section(new Line(), 강남역, 광교역, 100));
        sections.add(new Section(new Line(), 광교역, 영등포역, 100));

        // when
        sections.add(new Section(new Line(), 강남역, 광명역, 60));

        // then
        assertThat(sections.getStations())
                .map(Station::getName)
                .containsExactly("강남역", "광명역", "광교역", "영등포역");
    }

    @DisplayName("하행선이 같으면 구간을 사이에 추가한다")
    @Test
    void testInsertionTailSection() {
        // given
        Station 강남역 = new Station("강남역");
        Station 광교역 = new Station("광교역");
        Station 광명역 = new Station("광명역");
        Station 영등포역 = new Station("영등포역");
        Sections sections = new Sections();
        sections.add(new Section(new Line(), 강남역, 광교역, 100));
        sections.add(new Section(new Line(), 광교역, 영등포역, 100));

        // when
        sections.add(new Section(new Line(), 광명역, 영등포역, 60));

        // then
        assertThat(sections.getStations())
                .map(Station::getName)
                .containsExactly("강남역", "광교역", "광명역", "영등포역");
    }

    @DisplayName("구간이 삽입되면 새로운 길이를 뺀 나머지를 변경되는 구간의 길이로 설정한다")
    @Test
    void testInsertionSectionDistance() {
        // given
        int totalDistance = 100;
        int newDistance = 60;
        Station 강남역 = new Station("강남역");
        Station 광교역 = new Station("광교역");
        Station 영등포역 = new Station("영등포역");
        Sections sections = new Sections();
        Section section = new Section(new Line(), 강남역, 광교역, totalDistance);
        sections.add(section);

        // when
        Section newSection = new Section(new Line(), 강남역, 영등포역, newDistance);
        sections.add(newSection);

        // then
        assertAll(
                () -> assertThat(section.getDistance()).isEqualTo(totalDistance - newDistance),
                () -> assertThat(newSection.getDistance()).isEqualTo(newDistance)
        );
    }

    @DisplayName("상행역과 하행역이 이미 노선에 모두 등록되어 있다면 추가할 수 없음")
    @Test
    void givenDuplicateBothStationsThenThrowException() {
        // given
        Station 강남역 = new Station("강남역");
        Station 광교역 = new Station("광교역");
        Station 광명역 = new Station("광명역");
        Station 영등포역 = new Station("영등포역");
        Sections sections = new Sections();
        sections.add(new Section(new Line(), 강남역, 광교역, 100));
        sections.add(new Section(new Line(), 광교역, 광명역, 100));
        sections.add(new Section(new Line(), 광명역, 영등포역, 100));

        // when
        ThrowableAssert.ThrowingCallable throwingCallable = () -> sections.add(new Section(new Line(), 광명역, 영등포역, 60));

        // then
        assertThatThrownBy(throwingCallable)
                .isInstanceOf(DuplicateBothStationException.class);
    }

    @DisplayName("상행역과 하행역 둘 중 하나도 포함되어있지 않으면 추가할 수 없음")
    @Test
    void givenNotMatchedBothStationsThenThrowException() {
        // given
        Station 강남역 = new Station("강남역");
        Station 광교역 = new Station("광교역");
        Station 광명역 = new Station("광명역");
        Station 영등포역 = new Station("영등포역");
        Station 안양역 = new Station("안양역");
        Sections sections = new Sections();
        sections.add(new Section(new Line(), 강남역, 광교역, 100));
        sections.add(new Section(new Line(), 광교역, 광명역, 100));

        // when
        ThrowableAssert.ThrowingCallable throwingCallable = () -> sections.add(new Section(new Line(), 영등포역, 안양역, 60));

        // then
        assertThatThrownBy(throwingCallable)
                .isInstanceOf(NotMatchedStationException.class);
    }

    @DisplayName("출발역으로 구간을 삭제한다")
    @Test
    void testDeleteSectionByStation() {
        // given
        Station 강남역 = new Station("강남역");
        Station 광교역 = new Station("광교역");
        Station 광명역 = new Station("광명역");
        Station 영등포역 = new Station("영등포역");
        List<Section> sectionList = getSectionList(강남역, 광교역, 광명역, 영등포역);
        Sections sections = new Sections(sectionList);
        // when
        sections.deleteSectionByStation(강남역);
        // then
        assertThat(sections.getStations()).containsExactly(광교역, 광명역, 영등포역);
    }

    @DisplayName("출발역으로 구간을 삭제한다")
    @Test
    void testDeleteSectionByTerminalStation() {
        // given
        Station 강남역 = new Station("강남역");
        Station 광교역 = new Station("광교역");
        Station 광명역 = new Station("광명역");
        Station 영등포역 = new Station("영등포역");
        List<Section> sectionList = getSectionList(강남역, 광교역, 광명역, 영등포역);
        Sections sections = new Sections(sectionList);
        // when
        sections.deleteSectionByStation(영등포역);
        // then
        assertThat(sections.getStations()).containsExactly(강남역, 광교역, 광명역);
    }

    @DisplayName("중간역으로 구간을 삭제한다")
    @Test
    void testDeleteSectionByMiddleStation() {
        // given
        Station 강남역 = new Station("강남역");
        Station 광교역 = new Station("광교역");
        Station 광명역 = new Station("광명역");
        Station 영등포역 = new Station("영등포역");
        List<Section> sectionList = getSectionList(강남역, 광교역, 광명역, 영등포역);
        Sections sections = new Sections(sectionList);
        // when
        sections.deleteSectionByStation(광교역);
        // then
        assertThat(sections.getStations()).containsExactly(강남역, 광명역, 영등포역);
    }

    @DisplayName("중간역으로 구간을 삭제해도 총 길이는 변하지 않는다")
    @Test
    void testDeleteSectionByMiddleStationThenNotChangedDistance() {
        // given
        Station 강남역 = new Station("강남역");
        Station 광교역 = new Station("광교역");
        Station 광명역 = new Station("광명역");
        Station 영등포역 = new Station("영등포역");
        List<Section> sectionList = getSectionList(강남역, 광교역, 광명역, 영등포역);
        Sections sections = new Sections(sectionList);
        Integer totalDistance = sections.getSections().stream()
                .map(Section::getDistance)
                .reduce(Integer::sum)
                .get();
        // when
        sections.deleteSectionByStation(광교역);
        // then
        Integer resultDistance = sections.getSections().stream()
                .map(Section::getDistance)
                .reduce(Integer::sum)
                .get();
        assertThat(resultDistance).isEqualTo(totalDistance);
    }
}
