package nextstep.subway.station.domain;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class StationTest {

    @Test
    void create() {
        //given
        String expected = "서울역";

        //when
        Station actual = new Station(expected);

        //then
        assertThat(actual.getName()).isEqualTo(expected);
    }
}