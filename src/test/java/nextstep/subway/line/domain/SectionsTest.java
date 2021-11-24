package nextstep.subway.line.domain;

import nextstep.subway.station.domain.Station;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

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
        Station 영등포역 = new Station("영등포역");
        List<Section> sectionList = getSectionList(강남역, 광교역, 광명역, 영등포역);

        // Sections 안의 Section 들의 순서에 관계없이 테스트를 하기 위해 작성됨
        List<Sections> sectionsList = new ArrayList<>();
        int testSectionsCount = 10; // 확률상 모두 같은 순서로 정렬이 안될것 같은 횟수를 임의로 정함
        for (int i = 0; i < testSectionsCount; i++) {
            Collections.shuffle(sectionList);
            Sections sections = new Sections();
            for (Section section : sectionList) {
                sections.add(section);
            }
            sectionsList.add(sections);
        }

        for (Sections sections : sectionsList) {
            // when
            Section section = sections.getHeadSection();

            // then
            assertThat(section.getUpStation().getName()).isEqualTo("강남역");
        }
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
}
