package nextstep.subway.line;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.security.InvalidParameterException;
import nextstep.subway.line.domain.Section;
import nextstep.subway.station.StationTest;
import nextstep.subway.station.domain.Station;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class SectionTest {

    public final static Section SECTION1 = new Section(LineTest.LINE1, StationTest.STATION1,
        StationTest.STATION2, 100);

    @Test
    @DisplayName("Section 생성시 Line,upStation,distance 필수 값 체크")
    void validateNotNull() {
        // given
        // when
        // then
        assertAll(
            () -> assertThrows(InvalidParameterException.class,
                () -> new Section(null, StationTest.STATION1, StationTest.STATION2, 100)),
            () -> assertThrows(InvalidParameterException.class,
                () -> new Section(LineTest.LINE1, null, StationTest.STATION2, 100)),
            () -> assertThrows(InvalidParameterException.class,
                () -> new Section(LineTest.LINE1, StationTest.STATION1, null, null))
        );
    }

    @Test
    @DisplayName("soft delete 테스트, delete() 호출 후 isDelete true(삭제됨) 반환 검증")
    void deleted() {
        // given
        Section station = new Section(LineTest.LINE1, StationTest.STATION1, null, 100);

        // when
        station.delete();

        // then
        assertThat(station.isDeleted()).isTrue();
    }

    @Test
    @DisplayName("`LastOf` 메소드는, Section 인스턴스를 받아 nextStation 을 기준으로 마지막 구간 `역(station)`을 생성한다, 마지막 구간의 nextStation 은 null 임")
    void lastOf() {
        // given
        Station station = StationTest.STATION1;
        Station lastStation = StationTest.STATION2;
        Section section = new Section(LineTest.LINE1, station, lastStation, 100);

        // when
        Section actual = Section.lastOf(section);

        // then
        assertAll(
            () -> assertThat(actual.getStation()).isEqualTo(lastStation),
            () -> assertThat(actual.getNextStation()).isNull()
        );
    }
}
