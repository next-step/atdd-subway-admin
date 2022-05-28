package nextstep.subway.section.domain;

import static nextstep.subway.section.domain.exception.SectionExceptionMessage.NEW_SECTION_DISTANCE_IS_GREATER_OR_EQUALS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import nextstep.subway.line.domain.LineStations;
import nextstep.subway.station.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class SectionsTest {

    private static final int DISTANCE = 5;

    private Station 강남역;
    private Station 판교역;
    private Station 정자역;
    private Station 광교역;
    private Distance distance;
    private Section 강남역_판교역_구간;
    private Section 정자역_광교역_구간;

    @BeforeEach
    void setUp() {
        강남역 = Station.of(2L, "강남역");
        판교역 = Station.of(1L, "판교역");
        정자역 = Station.of(3L, "정자역");
        광교역 = Station.of(4L, "광교역");
        distance = Distance.from(DISTANCE);
        강남역_판교역_구간 = Section.of(1L, 강남역, 판교역, distance);
        정자역_광교역_구간 = Section.of(2L, 정자역, 광교역, distance);
    }

    @DisplayName("지하철역 구간 Section을 목록으로 Sections를 생성한다.")
    @Test
    void generate01() {
        assertThatNoException().isThrownBy(() ->
            Sections.from(Arrays.asList(강남역_판교역_구간, 정자역_광교역_구간))
        );
    }

    @DisplayName("기존 지하철역 구간 중간에 새로운 구간을 추가한다.")
    @Test
    void addSection01() {
        // given
        List<Section> givenSections = new ArrayList<>();
        givenSections.add(강남역_판교역_구간);

        Sections sections = Sections.from(givenSections);

        Station 양재역 = Station.of(11L, "양재역");
        Section 강남역_양재역_구간 = Section.of(11L, 강남역, 양재역, Distance.from(1));

        // when
        sections.add(강남역_양재역_구간);
        LineStations lineStations = sections.findSortedStations();

        // then
        assertThat(lineStations).isEqualTo(LineStations.from(Arrays.asList(강남역, 양재역, 판교역)));
    }

    @DisplayName("기존 지하철역 구간에 새로운 구간에 추가됨에 따라 역 간의 거리가 조정된다.")
    @Test
    void addSection02() {
        // given
        List<Section> givenSections = new ArrayList<>();
        givenSections.add(강남역_판교역_구간);

        Sections sections = Sections.from(givenSections);

        Station 양재역 = Station.of(11L, "양재역");
        Distance 강남역_양재역_구간_거리 = Distance.from(1);
        Section 강남역_양재역_구간 = Section.of(11L, 강남역, 양재역, 강남역_양재역_구간_거리);

        // when
        sections.add(강남역_양재역_구간);
        Section 중간역_추가로_조정된_강남역_판교역_구간 = sections.findSectionBySectionId(강남역_판교역_구간.getId()).get();
        Section 추가된_강남역_양재역_구간 = sections.findSectionBySectionId(강남역_양재역_구간.getId()).get();

        // then
        assertAll(
            () -> assertThat(중간역_추가로_조정된_강남역_판교역_구간.getDistance()).isEqualTo(Distance.from(4)),
            () -> assertThat(추가된_강남역_양재역_구간.getDistance()).isEqualTo(Distance.from(1))
        );
    }

    @DisplayName("중간에 새로운 구간을 추가한 후 지하철 역을 상행부터 하행의 순서로 출력할 수 있다.")
    @Test
    void showStations01() {
        // given
        List<Section> givenSections = new ArrayList<>();
        givenSections.add(강남역_판교역_구간);

        Sections sections = Sections.from(givenSections);

        Station 양재역 = Station.of(11L, "양재역");
        Distance 강남역_양재역_구간_거리 = Distance.from(1);
        Section 강남역_양재역_구간 = Section.of(11L, 강남역, 양재역, 강남역_양재역_구간_거리);

        // when
        sections.add(강남역_양재역_구간);
        LineStations lineStations = sections.findSortedStations();

        // then
        assertThat(lineStations).isEqualTo(LineStations.from(Arrays.asList(강남역, 양재역, 판교역)));
    }

    @DisplayName("기존 지하철역 구간 중간에 새로운 구간을 추가한다. (기존 구간의 하행역을 기준으로 추가해본다.)")
    @Test
    void addSection03() {
        // given
        List<Section> givenSections = new ArrayList<>();
        givenSections.add(강남역_판교역_구간);

        Sections sections = Sections.from(givenSections);

        Station 양재시민의숲역 = Station.of(11L, "양재시민의숲역");
        Section 양재시민의숲역_판교역_구간 = Section.of(11L, 양재시민의숲역, 판교역, Distance.from(1));

        // when
        sections.add(양재시민의숲역_판교역_구간);

        // then
        assertThat(sections.findSortedStations()).isEqualTo(LineStations.from(Arrays.asList(강남역, 양재시민의숲역, 판교역)));
    }

    @DisplayName("기존 지하철역 구간에 새로운 구간 추가 시 새로운 구간의 거리는 기존 지하철역 구간의 거리보다 크거나 같을 수 없다.")
    @ParameterizedTest
    @ValueSource(ints = { 5, 6, 7 })
    void addUpStation(int distance) {
        // given
        List<Section> givenSections = new ArrayList<>();
        givenSections.add(강남역_판교역_구간);

        Sections sections = Sections.from(givenSections);

        Station 양재시민의숲역 = Station.of(11L, "양재시민의숲역");
        Section 양재시민의숲역_판교역_구간 = Section.of(11L, 양재시민의숲역, 판교역, Distance.from(distance));

        // when & then
        assertThatIllegalArgumentException().isThrownBy(() -> sections.add(양재시민의숲역_판교역_구간))
            .withMessageContaining(NEW_SECTION_DISTANCE_IS_GREATER_OR_EQUALS.getMessage());
    }

    @DisplayName("기존 지하철역 구간 중간에 새로운 상행역 구간을 추가한다.")
    @Test
    void addUpStation01() {
        // given
        List<Section> givenSections = new ArrayList<>();
        givenSections.add(강남역_판교역_구간);

        Sections sections = Sections.from(givenSections);

        Station 신사역 = Station.of(11L, "신사역");
        Section 신사역_강남역_구간 = Section.of(11L, 신사역, 강남역, Distance.from(1));

        // when
        sections.add(신사역_강남역_구간);

        // then
        assertThat(sections.findSortedStations()).isEqualTo(LineStations.from(Arrays.asList(신사역, 강남역, 판교역)));
    }

    @DisplayName("기존 지하철역 구간 중간에 새로운 하행역 구간을 추가한다.")
    @Test
    void addDownStation01() {
        // given
        List<Section> givenSections = new ArrayList<>();
        givenSections.add(강남역_판교역_구간);

        Sections sections = Sections.from(givenSections);

        Section 판교역_광교역_구간 = Section.of(11L, 판교역, 광교역, Distance.from(1));

        // when
        sections.add(판교역_광교역_구간);

        // then
        assertThat(sections.findSortedStations()).isEqualTo(LineStations.from(Arrays.asList(강남역, 판교역, 광교역)));
    }
}