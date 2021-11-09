package nextstep.subway.line.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.mock;

import java.util.stream.Stream;
import nextstep.subway.common.domain.Name;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

@TestInstance(Lifecycle.PER_CLASS)
@DisplayName("지하철 노선")
class LineTest {

    @Test
    @DisplayName("객체화")
    void instance() {
        assertThatNoException()
            .isThrownBy(() -> Line.of(Name.from("name"), Color.from("color"), mock(Sections.class))
            );
    }

    @ParameterizedTest(name = "[{index}] {argumentsWithNames} 값 으로 객체화 할 수 없다.")
    @MethodSource
    @DisplayName("'null' 인자가 존재한 상태로 객체화하면 IllegalArgumentException")
    void instance_emptyArgument_thrownIllegalArgumentException(
        Name name, Color color, Sections sections) {
        assertThatIllegalArgumentException()
            .isThrownBy(() -> Line.of(name, color, sections))
            .withMessageContaining("must not be null");
    }

    @Test
    @DisplayName("수정")
    void update() {
        //given
        Name updatedName = Name.from("updatedName");
        Color updatedColor = Color.from("updatedColor");
        Line line = Line.of(Name.from("name"), Color.from("color"), mock(Sections.class));

        //when
        line.update(updatedName, updatedColor);

        //then
        assertAll(
            () -> assertThat(line.getName()).isEqualTo(updatedName),
            () -> assertThat(line.getColor()).isEqualTo(updatedColor)
        );
    }

    @ParameterizedTest(name = "[{index}] {argumentsWithNames} 값 으로 수정할 수 없다.")
    @MethodSource
    @DisplayName("'null' 인 이름과 색상을 수정하면 IllegalArgumentException")
    void update_nullArgument_thrownIllegalArgumentException(Name name, Color color) {
        //given
        Line line = Line.of(Name.from("name"), Color.from("color"), mock(Sections.class));

        //when
        ThrowingCallable updateCall = () -> line.update(name, color);

        //then
        assertThatIllegalArgumentException()
            .isThrownBy(updateCall)
            .withMessageEndingWith("must not be null");
    }

    private static Stream<Arguments> instance_emptyArgument_thrownIllegalArgumentException() {
        return Stream.of(
            Arguments.of(null, Color.from("color"), mock(Sections.class)),
            Arguments.of(Name.from("name"), null, mock(Sections.class)),
            Arguments.of(Name.from("name"), Color.from("color"), null)
        );
    }

    private Stream<Arguments> update_nullArgument_thrownIllegalArgumentException() {
        return Stream.of(
            Arguments.of(null, Color.from("color")),
            Arguments.of(Name.from("name"), null)
        );
    }
}
