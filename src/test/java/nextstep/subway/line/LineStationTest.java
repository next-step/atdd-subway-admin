package nextstep.subway.line;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.security.InvalidParameterException;
import nextstep.subway.line.domain.Distance;
import nextstep.subway.line.domain.LineStation;
import nextstep.subway.line.domain.LineStations;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class LineStationTest {

    private static Long FIRST_ID = 1L;
    private static Long SECOND_ID = 2L;
    private static Long LAST_ID = 3L;

    @Test
    @DisplayName("stationId는 필수 값")
    void 기준역_null_실패() {
        // given
        // when
        // then
        assertThrows(InvalidParameterException.class,
            () -> new LineStation(null, 2L, Distance.of(100)));
    }

    @Test
    @DisplayName("soft delete 테스트, delete() 호출 후 isDelete true(삭제됨) 반환 검증")
    void 삭제_검증() {
        // given
        LineStation station = new LineStation(1L, null, Distance.of(100));

        // when
        station.delete();

        // then
        assertThat(station.isDeleted()).isTrue();
    }


    @Test
    @DisplayName("하행 종점 추가시 추가 시점 하행 종점 stationId 는 추가할려는 구간의 nextStationId 가 된다. lastLineStationUpdate 는 그때 사용되는 메서드임")
    void 새로운_하행_종점역_업데이트() {
        // given
        LineStation lineStation = new LineStation(2L, null, Distance.of(100));
        LineStation newLastLineStation = new LineStation(2L, 3L, Distance.of(100));

        // when
        lineStation.lastLineStationUpdate(newLastLineStation);

        // then
        assertThat(newLastLineStation.getNextStationId()).isEqualTo(lineStation.getStationId());
    }

    @Test
    @DisplayName("중간 구간에 역추가시, 기존구간은 추가할려는 구간의 길이보다 같거난 크면 에러")
    void 중간역_추가시_구간사이_길이_에러() {
        // given
        LineStation lineStation = new LineStation(1L, 3L, Distance.of(100));

        // when
        LineStation sameDistanceAddLineStation = new LineStation(2L, 3L, Distance.of(100));
        LineStation overDistanceAddLineStation = new LineStation(2L, 3L, Distance.of(200));

        // then
        assertAll(
            () -> assertThrows(InvalidParameterException.class,
                () -> lineStation.pushNextStationId(sameDistanceAddLineStation)),
            () -> assertThrows(InvalidParameterException.class,
                () -> lineStation.pushNextStationId(overDistanceAddLineStation))
        );
    }

    @Test
    @DisplayName("마지막역 제거시, 마지막 구간 이전 구간의 nextStationId 가 새로운 마지막 구간 stationId 가 된다.")
    void 마지막역_제거() {
        // given
        LineStations lineStations = new LineStations();
        lineStations.add(new LineStation(FIRST_ID, SECOND_ID, Distance.of(100)));
        lineStations.add(new LineStation(SECOND_ID, LAST_ID, Distance.of(100)));

        // when
        lineStations.remove(LAST_ID);
        LineStation actual = lineStations.findLineStationByStationId(SECOND_ID).get();

        // then
        assertAll(
            () -> assertThat(actual.getStationId()).isEqualTo(SECOND_ID),
            () -> assertThat(actual.getNextStationId()).isNull()
        );
    }

    @Test
    @DisplayName("구간 하나일때 제거 실패")
    void 구간_하나일때_제거_실패() {
        // given
        LineStations lineStations = new LineStations();
        lineStations.add(new LineStation(FIRST_ID, LAST_ID, Distance.of(100)));

        // when
        // then
        assertThrows(InvalidParameterException.class, () -> lineStations.remove(LAST_ID));
    }
}
