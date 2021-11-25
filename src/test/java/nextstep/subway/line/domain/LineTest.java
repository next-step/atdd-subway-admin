package nextstep.subway.line.domain;

import nextstep.subway.station.domain.Station;
import nextstep.subway.station.dto.StationResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

class LineTest {

    @DisplayName("지하철 노선 역 목록 조회")
    @Test
    void getStations() {
        Station upStationSadang = new Station("사당역");
        Station downStationAnsan = new Station("안산역");
        Line line = new Line("4호선", "blue",upStationSadang,downStationAnsan,10);

        List<String> stationNames = line.getStations()
                .stream()
                .map(StationResponse::getName)
                .collect(Collectors.toList());

        assertThat(stationNames).containsExactly(upStationSadang.getName(), downStationAnsan.getName());
    }

}
