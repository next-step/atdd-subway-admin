package nextstep.subway.section.domain;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineName;
import nextstep.subway.station.domain.Station;

class SectionTest {
    private static final int DISTANCE = 10;

    private Station upStation;
    private Station downStation;
    private Line line;
    private Distance distance;

    @BeforeEach
    void setUp() {
        upStation = Station.from("판교역");
        downStation = Station.from("정자역");
        line = Line.of("신분당선", "RED");
        distance = Distance.from(DISTANCE);
    }

    @DisplayName("상행역, 하행역, 구간거리 정보로 Section 을 생성한다.")
    @Test
    void create1() {
        // when
        Section section = Section.of(upStation, downStation, distance);

        // then
        assertAll(
            () -> assertEquals(section.getUpStation(), upStation),
            () -> assertEquals(section.getDownStation(), downStation),
            () -> assertTrue(section.isSameDistance(distance))
        );
    }

    @DisplayName("상행역이 없으면 Section 생성 시, 예외가 발생한다.")
    @Test
    void create2() {
        // when & then
        assertThatIllegalArgumentException().isThrownBy(() -> Section.of(null, downStation, distance))
                                            .withMessageContaining("Section 생성에 필요한 필수 정보를 확인해주세요.");
    }

    @DisplayName("하행역이 없으면 Section 생성 시, 예외가 발생한다.")
    @Test
    void create3() {
        // when & then
        assertThatIllegalArgumentException().isThrownBy(() -> Section.of(upStation, null, distance))
                                            .withMessageContaining("Section 생성에 필요한 필수 정보를 확인해주세요.");
    }

    @DisplayName("구간거리가 없으면 Section 생성 시, 예외가 발생한다.")
    @Test
    void create4() {
        // when & then
        assertThatIllegalArgumentException().isThrownBy(() -> Section.of(upStation, downStation, null))
                                            .withMessageContaining("Section 생성에 필요한 필수 정보를 확인해주세요.");
    }

    @DisplayName("Line 에 중복해서 Section 을 추가할 수 없다.")
    @Test
    void registerLine() {
        // given
        Section section = Section.of(upStation, downStation, distance);
        line.addSection(section);

        // when & then
        assertThatIllegalStateException().isThrownBy(() -> line.addSection(section))
                                         .withMessageContaining("이미 포함된 Section 입니다.");
    }
}