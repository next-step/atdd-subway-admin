package nextstep.subway.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Collections;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class SectionsTest {

    private Station upStation;
    private Station downStation;
    private Section section;
    private Sections sections;

    @BeforeEach
    public void setUp() {
        // given
        upStation = Station.of(1L, "잠실역");
        downStation = Station.of(2L, "장지역");
        section = Section.of(1L, upStation, downStation, 10);
        sections = Sections.from(Collections.singletonList(section));
    }

    @Test
    @DisplayName("구간 추가 성공")
    void add() {
        // given
        Station addStation = Station.of(3L, "문정역");
        Integer addDistance = 5;
        Section addSection = Section.of(2L, upStation, addStation, addDistance);

        // when
        sections.add(addSection);

        // then
        assertThat(sections.size()).isEqualTo(2);
    }

    @Test
    @DisplayName("중간역 구간 삭제 성공")
    void removeByUpSectionAndDownSection_중간() {
        // given
        Station addStation = Station.of(3L, "문정역");
        Integer addDistance = 5;
        Section addSection = Section.of(2L, upStation, addStation, addDistance);
        sections.add(addSection);

        // when
        sections.removeByUpSectionAndDownSection(section, addSection);

        // then
        assertThat(sections.size()).isEqualTo(1);
        assertThat(sections.getSections()).contains(section);
    }

    @Test
    @DisplayName("종점역 구간 삭제 성공")
    void removeByUpSectionAndDownSection_종점() {
        // given
        Station addStation = Station.of(3L, "문정역");
        Integer addDistance = 5;
        Section addSection = Section.of(upStation, addStation, addDistance);
        sections.add(addSection);

        // when
        sections.removeByUpSectionAndDownSection(null, addSection);

        // then
        assertThat(sections.size()).isEqualTo(1);
        assertThat(sections.getSections()).contains(section);
    }
}
