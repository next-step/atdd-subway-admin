package nextstep.subway.line.domain;

import static nextstep.subway.line.domain.exception.LineExceptionMessage.*;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.assertj.core.api.Assertions.assertThatIllegalStateException;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import nextstep.subway.line.domain.exception.LineExceptionMessage;
import nextstep.subway.section.domain.Distance;
import nextstep.subway.section.domain.Section;
import nextstep.subway.station.domain.Station;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;

class LineTest {

    @DisplayName("지하철 노선을 이름과 노선 색상으로 생성할 수 있다.")
    @ParameterizedTest
    @CsvSource(value = {"신분당선,RED", "분당선,YELLOW"})
    void generate01(String name, String color) {
        // given & when
        Line line = Line.of(name, color);

        // then
        assertAll(
            () -> assertEquals(line.getName(), LineName.from(name)),
            () -> assertEquals(line.getColor(), LineColor.from(color))
        );
    }

    @DisplayName("지하철 노선 생성 시 이름이 공란일 수 없다.")
    @ParameterizedTest
    @NullAndEmptySource
    void generate02(String name) {
        // given & when & then
        assertThatIllegalArgumentException().isThrownBy(() -> Line.of(name, "RED"))
            .withMessageContaining(LINE_NAME_IS_NOT_NULL.getMessage());
    }

    @DisplayName("지하철 노선 생성 시 노선 색상이 공란일 수 없다.")
    @ParameterizedTest
    @NullAndEmptySource
    void generate03(String color) {
        // given & when & then
        assertThatIllegalArgumentException().isThrownBy(() -> Line.of("신분당선", color))
            .withMessageContaining(LINE_COLOR_IS_NOT_NULL.getMessage());
    }

    @DisplayName("지하철 노선에 Section을 추가할 수 있다.")
    @Test
    void generate04() {
        // given
        Line 신분당선 = Line.of("신분당선", "RED");
        Station 강남역 = Station.of(1L, "강남역");
        Station 판교역 = Station.of(2L, "판교역");
        Distance distance = Distance.from(10);
        Section 강남역_판교역_구간 = Section.of(강남역, 판교역, distance);

        // when & then
        assertThatNoException().isThrownBy(() -> 신분당선.addSection(강남역_판교역_구간));
    }

    @DisplayName("지하철 노선에 동일한 Section을 추가할 수 없다.")
    @Test
    void generate05() {
        // given
        Line 신분당선 = Line.of("신분당선", "RED");
        Station 강남역 = Station.of(1L, "강남역");
        Station 판교역 = Station.of(2L, "판교역");
        Distance distance = Distance.from(10);
        Section 강남역_판교역_구간 = Section.of(1L, 강남역, 판교역, distance);

        // when
        신분당선.addSection(강남역_판교역_구간);

        // when & then
        assertThatIllegalStateException().isThrownBy(() -> 신분당선.addSection(강남역_판교역_구간))
            .withMessageContaining(ALREADY_ADDED_SECTION.getMessage());
    }

    @DisplayName("지하철 노선에 다른 Section 이지만 이미 존재하는 상/하행역을 추가할 수 없다.")
    @Test
    void generate06() {
        // given
        Line 신분당선 = Line.of("신분당선", "RED");
        Station 강남역 = Station.of(1L, "강남역");
        Station 판교역 = Station.of(2L, "판교역");
        Distance distance = Distance.from(10);
        Section 강남역_판교역_구간 = Section.of(1L, 강남역, 판교역, distance);
        Section 중복된_강남역_판교역_구간 = Section.of(2L, 강남역, 판교역, distance);

        // when
        신분당선.addSection(강남역_판교역_구간);

        // when & then
        assertThatIllegalStateException().isThrownBy(() -> 신분당선.addSection(중복된_강남역_판교역_구간))
            .withMessageContaining(ALREADY_ADDED_UP_DOWN_STATION.getMessage());
    }
}