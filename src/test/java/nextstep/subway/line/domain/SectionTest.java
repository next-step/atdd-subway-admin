package nextstep.subway.line.domain;

import nextstep.subway.station.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

class SectionTest {
    private Station stationA;
    private Station stationB;
    private Station stationC;
    private Station stationD;

    @BeforeEach
    void setUp() {
        stationA = new Station("A");
        ReflectionTestUtils.setField(stationA, "id", 1L);
        stationB = new Station("B");
        ReflectionTestUtils.setField(stationB, "id", 2L);
        stationC = new Station("C");
        ReflectionTestUtils.setField(stationC, "id", 3L);
        stationD = new Station("D");
        ReflectionTestUtils.setField(stationD, "id", 4L);
    }

    @Test
    void create() {
        //when
        Section actual = Section.of(stationA, stationB, 10);

        //then
        assertAll(() -> {
            assertThat(actual.getUpStation()).isSameAs(stationA);
            assertThat(actual.getDownStation()).isSameAs(stationB);
        });
    }

    @Test
    void toLine() {
        //given
        Section section = Section.of(stationA, stationB, 10);
        Line line = new Line("2호선", "green");

        //when
        section.toLine(line);

        //then
        assertAll(() -> {
            assertThat(section.getLine().getName()).isEqualTo("2호선");
            assertThat(section.getLine().getColor()).isEqualTo("green");
        });
    }

    @DisplayName("역 사이에 새로운 역을 등록할 경우 상행역 기준으로 새로운 길이를 뺀 나머지를 새롭게 추가한 역과의 길이로 설정한다.")
    @Test
    void addInsideOfSectionInUpDirection() {
        //given
        Section section = Section.of(stationA, stationB, 10);
        Section newSection = Section.of(stationA, stationC, 1);

        //when
        section.addInsideOfSection(newSection);

        //then
        assertAll(() -> {
            assertThat(newSection.getUpStation()).isEqualTo(stationA);
            assertThat(newSection.getDownStation()).isEqualTo(stationC);
            assertThat(section.getUpStation()).isEqualTo(stationC);
            assertThat(section.getDownStation()).isEqualTo(stationB);
        });
    }

    @DisplayName("역 사이에 새로운 역을 등록할 경우 하행역 기준으로 새로운 길이를 뺀 나머지를 새롭게 추가한 역과의 길이로 설정한다.")
    @Test
    void addInsideOfSectionInDownDirection() {
        //given
        Section section = Section.of(stationA, stationB, 10);
        Section newSection = Section.of(stationC, stationB, 1);

        //when
        section.addInsideOfSection(newSection);

        //then
        assertAll(() -> {
            assertThat(section.getUpStation()).isEqualTo(stationA);
            assertThat(section.getDownStation()).isEqualTo(stationC);
            assertThat(newSection.getUpStation()).isEqualTo(stationC);
            assertThat(newSection.getDownStation()).isEqualTo(stationB);
        });
    }

    @DisplayName("역 사이에 새로운 역을 등록할 경우 추가할 거리보다 기존 거리보다 클 경우 예외를 발생시킨다.")
    @Test
    void addInsideOfSectionDistanceException() {
        //given
        Section section = Section.of(stationA, stationB, 10);
        Section newSection = Section.of(stationC, stationB, 11);

        //when
        assertThatThrownBy(() -> section.addInsideOfSection(newSection))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(Section.BIGGER_THAN_DISTANCE_EXCEPTION_MESSAGE);
    }
}