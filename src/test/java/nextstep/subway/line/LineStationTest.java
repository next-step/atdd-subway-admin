package nextstep.subway.line;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.security.InvalidParameterException;
import nextstep.subway.line.domain.Distance;
import nextstep.subway.line.domain.LineStation;
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
}
