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


    @Test
    @DisplayName("LineStation 생성시 station")
    void validateNotNull() {
        // given
        // when
        // then
        assertThrows(InvalidParameterException.class,
            () -> new LineStation(null, 2L, Distance.of(100)));
    }

    @Test
    @DisplayName("soft delete 테스트, delete() 호출 후 isDelete true(삭제됨) 반환 검증")
    void deleted() {
        // given
        LineStation station = new LineStation(1L, null, Distance.of(100));

        // when
        station.delete();

        // then
        assertThat(station.isDeleted()).isTrue();
    }

    @Test
    @DisplayName("기준이 되는 역은 필수 값, 없으면 실패")
    void stationId_null_fail() {
        //then
        assertThrows(InvalidParameterException.class,
            () -> LineStation.of(null, 1L, Distance.of(100)));
    }

    @Test
    @DisplayName("마지막 구간 추가시 stationId 는 추가할려는 구간의 nextStationId 가 된다. lastLineStationUpdate 는 그때 사용되는 메서드임")
    void stationIdUpdate() {
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
    void pushNextStationId() {
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
    void last_lineStation_remove() {
        // given
        Long firstId = 1L;
        Long secondId = 2L;
        Long lastId = 3L;
        LineStations lineStations = new LineStations();
        lineStations.add(new LineStation(firstId, secondId, Distance.of(100)));
        lineStations.add(new LineStation(secondId, lastId, Distance.of(100)));

        // when
        lineStations.remove(lastId);
        LineStation actual = lineStations.findLineStationByStationId(secondId).get();

        // then
        assertAll(
            () -> assertThat(actual.getStationId()).isEqualTo(secondId),
            () -> assertThat(actual.getNextStationId()).isNull()
        );
    }


}
