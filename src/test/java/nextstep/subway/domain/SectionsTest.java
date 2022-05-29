package nextstep.subway.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.Collections;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class SectionsTest {
    private final Line twoLine = new Line("이호선", "노란색");
    private final Station gangNam = new Station("강남역");
    private final Station seoCho = new Station("서초역");
    private final Station gyoDae = new Station("교대역");

    @Test
    @DisplayName("두 객체가 같은지 검증")
    void verifySameSectionsObject() {
        Sections sections = Sections.of(Collections.singletonList(new Section(twoLine, gangNam)));

        assertThat(sections).isEqualTo(Sections.of(Collections.singletonList(new Section(twoLine, gangNam))));
    }

    @Test
    @DisplayName("구역이 갯수만큼 추가되었는지 확인")
    void addSection() {
        Sections sections = new Sections();
        sections.addSection(new Section(twoLine, seoCho, gyoDae, 10L));

        assertThat(sections.size()).isEqualTo(1);
    }

    @Test
    @DisplayName("정렬된 순서대로 나오는지 검증")
    void verifyInOrderSections() {
        Section gyoDaeToGangNam = new Section(twoLine, gyoDae, gangNam, 5L);
        Section seoChoToGyoDae = new Section(twoLine, seoCho, gyoDae, 10L);
        Sections sections = Sections.of(Arrays.asList(gyoDaeToGangNam, seoChoToGyoDae));

        assertThat(sections.getInOrderSections()).containsExactly(seoChoToGyoDae, gyoDaeToGangNam);
    }
}
