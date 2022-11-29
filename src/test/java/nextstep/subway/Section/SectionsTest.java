package nextstep.subway.Section;

import nextstep.subway.domain.Section;
import nextstep.subway.domain.Sections;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
class SectionsTest {
    @Test
    void addSection() {
        // given
        Sections sections = new Sections();
        Section section = new Section(new Station("강남역"), new Station("보라매역"), 10);
        // when
        sections.add(section);
        // then
        assertThat(sections.asList().get(0).findUpStationName()).isEqualTo("강남역");
        assertThat(sections.asList().get(0).findDownStationName()).isEqualTo("보라매역");
    }

    @DisplayName("section 추가 시 상하행역이 기존 Section과 모두 동일하면 예외 발생")
    @Test
    void shouldExceptionWhenUpDownStationAlreadyBeing() {
        // given
        Sections sections = new Sections();
        Section section = new Section(new Station("강남역"), new Station("보라매역"), 10);
        sections.add(section);
        // when // then
        assertThatThrownBy(() -> sections.add(section))
                .isInstanceOf(IllegalArgumentException.class);
    }

}
