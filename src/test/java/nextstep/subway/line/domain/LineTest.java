package nextstep.subway.line.domain;

import nextstep.subway.station.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class LineTest {

    private Station 강남역;
    private Station 역삼역;
    private Station 잠실역;
    private Line 신분당선;

    @BeforeEach
    void setUp() {
        강남역 = new Station(1L, "강남역");
        역삼역 = new Station(2L, "역삼역");
        잠실역 = new Station(3L, "잠실역");

        신분당선 = new Line(1L, "신분당선", "bg-red-600");
        신분당선.addLineStation(강남역.getId(), 역삼역.getId(), 10);
    }

    @DisplayName("역 사이에 새로운 역을 등록할 경우")
    @Test
    void addLineStation() {
        //given / when
        신분당선.addLineStation(강남역.getId(), 잠실역.getId(), 6);

        //then
        List<LineStation> lineStations = 신분당선.getLineStations().getLineStations();

        구간_검증(lineStations.get(1), 강남역.getId(), 잠실역.getId(), 6);
        구간_검증(lineStations.get(0), 잠실역.getId(), 역삼역.getId(), 4);
    }

    private void 구간_검증(LineStation lineStation, Long preStationId, Long stationId, Integer distance) {
        assertThat(lineStation.getPreStationId()).isEqualTo(preStationId);
        assertThat(lineStation.getStationId()).isEqualTo(stationId);
        assertThat(lineStation.getDistance()).isEqualTo(distance);
    }
}