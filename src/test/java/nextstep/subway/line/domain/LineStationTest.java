package nextstep.subway.line.domain;

import nextstep.subway.station.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철 노선 구간에 대한 테스트")
class LineStationTest {

    private Line line1;

    private LineStation line1Station;

    @BeforeEach
    void setUp() {
        line1 = LineTest.지하철_1호선_생성됨();
        line1Station = line1.getLineStations().getLineStations().get(0);
    }

    @DisplayName("지하철 노선 구간을 생성합니다.")
    @Test
    void create() {
        // given
        Station station1 = new Station("금정역");
        Station station2 = new Station("당정역");
        Distance distance = Distance.valueOf(100);
        Section section = Section.builder()
                .upStation(station1)
                .downStation(station2)
                .distance(distance)
                .build();

        // when
        LineStation lineStation = new LineStation(line1, section);

        // then
        assertThat(lineStation).isNotNull();
    }

    @DisplayName("지하철 노선 구간에 같은 노선과 같은 구간이라면 동등성을 보장합니다.")
    @Test
    void equals() {
        // given
        Station station1 = new Station("금정역");
        Station station2 = new Station("당정역");

        Section section1 = Section.builder()
                .upStation(station1)
                .downStation(station2)
                .distance(Distance.valueOf(100))
                .build();
        Section section2 = Section.builder()
                .upStation(station1)
                .downStation(station2)
                .distance(Distance.valueOf(200))
                .build();

        LineStation lineStation1 = new LineStation(line1, section1);
        LineStation lineStation2 = new LineStation(line1, section2);

        // when
        boolean equals = lineStation1.equals(lineStation2);

        // then
        assertThat(equals).isTrue();
    }

    @DisplayName("지하철 노선 구간에 같은 노선과 같은 구간이 아니라면 동등성을 보장하지 않습니다.")
    @Test
    void equalsNot() {
        // given
        Station station1 = new Station("금정역");
        Station station2 = new Station("당정역");
        Station station3 = new Station("관악역");

        Section section1 = Section.builder()
                .upStation(station1)
                .downStation(station2)
                .distance(Distance.valueOf(100))
                .build();
        Section section2 = Section.builder()
                .upStation(station1)
                .downStation(station3)
                .distance(Distance.valueOf(200))
                .build();

        LineStation lineStation1 = new LineStation(line1, section1);
        LineStation lineStation2 = new LineStation(line1, section2);

        // when
        boolean equals = lineStation1.equals(lineStation2);

        // then
        assertThat(equals).isFalse();
    }

    @DisplayName("새로운 지하철 구간을 반영할 수 있는지 확인한다.")
    @Test
    void canAddBetweenSection() {
        // given
        Station station1 = new Station("청량리역");
        Station station2 = new Station("신도림역");
        Distance distance1 = Distance.valueOf(10);
        Section section1 = Section.builder()
                .upStation(station1)
                .downStation(station2)
                .distance(distance1)
                .build();

        Station station3 = new Station("당정역");
        Station station4 = new Station("신창역");
        Distance distance2 = Distance.valueOf(10);
        Section section2 = Section.builder()
                .upStation(station3)
                .downStation(station4)
                .distance(distance2)
                .build();

        // when
        boolean result1 = line1Station.canAddBetweenSection(section1);
        boolean result2 = line1Station.canAddBetweenSection(section2);

        // then
        assertThat(result1).isTrue();
        assertThat(result2).isTrue();
    }

    @DisplayName("새로운 지하철 구간을 반영하여 기존 지하철 노선 구간을 변경한다.")
    @Test
    void update() {
        // given
        Station station1 = new Station("청량리역");
        Station station2 = new Station("신도림역");
        Distance distance = Distance.valueOf(10);
        Section section = Section.builder()
                .upStation(station1)
                .downStation(station2)
                .distance(distance)
                .build();

        // when
        line1Station.update(section);

        // then
        assertThat(line1Station.getUpStation()).isEqualTo(station2);
        assertThat(line1Station.getDownStation()).isEqualTo(line1Station.getDownStation());
        assertThat(line1Station.getSection().getDistance()).isEqualTo(Distance.valueOf(90));
    }
}
