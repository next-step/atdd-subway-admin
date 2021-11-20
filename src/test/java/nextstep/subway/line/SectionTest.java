package nextstep.subway.line;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.Section;
import nextstep.subway.station.domain.Station;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static nextstep.subway.fixture.LineFixture.팔호선;
import static org.assertj.core.api.Assertions.assertThat;

class SectionTest {
    private static final Station 암사역 = Station.from("암사역");
    private static final Station 모란역 = Station.from("모란역");

    @DisplayName("정적팩토리 메서드 of에 Section객체를 생성하기 위한 필드를 인자로 받으면 Section 객체를 생성한다.")
    @Test
    void createTest() {
        assertThat(Section.of(Line.of(팔호선.get("name"), 팔호선.get("color")), Station.from("암사역"), Station.from("천호역"), 1)).isInstanceOf(Section.class);
    }
}
