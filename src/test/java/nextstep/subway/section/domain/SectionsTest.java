package nextstep.subway.section.domain;

import nextstep.subway.line.domain.Line;
import nextstep.subway.station.domain.Station;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

public class SectionsTest {

    @DisplayName("Sections 일급 콜렉션 객체 생성")
    @Test
    void createSections() {
        Station upStation = new Station("건대역");
        Station downStation = new Station("용마산역");
        Line line = new Line("bg-red-600", "7호선");
        Section section = Section.of(line, upStation, downStation, 10);

        Sections sections = Sections.empty();
        sections.add(section);

        assertThat(sections).isNotNull();
    }

    @DisplayName("구간 추가시 기존 구간 변경 검증")
    @Test
    void addSection() {
        Station upStation = new Station(1L, "건대역");
        Station downStation = new Station(2L, "용마산역");
        Line line = new Line("bg-red-600", "7호선");
        Section section = Section.of(line, upStation, downStation, 10);
        downStation = new Station(3L, "뚝섬유원지역");
        Section nextSection = Section.of(line, upStation, downStation, 3);
        Sections sections = Sections.empty();
        sections.add(section);

        //when
        sections.add(nextSection);

        //then
        assertAll(
                () -> assertThat(sections.size()).isEqualTo(2),
                () -> assertThat(section.getDistance()).isEqualTo(7)
        );
    }

    @DisplayName("새로운 역을 상행 종점으로 등록할 경우")
    @Test
    void addAscendingStation() {
        Station upStation = new Station(1L, "건대역");
        Station downStation = new Station(2L, "용마산역");
        Line line = new Line("bg-red-600", "7호선");
        Section section = Section.of(line, upStation, downStation, 10);

        Station newUpStation = new Station(3L, "뚝섬유원지역");
        Section nextSection = Section.of(line, newUpStation, upStation, 4);
        Sections sections = Sections.empty();
        sections.add(section);

        //when
        sections.add(nextSection);

        //then
        assertAll(
                () -> assertThat(sections.size()).isEqualTo(2),
                () -> assertThat(section.getDistance()).isEqualTo(10)
        );
    }

    @DisplayName("새로운 역을 하행 종점으로 등록할 경우")
    @Test
    void addDescendingStation() {
        Station upStation = new Station(1L, "건대역");
        Station downStation = new Station(2L, "용마산역");
        Line line = new Line("bg-red-600", "7호선");
        Section section = Section.of(line, upStation, downStation, 10);

        Station newDownStation = new Station(3L, "뚝섬유원지역");
        Section nextSection = Section.of(line, downStation, newDownStation, 4);
        Sections sections = Sections.empty();
        sections.add(section);

        //when
        sections.add(nextSection);

        //then
        assertAll(
                () -> assertThat(sections.size()).isEqualTo(2),
                () -> assertThat(section.getDistance()).isEqualTo(10)
        );
    }
}
