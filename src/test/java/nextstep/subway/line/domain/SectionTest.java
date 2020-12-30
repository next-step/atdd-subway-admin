package nextstep.subway.line.domain;

import nextstep.subway.station.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("구간에 대한 테스트")
class SectionTest {

    private Section section1;

    @BeforeEach
    void setUp() {
        Station upStation = new Station("청량리역");
        Station downStation = new Station("신창역");
        Distance distance = Distance.valueOf(100);

        section1 = Section.builder()
                .upStation(upStation)
                .downStation(downStation)
                .distance(distance)
                .build();
    }

    @DisplayName("구간을 생성한다.")
    @Test
    void create() {
        // given
        Station upStation = new Station("청량리역");
        Station downStation = new Station("신창역");
        Distance distance = Distance.valueOf(100);

        // when
        Section section = Section.builder()
                .upStation(upStation)
                .downStation(downStation)
                .distance(distance)
                .build();

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
        assertThrows(RuntimeException.class, () -> Section.builder()
                .upStation(upStation)
                .downStation(downStation)
                .distance(distance)
                .build());
    }

    @DisplayName("구간의 상행을 확인할 수 있다.")
    @Test
    void isUpStation() {
        // given
        Station upStation = new Station("청량리역");
        Station downStation = new Station("신창역");

        // when
        boolean result1 = section1.isUpStation(upStation);
        boolean result2 = section1.isUpStation(downStation);

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

        // when
        boolean result1 = section1.isDownStation(upStation);
        boolean result2 = section1.isDownStation(downStation);

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
        Section section1 = Section.builder()
                .upStation(upStation)
                .downStation(downStation)
                .distance(Distance.valueOf(100))
                .build();
        Section section2 = Section.builder()
                .upStation(upStation)
                .downStation(downStation)
                .distance(Distance.valueOf(200))
                .build();

        // then
        assertThat(section1).isEqualTo(section2);
    }

    @DisplayName("구간 사이에 상행역과 연결된 새로운 역을 반영하여 기존 구간을 업데이트한다.")
    @Test
    void updateConnectUp() {
        // when
        Station newStation = new Station("신도림역");
        Distance newDistance = Distance.valueOf(10);
        Section newSection = Section.builder()
                .upStation(section1.getUpStation())
                .downStation(newStation)
                .distance(newDistance)
                .build();

        boolean result = section1.canAddBetweenSection(newSection);
        section1.update(newSection);

        // then
        assertThat(result).isTrue();
        assertThat(section1.getUpStation()).isEqualTo(newStation);
        assertThat(section1.getDownStation()).isEqualTo(section1.getDownStation());
        assertThat(section1.getDistance()).isEqualTo(Distance.valueOf(90));
    }

    @DisplayName("구간 사이에 하행역과 연결된 새로운 역을 반영하여 기존 구간을 업데이트한다.")
    @Test
    void updateConnectDown() {
        // when
        Station newStation = new Station("신도림역");
        Distance newDistance = Distance.valueOf(20);
        Section newSection = Section.builder()
                .upStation(newStation)
                .downStation(section1.getDownStation())
                .distance(newDistance)
                .build();

        boolean result = section1.canAddBetweenSection(newSection);
        section1.update(newSection);

        // then
        assertThat(result).isTrue();
        assertThat(section1.getUpStation()).isEqualTo(section1.getUpStation());
        assertThat(section1.getDownStation()).isEqualTo(newStation);
        assertThat(section1.getDistance()).isEqualTo(Distance.valueOf(80));
    }

    @DisplayName("구간 사이에 상행역과 연결된 새로운 역을 등록할 수 있는지 확인한다.")
    @Test
    void canAddBetweenSectionUp() {
        // given
        Station station1 = new Station("회기역");
        Station station2 = new Station("신도림역");
        Distance distance = Distance.valueOf(20);

        Section betweenSection = Section.builder()
                .upStation(section1.getUpStation())
                .downStation(station2)
                .distance(distance)
                .build();

        Section endSection = Section.builder()
                .upStation(station1)
                .downStation(section1.getUpStation())
                .distance(distance)
                .build();

        // when
        boolean between = section1.canAddBetweenSection(betweenSection);
        boolean end = section1.canAddBetweenSection(endSection);

        // then
        assertThat(between).isTrue();
        assertThat(end).isFalse();
    }

    @DisplayName("구간 사이에 하행역과 연결된 새로운 역을 등록할 수 있는지 확인한다.")
    @Test
    void canAddBetweenSectionDown() {
        // given
        Station station1 = new Station("창신역");
        Station station2 = new Station("신도림역");
        Distance distance = Distance.valueOf(20);

        Section betweenSection = Section.builder()
                .upStation(station2)
                .downStation(section1.getDownStation())
                .distance(distance)
                .build();

        Section endSection = Section.builder()
                .upStation(section1.getDownStation())
                .downStation(station1)
                .distance(distance)
                .build();

        // when
        boolean between = section1.canAddBetweenSection(betweenSection);
        boolean end = section1.canAddBetweenSection(endSection);

        // then
        assertThat(between).isTrue();
        assertThat(end).isFalse();
    }
}
