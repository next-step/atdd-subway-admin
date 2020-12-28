package nextstep.subway.line.domain;

import nextstep.subway.station.domain.Station;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("지하철 노선에 대한 테스트")
class LineTest {

    @DisplayName("지하철 노선을 생성합니다.")
    @Test
    void create() {
        // given
        String name = "1호선";
        String color = "blue";
        Section section = Section.builder()
                .upStation(new Station("청량리역"))
                .downStation(new Station("신창역"))
                .distance(Distance.valueOf(100))
                .build();

        // when
        Line line = Line.of(name, color, section);

        // then
        assertAll(
                () -> assertThat(line).isNotNull(),
                () -> assertThat(line.getName()).isEqualTo(name),
                () -> assertThat(line.getColor()).isEqualTo(color),
                () -> assertThat(line.getLineStations()).isNotNull()
        );
    }

    @DisplayName("지하철 노선을 업데이트합니다.")
    @Test
    void update() {
        // given
        Line line1 = 지하철_1호선_생성됨();
        Line line2 = 지하철_2호선_생성됨();

        // when
        line1.update(line2);

        // then
        assertAll(
                () -> assertThat(line1).isNotNull(),
                () -> assertThat(line1.getName()).isEqualTo(line2.getName()),
                () -> assertThat(line1.getColor()).isEqualTo(line2.getColor())
        );
    }

    @DisplayName("지하철 노선의 구간을 추가합니다.")
    @Test
    void addSection() {
        // given
        Station upStation = new Station("청량리역");
        Station downStation = new Station("신도림역");
        Distance distance = Distance.valueOf(10);
        Section section = Section.builder()
                .upStation(upStation)
                .downStation(downStation)
                .distance(distance)
                .build();
        Line line = 지하철_1호선_생성됨();

        // when
        line.add(section);


        // then
        LineStations lineStations = line.getLineStations();
        LineStation lineStation = new LineStation(line, section);
        assertThat(lineStations.getLineStations()).contains(lineStation);
    }

    public static Line 지하철_1호선_생성됨() {
        String name = "1호선";
        String color = "blue";
        Section section = Section.builder()
                .upStation(new Station("청량리역"))
                .downStation(new Station("신창역"))
                .distance(Distance.valueOf(100))
                .build();

        return Line.of(name, color, section);
    }

    public static Line 지하철_2호선_생성됨() {
        String name = "2호선";
        String color = "green";
        Section section = Section.builder()
                .upStation(new Station("강남역"))
                .downStation(new Station("홍대역"))
                .distance(Distance.valueOf(200))
                .build();

        return Line.of(name, color, section);
    }
}
