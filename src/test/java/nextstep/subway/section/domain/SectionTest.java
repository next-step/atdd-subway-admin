package nextstep.subway.section.domain;

import nextstep.subway.station.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

class SectionTest {
    public static final int DEFAULT_DISTANCE = 10;
    private Station 상행역;
    private Station 하행역;
    private Station 신규역;
    private Section defaultSection;

    @BeforeEach
    void initialize(){
        상행역 = new Station("first");
        하행역 = new Station("last");
        신규역 = new Station("new");
        defaultSection = new Section(상행역, 하행역, DEFAULT_DISTANCE);
    }

    @Test
    @DisplayName("Section Update 테스트: 새로운 구간: [상행역 - 신규역], Default 구간: [신규역 - 하행역]")
    void updateSection(){
        Section newSection = Section.of(상행역, 신규역, 3);
        defaultSection.updateWith(newSection);
        assertAll(
                () -> assertThat(defaultSection.getUpStation()).isEqualTo(신규역),
                () -> assertThat(defaultSection.getDistance()).isEqualTo(Distance.from(7))
        );
    }

    @Test
    @DisplayName("Section Update 테스트: Default 구간: [상행역 - 신규역], 새로운 구간: [신규역 - 하행역]")
    void updateSection2(){
        Section newSection = Section.of(신규역, 하행역, 3);
        defaultSection.updateWith(newSection);
        assertAll(
                () -> assertThat(defaultSection.getDownStation()).isEqualTo(신규역),
                () -> assertThat(defaultSection.getDistance()).isEqualTo(Distance.from(7))
        );
    }
}