package nextstep.subway.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SectionsTest {
    @DisplayName("중간에 역을 추가 할 수 있다")
    @Test
    void addMiddleStation() {
        // given
        Station upStation = new Station("판교역");
        Station downStation = new Station("강남역");
        Station newStation = new Station("양재역");

        Sections sections = new Sections(new ArrayList<>());
        Section section = new Section(upStation, downStation, new Distance(10));
        sections.add(section);
        Section newSection = new Section(upStation, newStation, new Distance(5));

        // when
        sections.add(newSection);

        // then
        assertThat(sections.getStations()).containsOnly(upStation, downStation, newStation);
    }

    @DisplayName("상행역을 추가 할 수 있다")
    @Test
    void addUpStation() {
        // given
        Station upStation = new Station("판교역");
        Station downStation = new Station("강남역");
        Station newStation = new Station("양재역");

        Sections sections = new Sections(new ArrayList<>());
        Section section = new Section(upStation, downStation, new Distance(10));
        sections.add(section);
        Section newSection = new Section(newStation, upStation, new Distance(5));

        // when
        sections.add(newSection);

        // then
        assertThat(sections.getStations()).containsOnly(upStation, downStation, newStation);
    }

    @DisplayName("하행역을 추가 할 수 있다")
    @Test
    void addDownStation() {
        // given
        Station upStation = new Station("판교역");
        Station downStation = new Station("강남역");
        Station newStation = new Station("양재역");

        Sections sections = new Sections(new ArrayList<>());
        Section section = new Section(upStation, downStation, new Distance(10));
        sections.add(section);
        Section newSection = new Section(downStation, newStation, new Distance(5));

        // when
        sections.add(newSection);

        // then
        assertThat(sections.getStations()).containsOnly(upStation, downStation, newStation);
    }

    @DisplayName("추가하려는 구간의 지하철 역이 모두 존재하면 추가 할 수 없다")
    @Test
    void addExistStationException() {
        // given
        Station upStation = new Station("판교역");
        Station downStation = new Station("강남역");

        Sections sections = new Sections(new ArrayList<>());
        Section section = new Section(upStation, downStation, new Distance(10));
        sections.add(section);
        Section newSection = new Section(upStation, downStation, new Distance(5));

        // then
        assertThatThrownBy(() -> sections.add(newSection))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("추가하려는 역이 모두 존재합니다.");
    }

    @DisplayName("추가하려는 구간의 지하철 역이 모두 없으면 추가 할 수 없다")
    @Test
    void addNotExistStationException() {
        // given
        Station upStation = new Station("판교역");
        Station downStation = new Station("강남역");
        Station newUpStation = new Station("존재하지않는역1");
        Station newDownStation = new Station("존재하지않는역2");

        Sections sections = new Sections(new ArrayList<>());
        Section section = new Section(upStation, downStation, new Distance(10));
        sections.add(section);
        Section newSection = new Section(newUpStation, newDownStation, new Distance(5));

        // then
        assertThatThrownBy(() -> sections.add(newSection))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("상행성 하행선 모두 존재하지 않습니다.");
    }

    @DisplayName("중간에 존재하는 역을 제거 할 수 있다")
    @Test
    void deleteMiddleStation() {
        // given
        Station upStation = new Station("판교역");
        Station downStation = new Station("강남역");
        Station newStation = new Station("양재역");

        Sections sections = new Sections(new ArrayList<>());
        Section section = new Section(upStation, downStation, new Distance(10));
        sections.add(section);
        Section newSection = new Section(downStation, newStation, new Distance(5));
        sections.add(newSection);

        // when
        sections.delete(downStation);

        // then
        assertThat(sections.getStations()).containsOnly(upStation, newStation);
    }

    @DisplayName("상행역을 제거 할 수 있다")
    @Test
    void deleteUpStation() {
        // given
        Station upStation = new Station("판교역");
        Station downStation = new Station("강남역");
        Station newStation = new Station("양재역");

        Sections sections = new Sections(new ArrayList<>());
        Section section = new Section(upStation, downStation, new Distance(10));
        sections.add(section);
        Section newSection = new Section(downStation, newStation, new Distance(5));
        sections.add(newSection);

        // when
        sections.delete(upStation);

        // then
        assertThat(sections.getStations()).containsOnly(downStation, newStation);
    }

    @DisplayName("하행역을 제거 할 수 있다")
    @Test
    void deleteDownStation() {
        // given
        Station upStation = new Station("판교역");
        Station downStation = new Station("강남역");
        Station newStation = new Station("양재역");

        Sections sections = new Sections(new ArrayList<>());
        Section section = new Section(upStation, downStation, new Distance(10));
        sections.add(section);
        Section newSection = new Section(downStation, newStation, new Distance(5));
        sections.add(newSection);

        // when
        sections.delete(downStation);

        // then
        assertThat(sections.getStations()).containsOnly(upStation, newStation);
    }

    @DisplayName("삭제하려는 지하철 역이 없으면 삭제할 수 없다")
    @Test
    void deleteNotExistStationException() {
        // given
        Station downStation = new Station("강남역");
        Sections sections = new Sections(new ArrayList<>());

        // then
        assertThatThrownBy(() -> sections.delete(downStation))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("삭제하려는 지하철이 노선에 존재하지 않습니다");
    }

    @DisplayName("마지막 구간은 삭제할 수 없다")
    @Test
    void deleteOneSectionException() {
        // given
        Station upStation = new Station("판교역");
        Station downStation = new Station("강남역");
        Sections sections = new Sections(new ArrayList<>());
        Section section = new Section(upStation, downStation, new Distance(10));
        sections.add(section);

        // then
        assertThatThrownBy(() -> sections.delete(downStation))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("마지막 구간은 삭제할 수 없습니다.");
    }
}
