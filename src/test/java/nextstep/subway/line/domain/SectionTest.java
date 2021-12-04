package nextstep.subway.line.domain;

import nextstep.subway.common.exception.NegativeNumberDistanceException;
import nextstep.subway.common.exception.SameStationsInSectionException;
import nextstep.subway.line.domain.Distance;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.Section;
import nextstep.subway.station.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("구간 도메인 관련 기능")
class SectionTest {
    private Station 강남역;
    private Station 역삼역;
    private Line 호선2;
    private Section section;

    @BeforeEach
    void setUp() {
        강남역 = new Station("강남역");
        역삼역 = new Station("역삼역");
        호선2 = Line.of("2호선", "green", 강남역, 역삼역, 9);
        section = Section.of(호선2, 강남역, 역삼역, 9);
    }

    @DisplayName("구간을 생성한다.")
    @Test
    void createSection() {
        // then
        assertAll(
                () -> assertThat(section.getLine()).isEqualTo(호선2),
                () -> assertThat(section.getUpStation()).isEqualTo(강남역),
                () -> assertThat(section.getDownStation()).isEqualTo(역삼역),
                () -> assertThat(section.getDistance()).isEqualTo(9)
        );
    }

    @DisplayName("상행역을 변경한다.")
    @Test
    void changeUpStation() {
        // when
        final Station 신분당역 = new Station("신분당역");
        section.changeUpStation(신분당역, new Distance(2));

        // then
        assertThat(section.getUpStation()).isEqualTo(new Station("신분당역"));
    }

    @DisplayName("하행역을 변경한다.")
    @Test
    void changeDownStation() {
        // when
        final Station 잠실역 = new Station("잠실역");
        section.changeDownStation(잠실역, new Distance(3));

        // then
        assertThat(section.getDownStation()).isEqualTo(new Station("잠실역"));
    }

    @DisplayName("변경되는 거리가 기존의 거리보다 큰 경우 예외가 발생한다.")
    @Test
    void subtractDistanceException() {
        assertThatThrownBy(() -> {
           section.subtractDistance(new Distance(9));

        }).isInstanceOf(NegativeNumberDistanceException.class)
        .hasMessageContaining("현재 계산된 거리 값이 음수입니다.");
    }

    @DisplayName("상행, 하행 두 역이 같은 경우 예외가 발생한다.")
    @Test
    void sameStationRegisteredException() {
        assertThatThrownBy(() -> {
            final Section sameStationSection = Section.of(호선2, 강남역, 강남역, 9);

        }).isInstanceOf(SameStationsInSectionException.class)
                .hasMessageContaining("한 구간을 같은 역을 가리킬 수 없습니다.");
    }
}