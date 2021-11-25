package nextstep.subway.line.domain;

import nextstep.subway.station.domain.Station;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class SectionTest {

    @Test
    void 구간_생성() {
        Station upStation = Station.from("강남역");
        Station downStation = Station.from("양재역");
        Section section = Section.of(upStation, downStation, 10);

        Assertions.assertThat(section).isNotNull();
    }
}
