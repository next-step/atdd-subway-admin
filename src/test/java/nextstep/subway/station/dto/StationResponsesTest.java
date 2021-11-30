package nextstep.subway.station.dto;

import static org.assertj.core.api.Assertions.*;

import java.util.Arrays;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import nextstep.subway.station.domain.Station;

class StationResponsesTest {
    @DisplayName("지하철 역 응답 리스트로 변환한다")
    @Test
    void 지하철_역_응답_리스트로_변환한다() {
        StationResponses stationResponses = StationResponses.from(
            Arrays.asList(new Station("강남역"), new Station("양재역")));

        assertThat(stationResponses).isEqualTo(
            StationResponses.from(Arrays.asList(new Station("강남역"), new Station("양재역"))));
    }
}