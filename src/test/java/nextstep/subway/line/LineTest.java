package nextstep.subway.line;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.security.InvalidParameterException;
import java.util.Objects;
import nextstep.subway.line.domain.Distance;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineStation;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class LineTest {

    public static final String LINE_NAME1 = "2호선";
    public static final String LINE_NAME2 = "5호선";
    public static final String LINE_COLOR1 = "orange darken-4";
    public static final String LINE_COLOR2 = "yellow darken-3";
    public static final Line LINE1 = new Line(LINE_NAME1, LINE_COLOR1);
    public static final Line LINE2 = new Line(LINE_NAME2, LINE_COLOR2);

    @Test
    @DisplayName("Line 생성 후 name,color 검증")
    void create() {
        // given
        // when
        Line actual = new Line(LINE_NAME1, LINE_COLOR1);

        // then
        assertAll(
            () -> assertThat(actual.getName()).isEqualTo(LINE_NAME1),
            () -> assertThat(actual.getColor()).isEqualTo(LINE_COLOR1)
        );
    }

    @Test
    @DisplayName("update 메소드 호출 후 변경된 name,color 일치 검증")
    void update() {
        // given
        Line actual = new Line(LINE_NAME2, LINE_COLOR2);

        // when
        actual.update(LINE1);

        // then
        assertAll(
            () -> assertThat(actual.getName()).isEqualTo(LINE1.getName()),
            () -> assertThat(actual.getColor()).isEqualTo(LINE1.getColor())
        );
    }

    @Test
    @DisplayName("Line 생성 시 name,color 는 빈값일 경우 에러 발생")
    void validEmpty() {
        assertAll(
            () -> assertThrows(InvalidParameterException.class, () -> new Line("", LINE_COLOR1)),
            () -> assertThrows(InvalidParameterException.class, () -> new Line(LINE_NAME1, ""))
        );
    }

    @Test
    @DisplayName("soft delete 테스트, delete() 호출 후 isDelete true(삭제됨) 반환 검증")
    void deleted() {
        // given
        Line line = LINE1;

        // when
        line.delete();

        // then
        assertThat(line.isDeleted()).isTrue();
    }

    @Test
    @DisplayName("LineStation 추가 테스트, `Sections` 에는 null 인 `nextStation`이 1개 존재해야함")
    void addLineStation() {
        // given
        Line line = new Line(LineTest.LINE_NAME1, LineTest.LINE_COLOR1);
        LineStation lineStation = LineStation.of(1L, 2L, Distance.of(100));

        // when
        line.addLineStation(lineStation);

        // then
        assertThat(line.getStations().stream().filter(it -> Objects.isNull(it.getNextStationId()))
            .count()).isEqualTo(1);
    }

    @Test
    @DisplayName("상행,하행역 둘중 하나도 포함되어있지 않으면 추가할 수 없음")
    void addLineStation_not_position_fail() {
        // given
        Line line = new Line(LineTest.LINE_NAME1, LineTest.LINE_COLOR1);
        line.addLineStation(LineStation.of(1L, 2L, Distance.of(100)));

        // when
        LineStation lineStation = LineStation.of(3L, 4L, Distance.of(100));

        // then
        assertThrows(InvalidParameterException.class, () -> line.addLineStation(lineStation));
    }

    @Test
    @DisplayName("상행역과 하행역이 이미 노선에 모두 등록되어 있다면 추가할 수 없음")
    void addLineStation_duplicate_fail() {
        // given
        Line line = new Line(LineTest.LINE_NAME1, LineTest.LINE_COLOR1);
        line.addLineStation(LineStation.of(1L, 2L, Distance.of(100)));

        // when
        LineStation lineStation = LineStation.of(1L, 2L, Distance.of(100));

        // then
        assertThrows(InvalidParameterException.class, () -> line.addLineStation(lineStation));
    }

    @Test
    @DisplayName("역 사이에 새로운 역을 등록할 경우 기존 역 사이 길이와 같거나 크면 등록을 할 수 없음")
    void addLineStation_distance_fail() {
        // given
        Line line = new Line(LineTest.LINE_NAME1, LineTest.LINE_COLOR1);
        line.addLineStation(LineStation.of(1L, 2L, Distance.of(100)));

        // when
        LineStation sameDistance = LineStation.of(1L, 2L, Distance.of(100));
        LineStation overDistance = LineStation.of(1L, 2L, Distance.of(200));

        // then
        assertAll(
            () -> assertThrows(InvalidParameterException.class,
                () -> line.addLineStation(sameDistance)),
            () -> assertThrows(InvalidParameterException.class,
                () -> line.addLineStation(overDistance))
        );
    }
}
