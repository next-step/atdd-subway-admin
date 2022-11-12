package nextstep.subway.section.domain;

import nextstep.subway.station.domain.Station;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SectionsTest {

    private Sections sections;

    @BeforeEach
    void setUp() {
        sections = Sections.createEmpty();
        sections.add(Section.of(Station.from("신사역"), Station.from("광교역"), 10));
    }

    @DisplayName("지하철 구간 추가하기")
    @Test
    void add() {
        sections.add(Section.of(Station.from("신사역"), Station.from("강남역"), 5));

        Assertions.assertThat(sections.getStationsInOrder()).hasSize(3);
    }

    @DisplayName("새로운 상행종점 역을 포함하는 지하철 구간 추가하기")
    @Test
    void add2() {
        sections.add(Section.of(Station.from("신논현역"), Station.from("신사역"), 5));

        Assertions.assertThat(sections.getStationsInOrder()).hasSize(3);
    }

    @DisplayName("새로운 하행종점 역을 포함하는 지하철 구간 추가하기")
    @Test
    void add3() {
        sections.add(Section.of(Station.from("광교역"), Station.from("호매실역"), 5));

        Assertions.assertThat(sections.getStationsInOrder()).hasSize(3);
    }

    @DisplayName("길이가 동일한 지하철 구간 추가 시 예외가 발생한다.")
    @Test
    void add4() {
        Section section = Section.of(Station.from("신사역"), Station.from("강남역"), 10);

        Assertions.assertThatThrownBy(() -> sections.add(section))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("동일한 지하철 구간 추가 시 예외가 발생한다.")
    @Test
    void add5() {
        Section section = Section.of(Station.from("신사역"), Station.from("광교역"), 10);

        Assertions.assertThatThrownBy(() -> sections.add(section))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("지하철 노선 내 지하철 역 조회(상행역 -> 하행역 순)")
    @Test
    void getStationsInOrder() {
        sections.add(Section.of(Station.from("신사역"), Station.from("강남역"), 5));

        Assertions.assertThat(sections.getStationsInOrder())
                .containsExactly(
                        Station.from("신사역"),
                        Station.from("강남역"),
                        Station.from("광교역")
                );
    }
}
