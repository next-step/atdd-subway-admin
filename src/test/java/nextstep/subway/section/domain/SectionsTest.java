package nextstep.subway.section.domain;

import nextstep.subway.line.domain.Line;
import nextstep.subway.station.application.StationNotFoundException;
import nextstep.subway.station.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SectionsTest {
    private static final Long NOT_EXIST_STATION = 0L;
    private static final Long STATION1_ID = 1L;
    private static final Long STATION2_ID = 2L;
    private static final Long STATION3_ID = 3L;
    private static final Long STATION4_ID = 4L;
    private static final Long STATION5_ID = 5L;
    private Line line;
    private Station upStation;
    private Station downStation;
    private Station station1;
    private Station station2;
    private Station station3;
    private Station station4;
    private Station station5;
    private Section section;
    private Sections sections;

    @BeforeEach
    void setUp() {
        line = new Line("2호선", "green");
        upStation = new Station("강남역");
        downStation = new Station("역삼역");
        sections = new Sections();

        station1 = new Station("강남역");
        ReflectionTestUtils.setField(station1, "id", STATION1_ID);

        station2 = new Station("잠실역");
        ReflectionTestUtils.setField(station2, "id", STATION2_ID);

        station3 = new Station("선릉역");
        ReflectionTestUtils.setField(station3, "id", STATION3_ID);

        station4 = new Station("역삼역");
        ReflectionTestUtils.setField(station4, "id", STATION4_ID);

        station5 = new Station("종합운동장");
        ReflectionTestUtils.setField(station5, "id", STATION5_ID);
    }

    @Test
    void addSection() {
        Sections sections = new Sections();

        sections.addSection(new Section(station1, station2, 20));
        sections.addSection(new Section(station1, station4, 10));
        sections.addSection(new Section(station3, station2, 2));
        sections.addSection(new Section(station3, station5, 1));


        assertThat(sections.getStations()).containsExactly(station1, station4, station3, station5, station2);
    }

    @Test
    void addFirst() {
        Sections sections = new Sections();

        sections.addSection(new Section(station3, station2, 20));
        sections.addSection(new Section(station1, station3, 10));

        assertThat(sections.getStations()).containsExactly(station1, station3, station2);
    }

    @Test
    void addLast() {
        Sections sections = new Sections();

        sections.addSection(new Section(station1, station3, 20));
        sections.addSection(new Section(station3, station2, 10));

        assertThat(sections.getStations()).containsExactly(station1, station3, station2);
    }

    @Test
    void removeFirst() {
        // given
        Sections sections = new Sections();
        sections.addSection(new Section(station1, station2, 20));
        sections.addSection(new Section(station1, station4, 10));
        sections.addSection(new Section(station3, station2, 2));
        sections.addSection(new Section(station3, station5, 1));

        // when
        sections.deleteSection(STATION1_ID);

        // then
        assertThat(sections.getStations()).containsExactly(station4, station3, station5, station2);
    }

    @Test
    void removeLast() {
        // given
        Sections sections = new Sections();
        sections.addSection(new Section(station1, station2, 20));
        sections.addSection(new Section(station1, station4, 10));
        sections.addSection(new Section(station3, station2, 2));
        sections.addSection(new Section(station3, station5, 1));

        // when
        sections.deleteSection(STATION2_ID);

        // then
        assertThat(sections.getStations()).containsExactly(station1, station4, station3, station5);
    }

    @Test
    void removeMiddle() {
        // given
        Sections sections = new Sections();
        sections.addSection(new Section(station1, station2, 20));
        sections.addSection(new Section(station1, station4, 10));
        sections.addSection(new Section(station3, station2, 2));
        sections.addSection(new Section(station3, station5, 1));

        // when
        sections.deleteSection(STATION3_ID);

        // then
        assertThat(sections.getStations()).containsExactly(station1, station4, station5, station2);
    }

    @DisplayName("구간이 한 개 일때 삭제시 예외를 던진다.")
    @Test
    void removeWithOneSection() {
        // given
        Sections sections = new Sections();
        sections.addSection(new Section(station1, station2, 20));

        // when
        assertThatThrownBy(() -> sections.deleteSection(STATION1_ID))
                .isInstanceOf(IllegalStateException.class);
    }

    @DisplayName("존재하지 않는 지하철역 삭제시 예외를 던진다.")
    @Test
    void removeWithNotExistStation() {
        // given
        Sections sections = new Sections();
        sections.addSection(new Section(station1, station2, 20));
        sections.addSection(new Section(station1, station4, 10));

        // when
        assertThatThrownBy(() -> sections.deleteSection(NOT_EXIST_STATION))
                .isInstanceOf(StationNotFoundException.class);
    }
}
