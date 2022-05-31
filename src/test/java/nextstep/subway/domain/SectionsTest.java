package nextstep.subway.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class SectionsTest {
    private final static Station station1 = new Station(1L, "강남역");
    private final static Station station2 = new Station(2L, "삼성역");
    private final static Station station3 = new Station(3L, "잠실역");
    private final static Station station4 = new Station(4L, "시청역");
    private final static Station station5 = new Station(5L, "왕십리역");
    private final static Section section1 = new Section(1L, station1, station2, 5);
    private final static Section section2 = new Section(2L, station2, station3, 5);

    @Test
    @DisplayName("getSections의 결과 구간 정보를 가지는 리스트가 구간 순서대로 반환된다")
    void getSections() {
        // given
        final Sections sections = new Sections();
        sections.addSection(section1);
        sections.addSection(section2);

        // when
        final List<Section> sectionList = sections.getSections();

        // when
        assertThat(sectionList).containsExactly(section1, section2);
    }

    @Test
    @DisplayName("addSection의 결과 기 등록된 역 사이에 구간 등록시 구간보다 같거나 긴 구간을 추가하는 경우 오류를 반환한다")
    void addSectionInvalidDistanceError() {
        // given
        final Sections sections = new Sections();
        final Section section = new Section(3L, station1, station3, 5);
        sections.addSection(section);
        final Section section2 = new Section(3L, station2, station3, 5);

        // when & then
        assertThatIllegalArgumentException().isThrownBy(
                () -> sections.addSection(section2)
        );
    }

    @Test
    @DisplayName("addSection의 결과 동일한 구간을 추가하는 경우 오류를 반환한다")
    void addSectionSameSectionError() {
        // given
        final Sections sections = new Sections();
        sections.addSection(section2);
        final Section section3 = new Section(3L, station2, station3, 5);

        // when & then
        assertThatIllegalArgumentException().isThrownBy(
                () -> sections.addSection(section3)
        );
    }

    @Test
    @DisplayName("addSection의 결과 기존 구간에 속하지 않는 구간을 추가하는 경우 오류를 반환한다")
    void addSectionNonExistSectionError() {
        // given
        final Sections sections = new Sections();
        sections.addSection(section1);
        sections.addSection(section2);

        final Section section = new Section(3L, station4, station5, 5);

        // when& then
        assertThatIllegalArgumentException().isThrownBy(
                () -> sections.addSection(section)
        );
    }

}
