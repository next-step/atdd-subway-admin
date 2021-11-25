package nextstep.subway.line;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.security.InvalidParameterException;
import java.util.List;
import java.util.Objects;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.Section;
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
    @DisplayName("Section 추가 테스트, `Sections` 에는 추가된 `Section` 하나와  null 인 `nextStation`이 1개 존재해야함")
    void addSection() {
        // given
        Line line = new Line(LINE_NAME1, LINE_COLOR1);
        Section section = SectionTest.SECTION1;

        // when
        line.addSection(section);
        List<Section> sections = line.getSections();

        // then
        assertAll(
            () -> assertThat(sections).contains(section),
            () -> assertThat(sections.stream().filter(it -> Objects.isNull(it.getNextStation()))
                .count()).isEqualTo(1)
        );
    }
}
