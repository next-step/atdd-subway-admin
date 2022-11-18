package nextstep.subway.line.domain;

import nextstep.subway.station.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("요청한 역을 포함하고 있는 구간을 삭제하는 클래스 테스트")
class SectionDeleteManagerTest {

    private Line line;
    private Station 교대역;
    private Station 강남역;
    private Station 역삼역;

    @BeforeEach
    void setUp() {
        line = new Line("2호선", "bg-green-600");
        교대역 = new Station("교대역");
        강남역 = new Station("강남역");
        역삼역 = new Station("역삼역");
    }

    @Test
    void 역_사이의_구간을_삭제한다() {
        Section upSection = new Section(강남역, 역삼역, 10);
        Section downSection = new Section(교대역, 강남역, 10);
        line.addSection(upSection);
        line.addSection(downSection);

        new SectionDeleteManager(line, Optional.of(upSection), Optional.of(downSection)).delete();

        assertAll(
                () -> assertThat(line.getSections()).hasSize(1),
                () -> assertThat(line.getSections().get(0)).satisfies(section -> {
                    assertEquals("교대역", section.getUpStation().getName());
                    assertEquals("역삼역", section.getDownStation().getName());
                    assertEquals(20, section.getDistance());
                })
        );
    }

    @Test
    void 상행_종점역_구간을_삭제한다() {
        Section upSection = new Section(교대역, 강남역, 10);
        Section downSection = new Section(강남역, 역삼역, 10);
        line.addSection(upSection);
        line.addSection(downSection);

        new SectionDeleteManager(line, Optional.of(upSection), Optional.empty()).delete();

        assertThat(line.getSections()).hasSize(1);
    }

    @Test
    void 하행_종점역_구간을_삭제한다() {
        Section upSection = new Section(교대역, 강남역, 10);
        Section downSection = new Section(강남역, 역삼역, 10);
        line.addSection(upSection);
        line.addSection(downSection);

        new SectionDeleteManager(line, Optional.empty(), Optional.of(downSection)).delete();

        assertThat(line.getSections()).hasSize(1);
    }
}
