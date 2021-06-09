package nextstep.subway.line;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import nextstep.subway.exception.ValueFormatException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class LineTest {

    @BeforeEach
    void setUp() {

    }

    @DisplayName("노선 생성")
    @Test
    public void 노선생성시_생성확인() {
        //when
        Line line = new Line(2L, "새로운노선", "새로운색");

        //then
        assertThat(line).isNotNull();
        assertThat(line.name()).isEqualTo("새로운노선");
    }

    @DisplayName("노선생성시-예외발생")
    @Test
    public void 노선생성시_예외발생() throws Exception {
        //then
        assertThatThrownBy(() -> Line.create("","새로운색")).isInstanceOf(ValueFormatException.class);
        assertThatThrownBy(() -> Line.create(null,"새로운색")).isInstanceOf(ValueFormatException.class);
        assertThatThrownBy(() -> Line.create("새로운역","")).isInstanceOf(ValueFormatException.class);
        assertThatThrownBy(() -> Line.create("새로운역",null)).isInstanceOf(ValueFormatException.class);
    }

    @DisplayName("노선 수정")
    @Test
    public void 노선수정시_수정확인() {
        //given
        Line line = new Line(2L, "새로운노선", "새로운색");

        //when
        line.change("변경된노선이름", "변경된역이름");

        //then
        assertThat(line.name()).isEqualTo("변경된노선이름");
        assertThat(line.color()).isEqualTo("변경된역이름");
    }

    @DisplayName("노선 생성 - 역, 구간과 함께 생성시 연관관계 확인")
    @Test
    public void 역과구간과함께노선생성시_연관관계확인() throws Exception {
        //given
        Station upStation = new Station(1L, "상행종점역");
        Station downStation = new Station(2L, "하행종점역");
        Section section = new Section(1L, 100);

        //when
        Line line = Line.createWithSectionAndStation("테스트노선", "테스트색", section, upStation, downStation);

        //then
        assertThat(line.sortedStationList()).containsExactly(upStation, downStation);
        assertThat(line.sectionList()).contains(section);
        assertThat(section.upStation()).isEqualTo(upStation);
        assertThat(section.downStation()).isEqualTo(downStation);
        assertThat(upStation.downSection()).isEqualTo(section);
        assertThat(downStation.upSection()).isEqualTo(section);
    }

    @DisplayName("노선의 상행종점역부터 하행종점역까지 정렬된 리스트 조회")
    @Test
    public void 정렬된리스트_조회_확인() throws Exception {
        //given
        Line line = Line.create("테스트노선", "테스트색");
        Station station1 = new Station(1L, "1번");
        Station station2 = new Station(2L, "2번");
        Section section1 = new Section(1L, 100);
        station1.registerDownSection(section1);
        section1.registerDownStation(station2);

        Station station3 = new Station(3L, "3번");
        Section section2 = new Section(2L, 200);
        station2.registerDownSection(section2);
        section2.registerDownStation(station3);

        line.registerStation(station1);
        line.registerStation(station2);
        line.registerStation(station3);

        section1.registerLine(line);
        section2.registerLine(line);

        //when
        //then
        assertThat(line.sortedStationList()).containsExactly(station1, station2, station3);
    }
}
