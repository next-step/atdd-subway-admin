package nextstep.subway.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class SectionTest {
    private Station 상행종점역;
    private Station 하행종점역;
    private Station 새로운역;
    private Section initSection;

    @BeforeEach
    void setUp() {
        상행종점역 = new Station("상행종점역");
        하행종점역 = new Station("하행종점역");
        새로운역 = new Station("새로운역");
        initSection = Section.of(상행종점역, 하행종점역, 10);
    }

    @Test
    @DisplayName("기존 Section 의 거리보다 길거나 같으면 오류 발생한다.")
    void checkDistance() {
        Section newSection = Section.of(상행종점역, 새로운역, 10);
        assertThrows(IllegalArgumentException.class,() -> initSection.changeStation(newSection));
    }

    @Test
    @DisplayName("기존 Section 에 추가하는 section 의 station 있는지 확인")
    void checkExistStation() {
        Section newSection = Section.of(상행종점역, 새로운역, 7);
        assertThat(initSection.isCheckStation(newSection)).isTrue();
    }

    @Test
    @DisplayName("기존 Section 에 추가하는 section 의 station 없는지 확인")
    void checkNoExistStation() {
        Station 더새로운역 = new Station("더새로운역");
        Section newSection = Section.of(더새로운역, 새로운역, 7);
        assertThat(initSection.isCheckStation(newSection)).isFalse();
    }

    @Test
    @DisplayName("기존 Section 내 upStation 확인")
    void checkEqualUpstation() {
        assertThat(initSection.equalUpStation(상행종점역)).isTrue();
    }

    @Test
    @DisplayName("기존 Section 내 downStation 확인")
    void checkEqualDownstation() {
        assertThat(initSection.equalDownStation(하행종점역)).isTrue();
    }

    @Test
    @DisplayName("기존 Section 새로운 Section refresh")
    void refreshSection() {
        Section newSection = Section.of(상행종점역, 새로운역, 7);
        initSection.refreshWith(newSection);
        assertThat(initSection.getStations()).containsExactly(상행종점역,새로운역);
    }
}
