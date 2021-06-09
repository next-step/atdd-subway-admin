package nextstep.subway.section.domain;

import nextstep.subway.station.domain.Station;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SectionTest {

    @Test
    void create() {
        //given
        Station upStation = new Station("강남역");
        Station downStation = new Station("삼성역");

        //when
        Section actual = new Section(upStation, downStation, 10);

        //then
        assertThat(actual.getUpStation()).isSameAs(upStation);
        assertThat(actual.getDownStation()).isSameAs(downStation);
    }
}