package nextstep.subway.domain.line;

import nextstep.subway.domain.station.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@DisplayName("지하철역 라인 테스트")
class LineTest {
    private LineStation lineStation;
    private LineStation lineStation2;
    private LineStation lineStation3;
    private Line line;

    @BeforeEach
    void setUp() {
        lineStation = 라인스테이션_생성됨(1, 2, 10);
        lineStation2 = 라인스테이션_생성됨(2, 3, 4);
        lineStation3 = 라인스테이션_생성됨(3, 4, 5);

        라인_생성됨("신분당선", "red");
    }

    private void 라인_생성됨(String name, String color) {
        line = new Line("신분당선", "red");
        지하철역_구간_추가됨(lineStation);
        지하철역_구간_추가됨(lineStation2);
        지하철역_구간_추가됨(lineStation3);
    }

    private static LineStation 라인스테이션_생성됨(int l, int l1, int distance) {
        return new LineStation(Long.valueOf(l), Long.valueOf(l1), distance);
    }

    @Test
    @DisplayName("지하철역 삭제 테스트 - 종점인 경우")
    void removeStationTest() {
        // given
        Station stationToDelete = 지하철역_생성됨(4L, "강남");

        // when
        지하철역_삭제됨(stationToDelete);

        // then
        지하철역_삭제_검증됨(2);
    }

    @Test
    @DisplayName("지하철역 삭제 테스트 - 중간역인 경우")
    void removeStationTest2() {
        // given
        Station stationToDelete = 지하철역_생성됨(3L, "판교역");

        // when
        지하철역_삭제됨(stationToDelete);

        // then
        지하철역_삭제_검증됨(2);
    }

    @Test
    @DisplayName("지하철역 삭제 테스트 - 하나의 구간만 존재할경우 에러 발생")
    void removeStationTest3() {
        // given
        Station stationToDelete1 = 지하철역_생성됨(1L, "판교역");
        Station stationToDelete2 = 지하철역_생성됨(2L, "광교역");
        Station stationToDelete3 = 지하철역_생성됨(3L, "광교역");

        // when
        지하철역_삭제됨(stationToDelete1);
        지하철역_삭제됨(stationToDelete2);

        assertThatThrownBy(() -> line.removeStation(stationToDelete3))
                .isInstanceOf(RuntimeException.class);
    }

    @Test
    @DisplayName("지하철역 구간 추가 테스트 - 성공")
    void addLineStationTest() {
        // given
        LineStation lineStation = 라인스테이션_생성됨(4, 5, 5);

        // when
        지하철역_구간_추가됨(lineStation);

        // Then
        지하철구간_추가_검증됨(4);
    }

    private void 지하철역_구간_추가됨(LineStation lineStation) {
        line.addLineStation(lineStation);
    }

    private Station 지하철역_생성됨(Long id, String name) {
        return new Station(id, name);
    }

    private void 지하철역_삭제_검증됨(int size) {
        assertThat(line.getLineStations().size()).isEqualTo(size);
    }

    private void 지하철구간_추가_검증됨(int size) {
        assertThat(line.getLineStations().size()).isEqualTo(size);
    }

    private void 지하철역_삭제됨(Station stationToDelete) {
        line.removeStation(stationToDelete);
    }
}