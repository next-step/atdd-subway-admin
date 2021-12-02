package nextstep.subway.section.domain;

import nextstep.subway.line.domain.Line;
import nextstep.subway.station.domain.Station;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
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

        //when
        Section nextSection = Section.of(line, upStation, downStation, 3);

        //then
        assertAll(
                () -> assertThat(line.getSections().size()).isEqualTo(2),
                () -> assertThat(section.getDistance()).isEqualTo(7)
        );
    }

    @DisplayName("새로운 역을 상행 종점으로 등록할 경우")
    @Test
    void addAscendingStation() {
        Line line = new Line("bg-red-600", "7호선");
        Station upStation = new Station(1L, "건대역");
        Station downStation = new Station(2L, "용마산역");
        Station newUpStation = new Station(3L, "뚝섬유원지역");
        Section section = Section.of(line, upStation, downStation, 10);

        //when
        Section.of(line, newUpStation, upStation, 4);

        //then
        assertAll(
                () -> assertThat(line.getSections().size()).isEqualTo(2),
                () -> assertThat(section.getDistance()).isEqualTo(10)
        );
    }

    @DisplayName("새로운 역을 하행 종점으로 등록할 경우")
    @Test
    void addDescendingStation() {
        Line line = new Line("bg-red-600", "7호선");
        Station upStation = new Station(1L, "건대역");
        Station downStation = new Station(2L, "용마산역");
        Station newDownStation = new Station(3L, "뚝섬유원지역");
        Section section = Section.of(line, upStation, downStation, 10);

        //when
        Section.of(line, downStation, newDownStation, 4);

        //then
        assertAll(
                () -> assertThat(line.getSections().size()).isEqualTo(2),
                () -> assertThat(section.getDistance()).isEqualTo(10)
        );
    }

    @DisplayName("기존 역 사이 길이보다 크거나 같으면 등록 할 수 없음")
    @Test
    void distanceErrorValid() {
        Station upStation = new Station(1L, "건대역");
        Station downStation = new Station(2L, "용마산역");
        Line line = new Line("bg-red-600", "7호선");
        Section.of(line, upStation, downStation, 10);
        Station newDownStation = new Station(3L, "뚝섬유원지역");

        assertThatThrownBy(() -> {
            Section nextSection = Section.of(line, upStation, newDownStation, 20);
        }).isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("기존 역 사이 길이보다 크거나 같으면 등록을 할 수 없습니다.");
    }

    @DisplayName("이미 등록되어 있어서 등록 할 수 없음")
    @Test
    void alreadyAddedValid() {
        Station upStation = new Station(1L, "건대역");
        Station downStation = new Station(2L, "용마산역");
        Line line = new Line("bg-red-600", "7호선");
        Section.of(line, upStation, downStation, 10);

        assertThatThrownBy(() -> {
            Section.of(line, upStation, downStation, 5);
        }).isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("이미 구역의 역들이 등록 되어 있습니다.");
    }

    @DisplayName("상행역과 하행역 둘 중 하나도 포함되어 있지 않을때 ")
    @Test
    void validNotAdded() {
        Line line = new Line("bg-red-600", "7호선");
        Station upStation = new Station(1L, "건대역");
        Station downStation = new Station(2L, "용마산역");
        Station newUpStation = new Station(3L, "뚝섬유원지역");
        Station newDownStation = new Station(4L, "중곡역");
        Section.of(line, upStation, downStation, 10);

        assertThatThrownBy(() -> {
            Section.of(line, newUpStation, newDownStation, 5);
        }).isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("상행역과 하행역 둘 중 하나도 포함되어 있지 않습니다.");
    }
}
