package nextstep.subway.line.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.mockito.Mockito.mock;

import java.util.Arrays;
import java.util.List;
import nextstep.subway.common.domain.Name;
import nextstep.subway.station.domain.Station;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("구간들")
class SectionsTest {

    @Test
    @DisplayName("객체화")
    void instance() {
        assertThatNoException()
            .isThrownBy(() -> Sections.from(mock(Section.class)));
    }

    @Test
    @DisplayName("구간이 null인 상태로 객체화하면 IllegalArgumentException")
    void instance_nullSection_thrownIllegalArgumentException() {
        assertThatIllegalArgumentException()
            .isThrownBy(() -> Sections.from(null))
            .withMessage("section must not be null");
    }


    @Test
    @DisplayName("지하철 역들")
    void stations() {
        //given
        String gangnam = "강남";
        String yeoksam = "역삼";
        Sections sections = Sections.from(section(gangnam, yeoksam));

        //when
        List<Station> stations = sections.stations();

        //then
        doesNotHaveDuplicates(stations, 2, gangnam, yeoksam);
    }

    private void doesNotHaveDuplicates(
        List<Station> stations, int expectedSize, String... expectedNames) {
        assertThat(stations)
            .hasSize(expectedSize)
            .doesNotHaveDuplicates()
            .containsExactly(
                Arrays.stream(expectedNames)
                    .map(this::station)
                    .toArray(Station[]::new)
            );
    }


    private Section section(String upStation, String downStation) {
        return Section.of(
            station(upStation),
            station(downStation),
            Distance.from(Integer.MAX_VALUE)
        );
    }

    private Station station(String name) {
        return Station.from(Name.from(name));
    }


}
