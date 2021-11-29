package nextstep.subway.line;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.security.InvalidParameterException;
import java.util.Objects;
import nextstep.subway.line.domain.Distance;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineStation;
import nextstep.subway.line.domain.LineStations;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class LineTest {

    private static Long FIRST_ID = 1L;
    private static Long SECOND_ID = 2L;
    private static Long LAST_ID = 3L;

    public static final String LINE_NAME1 = "2호선";
    public static final String LINE_NAME2 = "5호선";
    public static final String LINE_COLOR1 = "orange darken-4";
    public static final String LINE_COLOR2 = "yellow darken-3";
    public static final Line LINE1 = new Line(LINE_NAME1, LINE_COLOR1);
    public static final Line LINE2 = new Line(LINE_NAME2, LINE_COLOR2);

    @Test
    @DisplayName("Line 생성 후 name,color 검증")
    void 핵심파라미터_일치_검증() {
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
    void 파라미터_업데이트_검증() {
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
    void 빈값_검증() {
        assertAll(
            () -> assertThrows(InvalidParameterException.class, () -> new Line("", LINE_COLOR1)),
            () -> assertThrows(InvalidParameterException.class, () -> new Line(LINE_NAME1, ""))
        );
    }

    @Test
    @DisplayName("soft delete 테스트, delete() 호출 후 isDelete true(삭제됨) 반환 검증")
    void 삭제_검증() {
        // given
        Line line = LINE1;

        // when
        line.delete();

        // then
        assertThat(line.isDeleted()).isTrue();
    }

    @Test
    @DisplayName("getStationIds 메소드는 상행부터 하행순으로 정렬되어 반환된다.")
    void 구간_지하철역_정렬() {
        // given
        Line line = new Line(LineTest.LINE_NAME1, LineTest.LINE_COLOR1);
        LineStation lineStation = LineStation.of(1L, 2L, Distance.of(100));
        LineStation lineStation2 = LineStation.of(2L, 3L, Distance.of(100));

        // when
        line.addLineStation(lineStation);
        line.addLineStation(lineStation2);

        // then
        assertThat(line.getStationIds()).containsExactly(1L, 2L, 3L);
    }


    @Test
    @DisplayName("LineStation 추가 테스트, `Sections` 에는 null 인 `nextStation`이 1개 존재해야함")
    void 마지막역_nextStationId_null() {
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
    void 상행_하행_연결_할_역이_없는_경우() {
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
    void 중복으로_추가_실패() {
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
    void 구간사이_길이_실패() {
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


    @Test
    @DisplayName("삭제역 없을때 실패")
    void 삭제_역이_없음() {
        // given
        LineStations lineStations = new LineStations();
        lineStations.add(new LineStation(FIRST_ID, LAST_ID, Distance.of(100)));

        // when
        // then
        assertThrows(InvalidParameterException.class, () -> lineStations.remove(100L));
    }

}
