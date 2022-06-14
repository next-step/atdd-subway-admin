package nextstep.subway.line.domain;

import nextstep.subway.line.exception.LineException;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class SectionsTest {
    private Sections sections;

    @BeforeEach
    void setUp() {
        sections = new Sections();
        섹션_추가(StationTest.강남역, StationTest.사당역, 10L);
    }

    @Test
    void 상하행_모두_존재하지_않을때_오류() {
        // given
        Section newSection = Section.of(StationTest.이수역, StationTest.서울역, 10L);
        // when
        // then
        assertThatThrownBy(() -> sections.add(newSection))
                .isInstanceOf(LineException.class);
    }

    @Test
    void 상하행_모두_존재_오류() {
        Section newSection = Section.of(StationTest.강남역, StationTest.사당역, 10L);
        // when
        // then
        assertThatThrownBy(() -> sections.add(newSection))
                .isInstanceOf(LineException.class);
    }

    @Test
    void 동일_거리_추가_오류() {
        // given
        Section newSection = Section.of(StationTest.강남역, StationTest.이수역, 10L);
        // when
        // then
        assertThatThrownBy(() -> sections.add(newSection))
                .isInstanceOf(LineException.class);
    }

    @Test
    void 하행_종착역_추가() {
        // given
        섹션_추가(StationTest.사당역, StationTest.이수역, 10L);
        // then
        assertThat(sections.contains(Section.of(StationTest.강남역, StationTest.사당역, 10L))).isTrue();
        assertThat(sections.contains(Section.of(StationTest.사당역, StationTest.이수역, 10L))).isTrue();
    }

    @Test
    void 상행_종착역_추가() {
        // given
        섹션_추가(StationTest.이수역, StationTest.강남역, 4L);
        // then
        assertThat(sections.contains(Section.of(StationTest.강남역, StationTest.사당역, 10L))).isTrue();
        assertThat(sections.contains(Section.of(StationTest.이수역, StationTest.강남역, 4L))).isTrue();
    }

    @Test
    void 중간역_추가() {
        // given
        섹션_추가(StationTest.강남역, StationTest.이수역, 4L);
        // then
        assertThat(sections.contains(Section.of(StationTest.강남역, StationTest.이수역, 4L))).isTrue();
        assertThat(sections.contains(Section.of(StationTest.이수역, StationTest.사당역, 6L))).isTrue();
    }

    @Test
    void 섹션_삭제_오류() {
        // then
        assertThatThrownBy(() -> sections.delete(StationTest.강남역))
                .isInstanceOf(LineException.class);
    }

    @Test
    void 섹션_종착역_삭제() {
        // given
        섹션_추가(StationTest.강남역, StationTest.이수역, 4L);
        // when
        sections.delete(StationTest.강남역);
        // then
        assertThat(sections.getAllStation().contains(StationTest.강남역)).isFalse();
    }

    @Test
    void 섹션_중간역_삭제() {
        // given
        섹션_추가(StationTest.강남역, StationTest.이수역, 4L);
        // when
        sections.delete(StationTest.이수역);
        // then
        assertThat(sections.getAllStation().contains(StationTest.이수역)).isFalse();
    }

    @Test
    void 상행선_종점_구하기() {
        // given
        섹션_추가(StationTest.사당역, StationTest.이수역, 10L);
        섹션_추가(StationTest.이수역, StationTest.서울역, 10L);
        // when
        // then
        assertThat(sections.finalUpStation()).isEqualTo(StationTest.강남역);
    }

    @Test
    void 하행선_종점_구하기() {
        // given
        섹션_추가(StationTest.사당역, StationTest.이수역, 10L);
        섹션_추가(StationTest.이수역, StationTest.서울역, 10L);
        // when
        // then
        assertThat(sections.finalDownStation()).isEqualTo(StationTest.서울역);
    }

    private void 섹션_추가(final Station upStation, final Station downStation, final long distance) {
        sections.add(Section.of(upStation, downStation, distance));
    }
}
