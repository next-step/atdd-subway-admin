package nextstep.subway.line;

import nextstep.subway.AcceptanceTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

@DisplayName("지하철 구간 관련 기능")
class LineStationAcceptanceTest extends AcceptanceTest {
    /**
     * Given 지하철 노선을 생성하고
     * When 생성된 노선 역들 사이에 새로운 역을 추가하면
     * Then 노선 조회시 2개의 구간을 찾을 수 있다.
     */
    @Test
    @DisplayName("역과 역 사이에 새로운 역을 등록한다.")
    void addStationBetweenLine() {
        // given

        // when

        // then
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 새로운 역을 상행 종점으로 등록하면
     * Then 노선 조회시 새로운 역이 상행 종점과 일치한다.
     */
    @Test
    @DisplayName("새로운 역을 상행 종점으로 등록한다.")
    void addStationByUpStation() {
        // given

        // when

        // then
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 새로운 역을 하행 종점으로 등록하면
     * Then 노선 조회시 새로운 역이 하행 종점과 일치한다.
     */
    @Test
    @DisplayName("새로운 역을 하행 좀점으로 등록한다.")
    void addStationByDownStation() {
        // given

        // when

        // then
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 새로운 역 등록시 기존 역 사이 길이 보다 크거나 같은 역을 등록하면
     * Then 등록되지 않는다.
     */
    @ParameterizedTest
    @ValueSource(ints = {10, 15})
    @DisplayName("새로운 역 등록시 기존 역 사이 길이보다 크거나 같으면 등록할 수 없다.")
    void addStationBySameAndGraterThenDistance(int distance) {
        // given

        // when

        // then
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 이미 등록된 상행역과 하행역을 등록하면
     * Then 등록되지 않는다.
     */
    @Test
    @DisplayName("이미 등록된 상행역과 하행역은 등록할 수 없다.")
    void addStationByAlreadyAddUpAndDownStation() {
        // given

        // when

        // then
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 상행역과 하행역이 포함되지 않은 구간을 등록하면
     * Then 등록되지 않는다.
     */
    @Test
    @DisplayName("상행역과 하행역이 하나라도 포함되어 있지 않으면 구간을 등록할 수 없다.")
    void addStationByDoesNotContainUpStationAndDownStation() {
        // given

        // when

        // then
    }
}
