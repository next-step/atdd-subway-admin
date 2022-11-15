package nextstep.subway.section.domain;

import nextstep.subway.common.exception.DataRemoveException;
import nextstep.subway.common.message.ExceptionMessage;
import nextstep.subway.station.domain.Station;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class SectionsTest {

    private Sections sections;

    @BeforeEach
    void setUp() {
        sections = Sections.from(Section.of(Station.from("신사역"), Station.from("광교역"), 10));
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

    @DisplayName("상행/하행 종점역 둘다 포함하지 않는 구간 추가 시 예외가 발생한다.")
    @Test
    void add6() {
        Section section = Section.of(Station.from("안양역"), Station.from("수원역"), 10);

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

    @DisplayName("지하철 구간에서 상행종점역을 제거하면 다음역이 상행종점역이 된다.")
    @Test
    void removeUpStation() {
        Station 신사역 = Station.from("신사역");
        Station 강남역 = Station.from("강남역");
        sections.add(Section.of(신사역, 강남역, 5));

        sections.remove(신사역);

        Assertions.assertThat(sections.getStationsInOrder())
                .containsExactly(
                        Station.from("강남역"),
                        Station.from("광교역")
                );
    }


    @DisplayName("지하철 구간에서 하행종점역을 제거하면 이전역이 하행종점역이 된다.")
    @Test
    void removeDownStation() {
        Station 신사역 = Station.from("신사역");
        Station 강남역 = Station.from("강남역");
        sections.add(Section.of(신사역, 강남역, 5));

        sections.remove(Station.from("광교역"));

        Assertions.assertThat(sections.getStationsInOrder())
                .containsExactly(
                        Station.from("신사역"),
                        Station.from("강남역")
                );
    }

    @DisplayName("지하철 구간에서 중간역을 제거하면 구간이 재배치되고, 거리는 기존 구간 거리의 합이 된다.")
    @Test
    void removeStationBetweenUpStationAndDownStation() {
        Station 신사역 = Station.from("신사역");
        Station 강남역 = Station.from("강남역");
        sections.add(Section.of(신사역, 강남역, 5));

        sections.remove(Station.from("강남역"));

        assertAll(
                () -> assertThat(sections.getTotalDistance()).isEqualTo(Distance.from(10)),
                () -> assertThat(sections.getStationsInOrder())
                        .containsExactly(
                                Station.from("신사역"),
                                Station.from("광교역")
                        )
        );
    }

    @DisplayName("지하철 구간이 하나인 경우 지하철역 제거 시 예외가 발생한다.")
    @Test
    void removeStationException() {
        Station 신사역 = Station.from("신사역");

        Assertions.assertThatThrownBy(() -> sections.remove(신사역))
                .isInstanceOf(DataRemoveException.class)
                .hasMessageStartingWith(ExceptionMessage.FAIL_TO_REMOVE_STATION_FROM_ONE_SECTION);
    }

    @DisplayName("지하철 구간에 존재하지 않는 지하철역 제거 시 예외가 발생한다.")
    @Test
    void removeStationException2() {
        Station 수원역 = Station.from("수원역");

        Assertions.assertThatThrownBy(() -> sections.remove(수원역))
                .isInstanceOf(DataRemoveException.class)
                .hasMessageStartingWith(ExceptionMessage.NOT_FOUND_STATION);
    }
}
