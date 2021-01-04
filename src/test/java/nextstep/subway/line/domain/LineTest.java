package nextstep.subway.line.domain;

import nextstep.subway.station.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
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

        신분당선 = new Line(1L, "신분당선", "bg-red-600", 강남역.getId(), 역삼역.getId(), 10);
    }
    @DisplayName("최초 구간 등록을 하는 경우")
    @Test
    void addLineStation1() {
        //then
        List<LineStation> lineStations = 신분당선.getLineStations().getLineStationsInOrder();

        구간_검증(lineStations.get(0), null, 강남역.getId(), 0);
        구간_검증(lineStations.get(1), 강남역.getId(), 역삼역.getId(), 10);
    }

    @DisplayName("역 사이에 새로운 역을 등록할 경우")
    @Test
    void addLineStation2() {
        //given / when
        신분당선.addLineStation(강남역.getId(), 잠실역.getId(), 6);

        //then
        List<LineStation> lineStations = 신분당선.getLineStations().getLineStationsInOrder();

        구간_검증(lineStations.get(0), null, 강남역.getId(), 0);
        구간_검증(lineStations.get(1), 강남역.getId(), 잠실역.getId(), 6);
        구간_검증(lineStations.get(2), 잠실역.getId(), 역삼역.getId(), 4);
    }

    @DisplayName("역 사이에 새로운 역을 등록할 경우 2")
    @Test
    void addLineStation3() {
        //given / when
        신분당선.addLineStation(잠실역.getId(), 역삼역.getId(), 6);

        //then
        List<LineStation> lineStations = 신분당선.getLineStations().getLineStationsInOrder();

        구간_검증(lineStations.get(0), null, 강남역.getId(), 0);
        구간_검증(lineStations.get(1), 강남역.getId(), 잠실역.getId(), 4);
        구간_검증(lineStations.get(2), 잠실역.getId(), 역삼역.getId(), 6);
    }

    @DisplayName("새로운 역을 상행 종점으로 등록할 경우")
    @Test
    void addLineStation4() {
        //given / when
        신분당선.addLineStation(잠실역.getId(), 강남역.getId(), 6);

        //then
        List<LineStation> lineStations = 신분당선.getLineStations().getLineStationsInOrder();

        구간_검증(lineStations.get(0), null, 잠실역.getId(), 0);
        구간_검증(lineStations.get(1), 잠실역.getId(), 강남역.getId(), 6);
        구간_검증(lineStations.get(2), 강남역.getId(), 역삼역.getId(), 10);
    }


    @DisplayName("새로운 역을 하행 종점으로 등록할 경우")
    @Test
    void addLineStation5() {
        //given / when
        신분당선.addLineStation(역삼역.getId(), 잠실역.getId(), 6);

        //then
        List<LineStation> lineStations = 신분당선.getLineStations().getLineStationsInOrder();

        구간_검증(lineStations.get(0), null, 강남역.getId(), 0);
        구간_검증(lineStations.get(1), 강남역.getId(), 역삼역.getId(), 10);
        구간_검증(lineStations.get(2), 역삼역.getId(), 잠실역.getId(), 6);
    }

    @DisplayName("역 사이에 새로운 역을 등록할 경우 기존 역 사이 길이보다 크거나 같으면 등록을 할 수 없음")
    @Test
    void verifyAddLineStation1() {
        assertThatThrownBy(() -> {
            신분당선.addLineStation(강남역.getId(), 잠실역.getId(), 10);
        }).isInstanceOf(RuntimeException.class);
    }

    @DisplayName("역 사이에 새로운 역을 등록할 경우 기존 역 사이 길이보다 크거나 같으면 등록을 할 수 없음 2")
    @Test
    void verifyAddLineStation2() {
        assertThatThrownBy(() -> {
            신분당선.addLineStation(잠실역.getId(), 역삼역.getId(), 10);
        }).isInstanceOf(RuntimeException.class);
    }

    @DisplayName("상행역과 하행역이 이미 노선에 모두 등록되어 있다면 추가할 수 없음")
    @Test
    void verifyAddLineStation3() {
        assertThatThrownBy(() -> {
            신분당선.addLineStation(강남역.getId(), 역삼역.getId(), 3);
        }).isInstanceOf(RuntimeException.class);
    }

    @DisplayName("상행역과 하행역 둘 중 하나도 포함되어있지 않으면 추가할 수 없음")
    @Test
    void verifyAddLineStation4() {
        Station 교대역 = new Station(4L, "교대역");
        assertThatThrownBy(() -> {
            신분당선.addLineStation(잠실역.getId(), 교대역.getId(), 3);
        }).isInstanceOf(RuntimeException.class);
    }

    @DisplayName("노선의 구간을 제거하는 기능 - 중간역이 제거될 경우")
    @Test
    void removeLineStation1() {
        //given: 강남 <- 6 -> 잠실 <- 4 -> 역삼
        신분당선.addLineStation(강남역.getId(), 잠실역.getId(), 6);

        //when
        신분당선.removeLineStation(잠실역.getId());

        //then: 강남 <- 10 -> 역삼
        List<LineStation> lineStations = 신분당선.getLineStations().getLineStationsInOrder();
        assertThat(lineStations.size()).isEqualTo(2);
        구간_검증(lineStations.get(0), null, 강남역.getId(), 0);
        구간_검증(lineStations.get(1), 강남역.getId(), 역삼역.getId(), 10);
    }


    @DisplayName("노선의 구간을 제거하는 기능 - Top 종점이 제거될 경우")
    @Test
    void removeLineStation2() {
        //given: 강남 <- 6 -> 잠실 <- 4 -> 역삼
        신분당선.addLineStation(강남역.getId(), 잠실역.getId(), 6);

        //when
        신분당선.removeLineStation(강남역.getId());

        //then: 잠실 <- 4 -> 역삼
        List<LineStation> lineStations = 신분당선.getLineStations().getLineStationsInOrder();
        assertThat(lineStations.size()).isEqualTo(2);
        구간_검증(lineStations.get(0), null, 잠실역.getId(), 0);
        구간_검증(lineStations.get(1), 잠실역.getId(), 역삼역.getId(), 4);
    }


    @DisplayName("노선의 구간을 제거하는 기능 - Tail 종점이 제거될 경우")
    @Test
    void removeLineStation3() {
        //given: 강남 <- 6 -> 잠실 <- 4 -> 역삼
        신분당선.addLineStation(강남역.getId(), 잠실역.getId(), 6);

        //when
        신분당선.removeLineStation(역삼역.getId());

        //then: 강남 <- 6 -> 잠실
        List<LineStation> lineStations = 신분당선.getLineStations().getLineStationsInOrder();
        assertThat(lineStations.size()).isEqualTo(2);
        구간_검증(lineStations.get(0), null, 강남역.getId(), 0);
        구간_검증(lineStations.get(1), 강남역.getId(), 잠실역.getId(), 6);
    }

    private void 구간_검증(LineStation lineStation, Long preStationId, Long stationId, Integer distance) {
        assertThat(lineStation.getPreStationId()).isEqualTo(preStationId);
        assertThat(lineStation.getStationId()).isEqualTo(stationId);
        assertThat(lineStation.getDistance()).isEqualTo(distance);
    }
}