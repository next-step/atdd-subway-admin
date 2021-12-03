package nextstep.subway.section.domain;

import nextstep.subway.common.exception.NegativeNumberDistanceException;
import nextstep.subway.line.domain.Line;
import nextstep.subway.station.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("구간 도메인 관련 기능")
class SectionTest {
    private Station upStation;
    private Station downStation;
    private Line line;
    private Section section;

    @BeforeEach
    void setUp() {
        upStation = new Station("강남역");
        downStation = new Station("역삼역");
        line = Line.of("2호선", "green", upStation, downStation, 9);
        section = Section.of(line, upStation, downStation, 9);
    }

    @DisplayName("구간을 생성한다.")
    @Test
    void createSection() {
        // then
        assertAll(
                () -> assertThat(section.getLine()).isEqualTo(line),
                () -> assertThat(section.getUpStation()).isEqualTo(upStation),
                () -> assertThat(section.getDownStation()).isEqualTo(downStation),
                () -> assertThat(section.getDistance()).isEqualTo(9)
        );
    }

    @DisplayName("상행역을 변경한다.")
    @Test
    void changeUpStation() {
        // when
        section.changeUpStation(new Station("신분당역"), new Distance(2));

        // then
        assertThat(section.getUpStation()).isEqualTo(new Station("신분당역"));
    }

    @DisplayName("하행역을 변경한다.")
    @Test
    void changeDownStation() {
        // when
        section.changeDownStation(new Station("잠실역"), new Distance(3));

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
}