package nextstep.subway.line;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import nextstep.subway.exception.ValueFormatException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

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
        assertThat(line.sections().contains(section)).isTrue();
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

    @DisplayName("구간 생성 및 노선등록 - 연관관계확인")
    @Test
    public void 구간생성및노선등록_연관관계확인() throws Exception {
        //given
        Station upStation = new Station(1L, "상행종점역");
        Station downStation = new Station(2L, "하행종점역");
        Section section = new Section(1L, 100);
        Line line = Line.createWithSectionAndStation("테스트노선", "테스트색", section, upStation, downStation);

        Station newStation = new Station(3L, "새로운역");
        Section newSection = new Section(2L, 50);

        //when
        line.register(upStation, newStation, newSection);

        //then
        assertThat(line.sortedStationList()).containsExactly(upStation, newStation, downStation);
        assertThat(newSection.downStation()).isEqualTo(newStation);
        assertThat(section.downStation()).isEqualTo(downStation);
        assertThat(newStation.downSection()).isEqualTo(section);
        assertThat(newStation.upSection()).isEqualTo(newSection);
    }

    @DisplayName("구간 생성 및 노선등록 - 등록하려는 두 역이 모두 노선에 포함되지 않는 경우 등록되면 안됨")
    @Test
    public void 구간생성및노선등록_등록안되는경우1() throws Exception {
        //given
        Station upStation = new Station(1L, "상행종점역");
        Station downStation = new Station(2L, "하행종점역");
        Section section = new Section(1L, 100);
        Line line = Line.createWithSectionAndStation("테스트노선", "테스트색", section, upStation, downStation);

        Station newStation1 = new Station(3L, "새로운역1");
        Station newStation2 = new Station(4L, "새로운역2");
        Section newSection = new Section(2L, 50);

        //when
        //then
        assertThatThrownBy(() -> line.register(newStation1, newStation2, newSection))
                .isInstanceOf(IllegalStateException.class);
    }

    @DisplayName("구간 생성 및 노선등록 - 등록하려는 두 역이 모두 노선에 포함된 경우 등록되면 안됨")
    @Test
    public void 구간생성및노선등록_등록안되는경우2() throws Exception {
        //given
        Station upStation = new Station(1L, "상행종점역");
        Station downStation = new Station(2L, "하행종점역");
        Section section = new Section(1L, 100);
        Line line = Line.createWithSectionAndStation("테스트노선", "테스트색", section, upStation, downStation);

        Section newSection = new Section(2L, 50);

        //when
        //then
        assertThatThrownBy(() -> line.register(upStation, downStation, newSection))
                .isInstanceOf(IllegalStateException.class);
    }

    @DisplayName("구간 생성 및 노선등록 - 기존 등록된 구간의 길이보다 크거나 같으면 등록될 수 없다.")
    @ParameterizedTest
    @ValueSource(ints = {100, 101})
    public void 구간생성및노선등록_구간길이확인(int distance) throws Exception {
        //given
        Station upStation = new Station(1L, "상행종점역");
        Station downStation = new Station(2L, "하행종점역");
        Section section = new Section(1L, 100);
        Line line = Line.createWithSectionAndStation("테스트노선", "테스트색", section, upStation, downStation);

        Station newStation = new Station(3L, "새로운역");
        Section newSection = new Section(2L, distance);

        //when
        //then
        assertThatThrownBy(() -> line.register(upStation, newStation, newSection))
                .isInstanceOf(IllegalStateException.class);
    }

    @DisplayName("구간 생성 및 노선등록 - 구간길이확인")
    @Test
    public void 구간생성및노선등록_구간길이확인2() throws Exception {
        //given
        Station upStation = new Station(1L, "상행종점역");
        Station downStation = new Station(2L, "하행종점역");
        Section section = new Section(1L, 100);
        Line line = Line.createWithSectionAndStation("테스트노선", "테스트색", section, upStation, downStation);

        Station newStation = new Station(3L, "새로운역");
        Section newSection = new Section(2L, 50);

        //when
        line.register(upStation, newStation, newSection);

        //then
        assertThat(section.distance()).isEqualTo(50);
        assertThat(newSection.distance()).isEqualTo(50);
    }

    @DisplayName("구간 생성 및 노선등록 - 상행종점역으로 등록")
    @Test
    public void 상행종점역으로등록_등록시_등록확인() throws Exception {
        //given
        Station upStation = new Station(1L, "상행종점역");
        Station downStation = new Station(2L, "하행종점역");
        Section section = new Section(1L, 100);
        Line line = Line.createWithSectionAndStation("테스트노선", "테스트색", section, upStation, downStation);

        Station newStation = new Station(3L, "새로운역");
        Section newSection = new Section(2L, 50);

        //when
        line.register(newStation, upStation, newSection);

        //then
        assertThat(line.sortedStationList()).containsExactly(newStation, upStation, downStation);
    }

    @DisplayName("구간 생성 및 노선등록 - 하행종점역으로 등록")
    @Test
    public void 하행종점역으로등록_등록시_등록확인() throws Exception {
        //given
        Station upStation = new Station(1L, "상행종점역");
        Station downStation = new Station(2L, "하행종점역");
        Section section = new Section(1L, 100);
        Line line = Line.createWithSectionAndStation("테스트노선", "테스트색", section, upStation, downStation);

        Station newStation = new Station(3L, "새로운역");
        Section newSection = new Section(2L, 50);

        //when
        line.register(downStation, newStation, newSection);

        //then
        assertThat(line.sortedStationList()).containsExactly(upStation, downStation, newStation);
    }

    @DisplayName("구간 생성 및 노선등록 - 역 중간에 등록 - 이미존재하는 역이 상행역인경우")
    @Test
    public void 중간역으로등록_등록시_등록확인() throws Exception {
        //given
        Station upStation = new Station(1L, "상행종점역");
        Station downStation = new Station(2L, "하행종점역");
        Section section = new Section(1L, 100);
        Line line = Line.createWithSectionAndStation("테스트노선", "테스트색", section, upStation, downStation);

        Station newStation = new Station(3L, "새로운역");
        Section newSection = new Section(2L, 50);

        //when
        line.register(upStation, newStation, newSection);

        //then
        assertThat(line.sortedStationList()).containsExactly(upStation, newStation, downStation);
    }

    @DisplayName("구간 생성 및 노선등록 - 역 중간에 등록 - 이미존재하는 역이 하행역인경우")
    @Test
    public void 중간역으로등록_등록시_등록확인2() throws Exception {
        //given
        Station upStation = new Station(1L, "상행종점역");
        Station downStation = new Station(2L, "하행종점역");
        Section section = new Section(1L, 100);
        Line line = Line.createWithSectionAndStation("테스트노선", "테스트색", section, upStation, downStation);

        Station newStation = new Station(3L, "새로운역");
        Section newSection = new Section(2L, 50);

        //when
        line.register(newStation, downStation, newSection);

        //then
        assertThat(line.sortedStationList()).containsExactly(upStation, newStation, downStation);
    }
}
