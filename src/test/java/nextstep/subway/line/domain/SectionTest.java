package nextstep.subway.line.domain;

import static nextstep.subway.station.StationAcceptanceTest.*;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import nextstep.subway.line.exception.DuplicateSectionStationException;
import nextstep.subway.station.domain.Station;

public class SectionTest {

    @Test
    void 동일한_상행_하행_입력시_예외() {
        //given
        Station stationGangnam = new Station(1L, 강남역);

        // then
        Assertions.assertThatThrownBy(() -> {
                      Section section = new Section(stationGangnam, stationGangnam, new Distance(10));
                  }).isInstanceOf(DuplicateSectionStationException.class)
                  .hasMessage("상행선과 하행선은 동일할 수 없습니다.");
    }
}
