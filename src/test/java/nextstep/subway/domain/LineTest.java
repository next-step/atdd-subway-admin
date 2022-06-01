package nextstep.subway.domain;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

class LineTest {
    @Test
    void 노선_이름_빈_값_예외() {
        assertThatThrownBy(() ->
                new Line("", "bg-red-600", new Station(1L, "상행종점"), new Station(2L, "하행종점"), Distance.from(10)))
                .isInstanceOf(
                        IllegalArgumentException.class
                ).hasMessage("지하철 노선의 이름은 필수입니다.");
    }

    @Test
    void 노선_색깔_빈_값_예외() {
        assertThatThrownBy(() ->
                new Line("분당선", "", new Station(1L, "상행종점"), new Station(2L, "하행종점"), Distance.from(10)))
                .isInstanceOf(
                        IllegalArgumentException.class
                ).hasMessage("지하철 노선의 색깔은 필수입니다.");
    }

    @Test
    void 상행종점_하행종점_같은경우_예외() {
        Station station = new Station(1L, "상행종점");
        assertThatThrownBy(() ->
                new Line("분당선", "bg-red-600", station, station, Distance.from(10)))
                .isInstanceOf(
                        IllegalArgumentException.class
                ).hasMessage("상행종점역과 하행종점역은 같을 수 없습니다.");
    }

}