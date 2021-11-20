package nextstep.subway.line.domain;

import nextstep.subway.exception.IllegalStationDistanceException;
import nextstep.subway.station.domain.Station;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

public class SectionTest {

    private static final Line 이호선 = new Line("2호선", "green");
    private static final Station 강남역 = new Station("강남역");
    private static final Station 역삼역 = new Station("역삼역");
    private static final Station 삼성역 = new Station("삼성역");

    @Test
    void updateUpStation_새로운_구간으로_업데이트_한다() {
        Section 구간 = new Section(이호선, 강남역, 삼성역, 10);
        구간.updateUpStation(역삼역, 3);
        assertThat(구간).isEqualTo(new Section(이호선, 역삼역, 삼성역, 7));
    }

    @Test
    void updateUpStation_기존_길이보다_새로운_구간의_길이가_길면_에러를_발생한다() {
        Section 구간 = new Section(이호선, 강남역, 삼성역, 10);
        assertThatExceptionOfType(IllegalStationDistanceException.class)
                .isThrownBy(() -> 구간.updateUpStation(역삼역, 20));
    }

    @Test
    void updateDownStation_하행역을_업데이트한다() {
        Section 구간 = new Section(이호선, 강남역, 삼성역, 10);
        구간.updateDownStation(역삼역, 3);
        assertThat(구간).isEqualTo(new Section(이호선, 강남역, 역삼역, 7));
    }

    @Test
    void updateDownStation_기존_길이보다_새로운_구간의_길이가_길면_에러를_발생한다() {
        Section 구간 = new Section(이호선, 강남역, 삼성역, 10);
        assertThatExceptionOfType(IllegalStationDistanceException.class)
                .isThrownBy(() -> 구간.updateDownStation(역삼역, 20));
    }
}
