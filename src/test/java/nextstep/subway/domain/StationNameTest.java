package nextstep.subway.domain;

import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.assertj.core.api.Assertions.assertThatNoException;

import nextstep.subway.domain.station.StationName;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

class StationNameTest {

    @DisplayName("지하철역 명 일급객체 생성")
    @ParameterizedTest
    @ValueSource(strings = {"역", "역역", "역역역", "서울대입구역", "수서역", "압구정역", "판교역"})
    void generate01(String name) {
        // given & when & then
        assertThatNoException().isThrownBy(() -> StationName.from(name));
    }

    @DisplayName("지하철 역은 null 혹은 빈문자일 수 없다.")
    @ParameterizedTest
    @NullAndEmptySource
    void generate02(String name) {
        // given & when & then
        assertThatIllegalArgumentException()
            .isThrownBy(() -> StationName.from(name))
            .withMessageMatching("지하철 역 명이 비어있습니다.");
    }
}