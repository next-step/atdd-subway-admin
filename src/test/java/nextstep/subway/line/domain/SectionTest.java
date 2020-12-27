package nextstep.subway.line.domain;

import nextstep.subway.station.domain.Station;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("구간에 대한 테스트")
class SectionTest {

    @DisplayName("구간을 생성한다.")
    @Test
    void create() {
        // given
        Station upStation = new Station("청량리역");
        Station downStation = new Station("신창역");
        Distance distance = Distance.valueOf(100);

        // when
        Section section = Section.of(upStation, downStation, distance);

        // then
        assertThat(section).isNotNull();
    }

    @DisplayName("구간의 상행과 하행은 같은 역일 수 없다.")
    @Test
    void createFail() {
        // given
        Station upStation = new Station("청량리역");
        Station downStation = new Station("청량리역");
        Distance distance = Distance.valueOf(100);

        // when / then
        assertThrows(RuntimeException.class, () -> Section.of(upStation, downStation, distance));
    }

    @DisplayName("구간의 상행을 확인할 수 있다.")
    @Test
    void isUpStation() {
        // given
        Station upStation = new Station("청량리역");
        Station downStation = new Station("신창역");
        Distance distance = Distance.valueOf(100);
        Section section = Section.of(upStation, downStation, distance);

        // when
        boolean result1 = section.isUpStation(upStation);
        boolean result2 = section.isUpStation(downStation);

        // then
        assertAll(
                () -> assertThat(result1).isTrue(),
                () -> assertThat(result2).isFalse()
        );
    }

    @DisplayName("구간의 하행을 확인할 수 있다.")
    @Test
    void isDownStation() {
        // given
        Station upStation = new Station("청량리역");
        Station downStation = new Station("신창역");
        Distance distance = Distance.valueOf(100);
        Section section = Section.of(upStation, downStation, distance);

        // when
        boolean result1 = section.isDownStation(upStation);
        boolean result2 = section.isDownStation(downStation);

        // then
        assertAll(
                () -> assertThat(result1).isFalse(),
                () -> assertThat(result2).isTrue()
        );
    }

    @DisplayName("구간 거리와 상관 없이 상행, 하행이 같으면 동등성을 보장한다.")
    @Test
    void equals() {
        // given
        Station upStation = new Station("청량리역");
        Station downStation = new Station("신창역");

        // when
        Section section1 = Section.of(upStation, downStation, Distance.valueOf(100));
        Section section2 = Section.of(upStation, downStation, Distance.valueOf(100));
        // then
        assertThat(section1).isEqualTo(section2);
    }
}
