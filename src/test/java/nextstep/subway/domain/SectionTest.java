package nextstep.subway.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class SectionTest {
    private final Line twoLine = new Line("이호선", "노란색");
    private final Station gangNam = new Station("강남역");
    private final Station seoCho = new Station("서초역");
    private final Station gyoDae = new Station("교대역");

    @Test
    @DisplayName("두 객체가 같은지 검증")
    void verifySameSectionObject() {
        Section section = new Section(twoLine, gangNam, seoCho, Distance.of(10L));

        assertThat(section).isEqualTo(new Section(twoLine, gangNam, seoCho, Distance.of(10L)));
    }

    @Test
    @DisplayName("구간을 업데이트시 잘 반영되었는지 확인")
    void updateSection() {
        Section section = new Section(twoLine, gangNam, seoCho, Distance.of(10L));
        section.update(new Section(twoLine, gangNam, gyoDae, Distance.of(5L)));

        assertAll(
                () -> assertThat(section.upStation()).isEqualTo(gyoDae),
                () -> assertThat(section.downStation()).isEqualTo(seoCho)
        );
    }

    @Test
    @DisplayName("해당 구간에 역이 올바른지 확인")
    void verifyUpStationAndDownStation() {
        Section section = new Section(twoLine, gangNam, seoCho, Distance.of(10L));

        assertAll(
                () -> assertThat(section.isSameUpStation(gangNam)).isTrue(),
                () -> assertThat(section.isSameDownStation(seoCho)).isTrue(),
                () -> assertThat(section.stations()).containsExactly(gangNam, seoCho)
        );
    }

    @Test
    @DisplayName("두 구간을 합칠 경우 하나로 합쳐지는지 확인")
    void mergeTwoSectionThenOneSection() {
        Section section1 = new Section(twoLine, gangNam, seoCho, Distance.of(10L));
        Section section2 = new Section(twoLine, gyoDae, gangNam, Distance.of(10L));
        Section merge = Section.mergeTwoSection(section1, section2);

        assertAll(
                () -> assertThat(merge.upStation()).isEqualTo(gyoDae),
                () -> assertThat(merge.downStation()).isEqualTo(seoCho),
                () -> assertThat(merge.distance()).isEqualTo(Distance.of(20L))
        );
    }
}
