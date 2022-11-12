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
