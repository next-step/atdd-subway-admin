package nextstep.subway.line.domain;

import nextstep.subway.station.domain.Station;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class LineTest {

    @DisplayName("지하철 노선 역 목록 조회")
    @Test
    void getStations() {
        Line line = new Line("4호선", "blue");
        Station upStationSadang = new Station("사당역");
        Station downStationAnsan = new Station("안산역");
        Section firstSection = new Section(upStationSadang, downStationAnsan, 10);
        line.addSection(firstSection);

        Station upStationAnsan = new Station("안산역");
        Station downStationOido = new Station("오이도역");
        Section secondSection = new Section(upStationAnsan, downStationOido, 15);
        line.addSection(secondSection);

        List<Station> stations = line.getStations();

        assertThat(stations).containsExactly(upStationSadang,upStationAnsan,downStationOido);
    }

}
