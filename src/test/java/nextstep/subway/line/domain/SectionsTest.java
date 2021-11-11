package nextstep.subway.line.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.mockito.Mockito.mock;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;
import nextstep.subway.common.domain.Name;
import nextstep.subway.station.domain.Station;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

@DisplayName("구간들")
class SectionsTest {

    @Test
    @DisplayName("객체화")
    void instance() {
        assertThatNoException()
            .isThrownBy(() -> Sections.from(Collections.singletonList(mock(Section.class))));
    }

    @Test
    @DisplayName("목록이 비어있는 상태로 객체화하면 IllegalArgumentException")
    void instance_emptyList_thrownIllegalArgumentException() {
        assertThatIllegalArgumentException()
            .isThrownBy(() -> Sections.from(Collections.emptyList()))
            .withMessage("section list must not be empty");
    }


    @Test
    @DisplayName("지하철 역들")
    void stations() {
        //given
        String gangnam = "강남";
        String yeoksam = "역삼";
        String jamsil = "잠실";

        Sections sections = Sections.from(Arrays.asList(
            section(gangnam, yeoksam, Integer.MAX_VALUE),
            section(yeoksam, jamsil, Integer.MAX_VALUE)
        ));

        //when
        List<Station> stations = sections.stations();

        //then
        hasThreeAndNotHasDuplicates(stations, gangnam, yeoksam, jamsil);
    }


    @ParameterizedTest
    @DisplayName("구간 추가")
    @MethodSource
    void addSection(Section section, Station... expected) {
        //given
        String gyodae = "교대";
        String yeoksam = "역삼";
        String samseong = "삼성";

        Sections sections = Sections.from(new ArrayList<>(Arrays.asList(
            section(gyodae, yeoksam, Integer.MAX_VALUE),
            section(yeoksam, samseong, Integer.MAX_VALUE)
        )));

        //when
        sections.addSection(section);

        //then
        assertThat(sections.stations())
            .hasSize(4)
            .doesNotHaveDuplicates()
            .containsExactly(expected);
    }

    private static Stream<Arguments> addSection() {

        return Stream.of(
            Arguments.of()
        );
    }

    private void hasThreeAndNotHasDuplicates(
        List<Station> stations, String... expected) {
        assertThat(stations)
            .hasSize(3)
            .doesNotHaveDuplicates()
            .containsExactly(
                Arrays.stream(expected)
                    .map(this::station)
                    .toArray(Station[]::new)
            );
    }


    private Section section(String upStation, String downStation, int distance) {
        return Section.of(station(upStation),
            station(downStation),
            Distance.from(distance)
        );
    }

    private Station station(String name) {
        return Station.from(Name.from(name));
    }


}
