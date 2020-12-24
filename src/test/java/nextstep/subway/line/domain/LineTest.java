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
        int distance = 100;
        Station upStation = new Station("청량리역");
        Station downStation = new Station("신창역");
        LastStation lastStation = new LastStation(upStation, downStation);

        // when
        Line line = Line.of(name, color, distance, lastStation);

        // then
        assertAll(
                () -> assertThat(line).isNotNull(),
                () -> assertThat(line.getName()).isEqualTo(name),
                () -> assertThat(line.getColor()).isEqualTo(color),
                () -> assertThat(line.getDistance()).isEqualTo(distance),
                () -> assertThat(line.getLastStation()).isEqualTo(lastStation),
                () -> assertThat(line.getLineStations().getLineStations()).contains(LineStation.of(line, upStation, downStation, distance))
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
                () -> assertThat(line1.getColor()).isEqualTo(line2.getColor()),
                () -> assertThat(line1.getDistance()).isEqualTo(line2.getDistance()),
                () -> assertThat(line1.getLastStation()).isEqualTo(line2.getLastStation()),
                () -> assertThat(line1.getLineStations()).isEqualTo(line2.getLineStations())
        );
    }

    @DisplayName("지하철 노선의 구간을 추가합니다.")
    @Test
    void addSection() {
        // given
        Line line = 지하철_1호선_생성됨();
        Station upStation = new Station("신도림역");
        Station downStation = new Station("문래역");
        int distance = 200;

        // when
        line.addLineStation(upStation, downStation, distance);


        // then
        assertThat(line.getLineStations().getLineStations()).contains(LineStation.of(line, upStation, downStation, distance));
    }

    public static Line 지하철_1호선_생성됨() {
        String name = "1호선";
        String color = "blue";
        int distance = 100;
        Station upStation = new Station("청량리역");
        Station downStation = new Station("신창역");
        LastStation lastStation = new LastStation(upStation, downStation);
        return Line.of(name, color, distance, lastStation);
    }

    public static Line 지하철_2호선_생성됨() {
        String name = "2호선";
        String color = "green";
        int distance = 200;
        Station upStation = new Station("강남역");
        Station downStation = new Station("홍대역");
        LastStation lastStation = new LastStation(upStation, downStation);
        return Line.of(name, color, distance, lastStation);
    }
}
