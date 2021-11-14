package nextstep.subway.line.domain;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;

import nextstep.subway.section.domain.Distance;
import nextstep.subway.section.domain.Section;
import nextstep.subway.station.domain.Station;

class LineTest {

    @DisplayName("이름, 색깔 정보로 Line 을 생성한다.")
    @ParameterizedTest
    @CsvSource(value = {"신분당선,RED", "1호선,BLUE", "2호선,GREEN"})
    void create1(String name, String color) {
        // when
        Line line = Line.of(name, color);

        // then
        assertAll(
            () -> assertEquals(line.getName(), LineName.from(name)),
            () -> assertEquals(line.getColor(), LineColor.from(color))
        );
    }

    @DisplayName("Line 이름을 null or 빈 문자열로 할 수 없다.")
    @ParameterizedTest
    @NullAndEmptySource
    void create2(String name) {
        // when & then
        assertThatIllegalArgumentException().isThrownBy(() -> Line.of(name, "RED"))
                                            .withMessageContaining("노선이름이 비어있습니다");
    }

    @DisplayName("Line 색깔을 null or 빈 문자열로 할 수 없다.")
    @ParameterizedTest
    @NullAndEmptySource
    void create3(String color) {
        // when & then
        assertThatIllegalArgumentException().isThrownBy(() -> Line.of("신분당선", color))
                                            .withMessageContaining("노선색깔이 비어있습니다.");
    }

    @DisplayName("Line 에 중복해서 Section 을 추가할 수 없다.")
    @Test
    void registerSection() {
        // given
        Line 신분당선 = Line.of("신분당선", "RED");
        Station 판교역 = Station.from("판교역");
        Station 정자역 = Station.from("정자역");
        Distance distance = Distance.from(10);
        Section 판교_정자_구간 = Section.of(판교역, 정자역, distance);

        신분당선.addSection(판교_정자_구간);

        // when & then
        assertThatIllegalStateException().isThrownBy(() -> 신분당선.addSection(판교_정자_구간))
                                         .withMessageContaining("이미 포함된 Section 입니다.");
    }

    @DisplayName("Line 에 Section 이 1개만 존재하는 경우, Station 을 삭제 할 수 없다.")
    @Test
    void deleteStation1() {
        // given
        Line 신분당선 = Line.of("신분당선", "RED");
        Station 판교역 = Station.from("판교역");
        Station 정자역 = Station.from("정자역");
        Distance distance = Distance.from(10);
        Section 판교_정자_구간 = Section.of(판교역, 정자역, distance);

        신분당선.addSection(판교_정자_구간);

        // when & then
        assertThatIllegalArgumentException().isThrownBy(() -> 신분당선.removeStation(판교역))
                                         .withMessageContaining("노선의 구간이 1개인 경우, 지하철 역을 삭제 할 수 없습니다.");
    }

    @DisplayName("Line 에 존재하지 않는 Station 은 삭제 할 수 없다.")
    @Test
    void deleteStation2() {
        // given
        Line 신분당선 = Line.of("신분당선", "RED");
        Station 판교역 = Station.from(1L, "판교역");
        Station 정자역 = Station.from(2L, "정자역");
        Station 미금역 = Station.from(3L, "미금역");

        Section 판교_정자_구간 = Section.of(판교역, 정자역, Distance.from(10));
        Section 정자_미금_구간 = Section.of(정자역, 미금역, Distance.from(5));

        신분당선.addSection(판교_정자_구간);
        신분당선.addSection(정자_미금_구간);

        // when
        Station 강남역 = Station.from(10L, "강남역");

        // then
        assertThatIllegalArgumentException().isThrownBy(() -> 신분당선.removeStation(강남역))
                                            .withMessageContaining("노선에 존재하지 않는 역입니다.");
    }
}