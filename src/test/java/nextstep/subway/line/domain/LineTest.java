package nextstep.subway.line.domain;

import nextstep.subway.station.domain.Station;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class LineTest {

    @Test
    void newLineTest() {
        // line에 section이 포함되어 생성되는지 확인

        Station 강남역 = new Station("강남역");
        Station 정자역 = new Station("정자역");

        Line line = new Line("신분당선", "red", 강남역, 정자역, 10);
        List<Station> stations = line.getStations();
        assertAll(
                () -> assertThat(stations.size()).isEqualTo(2),
                () -> assertThat(stations.get(0).getName()).isEqualTo("강남역"),
                () -> assertThat(stations.get(1).getName()).isEqualTo("정자역")
        );
    }
}