package nextstep.subway.section.domain;

import nextstep.subway.line.domain.Line;
import nextstep.subway.station.application.StationNotFoundException;
import nextstep.subway.station.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SectionTest {
    private Station station1;
    private Station station2;
    private Station station3;
    private Line line;

    @BeforeEach
    void setUp() {
        station1 = new Station("강남역");
        station2 = new Station("역삼역");
        station3 = new Station("교대역");
        line = new Line("2호선", "green");
    }

    @DisplayName("역 정보 누락시 구간 생성 예외를 던진다.")
    @Test
    void createWithInvalidStation() {
        assertThatThrownBy(() -> new Section(line, station1, null, 10))
                .isInstanceOf(StationNotFoundException.class);
    }
}
