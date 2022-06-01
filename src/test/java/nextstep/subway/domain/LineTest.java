package nextstep.subway.domain;

import nextstep.subway.exception.InvalidDistanceException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class LineTest {
    private Station A역;
    private Station B역;
    private Station C역;
    private Station D역;
    private Station E역;
    private Station F역;

    @BeforeEach
    void setUp() {
        A역 = new Station(1L, "A역");
        B역 = new Station(2L, "B역");
        C역 = new Station(3L, "C역");
        D역 = new Station(4L, "D역");
        E역 = new Station(5L, "E역");
        F역 = new Station(6L, "F역");
    }

    @Test
    void 노선_초기화_테스트() {
        // given
        Line line = new Line("2호선", "초록", 10);

        // when
        line.initStation(A역, B역);

        // then
        assertThat(line.getUpStation()).isEqualTo(A역);
        assertThat(line.getDownStation()).isEqualTo(B역);
        assertThat(line.getDistance()).isEqualTo(10);
        assertThat(line.getSections().getList()).hasSize(1);
        assertThat(line.getLineStations().getList()).hasSize(2);
    }

    @Test
    void 구간_추가_상행역으로() {
        // given
        Line line = new Line("2호선", "초록", 10);
        line.initStation(A역, C역);

        // when
        line.insertSection(A역, B역, 3);

        // then
        assertThat(line.getUpStation()).isEqualTo(A역);
        assertThat(line.getDownStation()).isEqualTo(C역);
        assertThat(line.getDistance()).isEqualTo(10);
        assertThat(line.getSections().getList()).hasSize(2);
        assertThat(line.getLineStations().getList()).hasSize(3);
    }

    @Test
    void 구간_추가_하행역으로() {
        // given
        Line line = new Line("2호선", "초록", 10);
        line.initStation(A역, C역);

        // when
        line.insertSection(B역, C역, 3);

        // then
        assertThat(line.getUpStation()).isEqualTo(A역);
        assertThat(line.getDownStation()).isEqualTo(C역);
        assertThat(line.getDistance()).isEqualTo(10);
        assertThat(line.getSections().getList()).hasSize(2);
        assertThat(line.getLineStations().getList()).hasSize(3);
    }

    @Test
    void 구간_추가_상행종점역_변경() {
        // given
        Line line = new Line("2호선", "초록", 10);
        line.initStation(A역, C역);

        // when
        line.insertSection(B역, A역, 3);

        // then
        assertThat(line.getUpStation()).isEqualTo(B역);
        assertThat(line.getDownStation()).isEqualTo(C역);
        assertThat(line.getDistance()).isEqualTo(13);
        assertThat(line.getSections().getList()).hasSize(2);
        assertThat(line.getLineStations().getList()).hasSize(3);
    }

    @Test
    void 구간_추가_하행종점역_변경() {
        // given
        Line line = new Line("2호선", "초록", 10);
        line.initStation(A역, C역);

        // when
        line.insertSection(C역, B역, 3);

        // then
        assertThat(line.getUpStation()).isEqualTo(A역);
        assertThat(line.getDownStation()).isEqualTo(B역);
        assertThat(line.getDistance()).isEqualTo(13);
        assertThat(line.getSections().getList()).hasSize(2);
        assertThat(line.getLineStations().getList()).hasSize(3);
    }

    @Test
    void 다양하게_구간_추가() {
        // given
        Line line = new Line("2호선", "초록", 15);
        line.initStation(A역, D역);

        // when
        line.insertSection(A역, B역, 4);
        line.insertSection(C역, D역, 3);
        line.insertSection(E역, A역, 15);
        line.insertSection(D역, F역, 30);

        // then
        assertThat(line.getUpStation()).isEqualTo(E역);
        assertThat(line.getDownStation()).isEqualTo(F역);
        assertThat(line.getDistance()).isEqualTo(60);
        assertThat(line.getSections().getList()).hasSize(5);
        assertThat(line.getLineStations().getList()).hasSize(6);
    }

    @Test
    void 구간_추가_상행역으로_거리오류() {
        // given
        Line line = new Line("2호선", "초록", 10);
        line.initStation(A역, C역);

        // when, then
        assertThatThrownBy(() -> {
            line.insertSection(A역, B역, 10);
        }).isInstanceOf(InvalidDistanceException.class);
    }

    @Test
    void 구간_추가_하행역으로_거리오류() {
        // given
        Line line = new Line("2호선", "초록", 10);
        line.initStation(A역, C역);

        // when
        assertThatThrownBy(() -> {
            line.insertSection(B역, C역, 13);
        }).isInstanceOf(InvalidDistanceException.class);
    }
}
