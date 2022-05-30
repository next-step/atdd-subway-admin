package nextstep.subway.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SectionsTest {
    private Sections sections;

    @BeforeEach
    void setUp() {
        sections = new Sections();

        Station upStation = new Station("양재역");
        Station downStation = new Station("정자역");

        Section section = new Section(10, upStation, downStation);
        sections.add(section);
    }

    @Test
    void 상행역과_하행역이_이미_노선에_등록되어_있으면_구간을_추가할_수_없다() {
        // given
        Section newSection = new Section(7, new Station("양재역"), new Station("정자역"));
        sections.add(newSection);
        // when & then
        assertThatThrownBy(() ->
                sections.add(newSection)
        ).isInstanceOf(IllegalArgumentException.class);
    }
}