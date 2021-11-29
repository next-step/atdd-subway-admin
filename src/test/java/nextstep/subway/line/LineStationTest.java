package nextstep.subway.line;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.security.InvalidParameterException;
import nextstep.subway.line.domain.Distance;
import nextstep.subway.line.domain.LineStation;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class LineStationTest {

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
    @DisplayName("구간에 추가 가능여부 체크 검증")
    void isAddableMatch() {
        // given
        LineStation 기존구간 = new LineStation(1L, 2L, Distance.of(100));
        LineStation 상행종점추가구간 = new LineStation(3L, 1L, Distance.of(50));
        LineStation 중간추가구간 = new LineStation(3L, 2L, Distance.of(50));
        LineStation 하행종점추가구간 = new LineStation(2L, 3L, Distance.of(50));

        // when
        // then
        assertAll(
            () -> assertThat(기존구간.isAddableMatch(상행종점추가구간)).isTrue(),
            () -> assertThat(기존구간.isAddableMatch(중간추가구간)).isTrue(),
            () -> assertThat(기존구간.isAddableMatch(하행종점추가구간)).isTrue()
        );
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
    @DisplayName("이전 구간 찾기, nextStationId 가 stationId 와 같으면 이전 구간임")
    void isPre() {
        // given
        LineStation preLineStation = new LineStation(1L, 2L, Distance.of(100));
        LineStation lineStation = new LineStation(2L, 3L, Distance.of(100));

        // when
        boolean actual = preLineStation.isPre(lineStation);

        // then
        assertThat(actual).isTrue();
    }

    @Test
    @DisplayName("중복체크 검증")
    void isDuplicate() {
        // given
        LineStation lineStation = new LineStation(1L, 2L, Distance.of(100));

        // when
        boolean actual = lineStation.isDuplicate(lineStation);

        // then
        assertThat(actual).isTrue();
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
}
