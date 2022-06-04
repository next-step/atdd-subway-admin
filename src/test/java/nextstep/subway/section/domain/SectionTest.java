package nextstep.subway.section.domain;

import nextstep.subway.station.domain.Station;
import nextstep.subway.testutils.FactoryMethods;
import org.junit.jupiter.api.BeforeEach;
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
                () -> assertThat(defaultSection.getUpStation()).isEqualTo(신규역),
                () -> assertThat(defaultSection.getDistance()).isEqualTo(Distance.from(7))
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
                () -> assertThat(defaultSection.getDownStation()).isEqualTo(신규역),
                () -> assertThat(defaultSection.getDistance()).isEqualTo(Distance.from(7))
        );
    }
}