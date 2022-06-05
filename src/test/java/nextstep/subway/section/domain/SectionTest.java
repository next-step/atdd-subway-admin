package nextstep.subway.section.domain;

import nextstep.subway.station.domain.Station;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static nextstep.subway.testutils.FactoryMethods.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

class SectionTest {
    public static final int DEFAULT_DISTANCE = 10;

    @Test
    @DisplayName("새로운 Section 추가 테스트: 새로운 구간: [상행역 - 신규역], Default 구간: [신규역 - 하행역]")
    void updateSection(){
        //given
        Station 상행역 = createStation("상행역");
        Station 하행역 = createStation("하행역");
        Station 신규역 = createStation("신규역");
        Section defaultSection = createSection(상행역, 하행역, DEFAULT_DISTANCE);

        //when
        Section newSection = Section.of(상행역, 신규역, 3);
        defaultSection.divideWith(newSection);

        //then
        assertAll(
                () -> assertThat(defaultSection.upStation()).isEqualTo(신규역),
                () -> assertThat(defaultSection.distance()).isEqualTo(Distance.from(7))
        );
    }

    @Test
    @DisplayName("새로운 Section 추가 테스트: Default 구간: [상행역 - 신규역], 새로운 구간: [신규역 - 하행역]")
    void updateSection2(){
        //given
        Station 상행역 = createStation("상행역");
        Station 하행역 = createStation("하행역");
        Station 신규역 = createStation("신규역");
        Section defaultSection = createSection(상행역, 하행역, DEFAULT_DISTANCE);

        //when
        Section newSection = Section.of(신규역, 하행역, 3);
        defaultSection.divideWith(newSection);

        //then
        assertAll(
                () -> assertThat(defaultSection.downStation()).isEqualTo(신규역),
                () -> assertThat(defaultSection.distance()).isEqualTo(Distance.from(7))
        );
    }

    @Test
    @DisplayName("Section Merge 테스트: Default 구간: [상행역 - 중간역], Merge 구간: [중간역 - 하행역]")
    void mergeSection(){
        //given
        Station 상행역 = createStation("상행역");
        Station 중간역 = createStation("중간역");
        Station 하행역 = createStation("하행역");
        Section defaultSection = createSection(상행역, 중간역, DEFAULT_DISTANCE);
        Section sectionToMerge = createSection(중간역, 하행역, DEFAULT_DISTANCE);

        //when
        defaultSection.mergeWith(sectionToMerge);

        //then
        assertAll(
                () -> assertThat(defaultSection.downStation()).isEqualTo(하행역),
                () -> assertThat(defaultSection.distance()).isEqualTo(Distance.from(DEFAULT_DISTANCE * 2))
        );
    }
}