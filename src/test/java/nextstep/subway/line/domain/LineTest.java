package nextstep.subway.line.domain;

import nextstep.subway.station.domain.Station;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

class LineTest {

    @Test
    void newLineTest() {
        // line에 section이 포함되어 생성되는지 확인

        Station 정자역 = new Station("정자역");
        Station 강남역 = new Station("강남역");

        Line line = new Line("신분당선", "red", 강남역, 정자역, 10);

        assertThat(line.getStations().size()).isEqualTo(2);

    }
}