package nextstep.subway.section.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;
import nextstep.subway.station.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("지하철 구간 테스트")
public class SectionsTest {

    private Section firstSection = new Section(1L, 1L, 1L, 2L, 1000);
    private Section secondSection = new Section(2L, 1L, 2L, 3L, 1000);
    private Section thirdSection = new Section(3L, 1L, 3L, 4L, 1000);
    private Section forthSection = new Section(4L, 1L, 4L, 5L, 1000);
    private Section fifthSection = new Section(5L, 1L, 5L, 6L, 1000);
    private Section sixthSection = new Section(6L, 1L, 6L, 7L, 1000);
    private Sections sections;

    @BeforeEach
    void setup() {
        List<Section> list = new ArrayList<>();
        list.add(forthSection);
        list.add(thirdSection);
        list.add(fifthSection);
        list.add(secondSection);
        list.add(sixthSection);
        list.add(firstSection);
        sections = new Sections(list);
    }

    @DisplayName("지하철 노선의 구간 목록에서 지하철역 오름차순 정렬")
    @Test
    void findSortedStations() {
        // when
        List<Station> stations = sections.findSortedStations();

        // then
        assertThat(stations.get(0)).isEqualTo(firstSection.getUpStation());
        assertThat(stations.get(1)).isEqualTo(secondSection.getUpStation());
        assertThat(stations.get(2)).isEqualTo(thirdSection.getUpStation());
        assertThat(stations.get(3)).isEqualTo(forthSection.getUpStation());
        assertThat(stations.get(4)).isEqualTo(fifthSection.getUpStation());
        assertThat(stations.get(5)).isEqualTo(sixthSection.getUpStation());
        assertThat(stations.get(6)).isEqualTo(sixthSection.getDownStation());
    }

}
