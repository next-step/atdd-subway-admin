package nextstep.subway.section.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;
import nextstep.subway.station.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("지하철 구간 테스트")
public class SectionsTest {

    private Section firstSection = new Section(1L, 1L, 2L, 1000);
    private Section secondSection = new Section(1L, 2L, 3L, 1000);
    private Section thirdSection = new Section(1L, 3L, 4L, 1000);
    private Section forthSection = new Section(1L, 4L, 5L, 1000);
    private Section fifthSection = new Section(1L, 5L, 6L, 1000);
    private Section sixthSection = new Section(1L, 6L, 7L, 1000);

    @DisplayName("객체 생성시 구간 콜렉션이 없을 경우 예외 발생")
    @Test
    void createSectionWrong() {
        assertThatThrownBy(() -> new Sections(null))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("생성자를 이용하여 구간을 생성시 지하철역 정렬 확인")
    @Test
    void findStationsInOrder() {

        // given
        List<Section> sectionList = new ArrayList<>(Arrays.asList(firstSection, secondSection, thirdSection, forthSection, fifthSection, sixthSection));
        Collections.shuffle(sectionList); //섞기

        // when
        Sections sections = new Sections(sectionList);
        List<Station> stations = sections.findStationsInOrder();

        // then
        assertThat(stations.get(0)).isEqualTo(firstSection.getUpStation());
        assertThat(stations.get(1)).isEqualTo(secondSection.getUpStation());
        assertThat(stations.get(2)).isEqualTo(thirdSection.getUpStation());
        assertThat(stations.get(3)).isEqualTo(forthSection.getUpStation());
        assertThat(stations.get(4)).isEqualTo(fifthSection.getUpStation());
        assertThat(stations.get(5)).isEqualTo(sixthSection.getUpStation());
        assertThat(stations.get(6)).isEqualTo(sixthSection.getDownStation());
    }

    @DisplayName("구간추가시 연결이 불가능한 구간일 경우 예외 발생")
    @Test
    void addFail() {
        // given
        Sections sections = new Sections();
        sections.add(firstSection);

        // when & then
        assertThatThrownBy(() -> sections.add(thirdSection))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("구간추가 메서드를 이용했을때 지하철역 정렬 확인")
    @Test
    void add() {
        // given
        Sections sections = new Sections();
        sections.add(forthSection);
        sections.add(thirdSection);
        sections.add(fifthSection);
        sections.add(secondSection);
        sections.add(sixthSection);
        sections.add(firstSection);

        // when
        List<Station> stations = sections.findStationsInOrder();

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
