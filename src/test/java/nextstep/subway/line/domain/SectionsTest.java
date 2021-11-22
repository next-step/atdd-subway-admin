package nextstep.subway.line.domain;

import nextstep.subway.station.domain.Station;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class SectionsTest {


    @DisplayName("역목록은 상행역 부터 하행역 순으로 정렬 되어야 한다.")
    @Test
    void getStationsOrderByUptoDown() {

        final Sections sections = Sections.of(
                Arrays.asList(
                        Section.of(10, "3번", "4번"),
                        Section.of(10, "4번", "5번"),
                        Section.of(10, "1번", "2번"),
                        Section.of(10, "2번", "3번")
                )
        );

        final List<Station> stationsOrderByUptoDown = sections.getStationsOrderByUptoDown();

        assertThat(stationsOrderByUptoDown.get(0).getName()).isEqualTo("1번");
        assertThat(stationsOrderByUptoDown.get(1).getName()).isEqualTo("2번");
        assertThat(stationsOrderByUptoDown.get(2).getName()).isEqualTo("3번");
        assertThat(stationsOrderByUptoDown.get(3).getName()).isEqualTo("4번");
        assertThat(stationsOrderByUptoDown.get(4).getName()).isEqualTo("5번");
    }
}