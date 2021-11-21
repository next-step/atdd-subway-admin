package nextstep.subway.station;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.security.InvalidParameterException;
import nextstep.subway.station.domain.Station;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class StationTest {

    public static final String STATION_NAME1 = "잠실역";
    public static final String STATION_NAME2 = "강남역";
    public static final Station STATION1 = new Station(STATION_NAME1);
    public static final Station STATION2 = new Station(STATION_NAME2);
    ;

    @Test
    @DisplayName("Station 생성 후 name 검증")
    void create() {
        // given
        // when
        Station station = new Station(STATION_NAME1);

        // when
        assertThat(station.getName()).isEqualTo(STATION_NAME1);
    }

    @Test
    @DisplayName("Station 생성 시 name 는 빈값일 경우 에러 발생")
    void validEmpty() {
        assertThrows(InvalidParameterException.class, () -> new Station(""));
    }


    @Test
    @DisplayName("soft delete 테스트, delete() 호출 후 isDelete true(삭제됨) 반환 검증")
    void deleted() {
        // given
        Station station = STATION1;

        // when
        station.delete();

        // then
        assertThat(station.isDeleted()).isTrue();
    }
}
