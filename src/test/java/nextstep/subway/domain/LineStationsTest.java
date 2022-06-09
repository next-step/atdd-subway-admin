package nextstep.subway.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class LineStationsTest {
    private final static Station station1 = new Station("삼성역");
    private final static Station station2 = new Station("선릉역");
    private final static Station station3 = new Station("역삼역");
    private final static Station station4 = new Station("강남역");

    @DisplayName("지하철의 구간 목록에 새로운 시작점을 추가하면 해당 구간이 시작점으로 추가 되어야 한다")
    @Test
    void addFirstLineStationTest() {
        // given
        LineStations lineStations =  new LineStations();
        lineStations.add(station2, station3, new Distance(10L));
        lineStations.add(station3, station4, new Distance(10L));

        // when
        lineStations.add(station1, station2, new Distance(10L));

        // then
        assertThat(lineStations.getStationsSortedByUpToDown()).containsExactly(station1, station2, station3, station4);
    }

    @DisplayName("지하철의 구간 목록에 새로운 종점을 추가하면 해당 구간이 종점으로 추가 되어야 한다")
    @Test
    void addLastLineStationTest() {
        // given
        LineStations lineStations =  new LineStations();
        lineStations.add(station1, station2, new Distance(10L));
        lineStations.add(station2, station3, new Distance(10L));

        // when
        lineStations.add(station3, station4, new Distance(10L));

        // then
        assertThat(lineStations.getStationsSortedByUpToDown()).containsExactly(station1, station2, station3, station4);
    }

    @DisplayName("지하철의 구간 목록에 중간 구간을 추가하면 해당 구간 해당 위치에 정상 추가되어야 한다")
    @Test
    void addMiddleLineStationTest() {
        // given
        LineStations lineStations =  new LineStations();
        lineStations.add(station1, station3, new Distance(10L));
        lineStations.add(station3, station4, new Distance(10L));

        // when
        lineStations.add(station1, station2, new Distance(5L));

        // then
        assertThat(lineStations.getStationsSortedByUpToDown()).containsExactly(station1, station2, station3, station4);
    }

    @DisplayName("지하철의 구간 목록에 중간 구간을 추가할 때 기존의 구간 길이보다 크거나 같은 구간을 추가하면 예외가 발생해야 한다")
    @Test
    void addMiddleByLongerDistanceLineStationTest() {
        // given
        LineStations lineStations =  new LineStations();
        lineStations.add(station1, station3, new Distance(10L));

        // then
        assertThatThrownBy(() -> lineStations.add(station1, station2, new Distance(10L)))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> lineStations.add(station1, station2, new Distance(11L)))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("지하철 노선에 존재하지 않는 지하철을 가진 구간을 추가하면 예외가 발생해야 한다")
    @Test
    void addNotContainLineStationTest() {
        // given
        LineStations lineStations =  new LineStations();
        lineStations.add(station1, station2, new Distance(10L));

        // then
        assertThatThrownBy(() -> lineStations.add(station3, station4, new Distance(10L)))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("2개 이상의 구간을 가진 지하철 노선에 시작점을 지우면 해당 구간은 삭제되고 다음 구간이 시작점이 되어야 한다")
    @Test
    void removeStartLineStation() {
        // given
        LineStations lineStations =  new LineStations();
        lineStations.add(station1, station2, new Distance(10L));
        lineStations.add(station2, station3, new Distance(10L));

        // when
        lineStations.deleteLineStation(station1);

        // then
        assertThat(lineStations.getStationsSortedByUpToDown()).containsExactly(station2, station3);
        assertThat(findLineStationByUpStation(lineStations, station2).isStartSection()).isTrue();
    }

    @DisplayName("2개 이상의 구간을 가진 지하철 노선에 종점을 지우면 해당 구간은 삭제되고 이전 구간이 종점이 되어야 한다")
    @Test
    void removeLastLineStation() {
        // given
        LineStations lineStations =  new LineStations();
        lineStations.add(station1, station2, new Distance(10L));
        lineStations.add(station2, station3, new Distance(10L));

        // when
        lineStations.deleteLineStation(station3);

        // then
        assertThat(lineStations.getStationsSortedByUpToDown()).containsExactly(station1, station2);
        assertThat(findLineStationByUpStation(lineStations, station1).isLastSection()).isTrue();
    }

    @DisplayName("3개 이상의 구간을 가진 지하철 노선의 중간 노선을 지우면 해당 구간은 삭제되고 해당 노선의 이전 노선은 삭제된 노선의 길이만큼 증가되어야 한다")
    @Test
    void removeMiddleLineStation() {
        // given
        LineStations lineStations =  new LineStations();
        lineStations.add(station1, station2, new Distance(10L));
        lineStations.add(station2, station3, new Distance(10L));
        lineStations.add(station3, station4, new Distance(10L));

        // when
        lineStations.deleteLineStation(station2);

        // then
        assertThat(lineStations.getStationsSortedByUpToDown()).containsExactly(station1, station3, station4);
        assertThat(findLineStationByUpStation(lineStations, station1).getDistance().getValue()).isEqualTo(20L);
    }

    @DisplayName("1개 이하의 노선을 가진 지하철 노선의 노선을 삭제하면 예외가 발생해야 한다")
    @Test
    void removeLineStationWithLessThanMinimumLineStationCountTest() {
        // given
        LineStations lineStations =  new LineStations();
        lineStations.add(station1, station2, new Distance(10L));

        // then
        assertThatThrownBy(() -> lineStations.deleteLineStation(station1)).isInstanceOf(IllegalArgumentException.class);
    }

    private LineStation findLineStationByUpStation(LineStations lineStations, Station upStation) {
        return lineStations.getValue()
                .stream()
                .filter(it -> it.isSameUpStation(upStation))
                .findFirst()
                .orElseGet(() -> fail("해당 구간을 찾을 수 없습니다."));
    }
}
