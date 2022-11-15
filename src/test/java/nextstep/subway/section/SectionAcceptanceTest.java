package nextstep.subway.section;

import nextstep.subway.common.BaseAcceptanceTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("구간 관련 기능")
public class SectionAcceptanceTest extends BaseAcceptanceTest {

    /**
     * Given 2개의 지하철역을 생성하고
     * Given 1개의 지하철 노선을 생성하고
     * Given 1개의 지하철역을 생성하고
     * When 지하철 구간을 등록하면
     * Then 지하철 노선을 조회 시 추가된 구간을 확인할 수 있다.
     */
    @Test
    @DisplayName("역 사이에 새로운 역을 등록할 경우")
    public void 새로운_역_등록() {
    }

    /**
     * Given 2개의 지하철역을 생성하고
     * Given 1개의 지하철 노선을 생성하고
     * Given 1개의 지하철역을 생성하고
     * When 새로운 역을 상행 종점으로 구간을 등록하면
     * Then 지하철 노선을 조회 시 추가된 구간을 확인할 수 있다.
     */
    @Test
    @DisplayName("새로운 역을 상행 종점으로 등록할 경우")
    public void 새로운_역_등록_상행종점() {
    }

    /**
     * Given 2개의 지하철역을 생성하고
     * Given 1개의 지하철 노선을 생성하고
     * Given 1개의 지하철역을 생성하고
     * When 새로운 역을 하행 종점으로 구간을 등록하면
     * Then 지하철 노선을 조회 시 추가된 구간을 확인할 수 있다.
     */
    @Test
    @DisplayName("새로운 역을 하행 종점으로 등록할 경우")
    public void 새로운_역_등록_하행종점() {
    }

    /**
     * Given 2개의 지하철역을 생성하고
     * Given 1개의 지하철 노선을 생성하고
     * Given 1개의 지하철역을 생성하고
     * When 새로운 역의 구간 길이를 기존 구간 길이보다 크거나 같게 등록하면
     * Then 지하철 구간 등록을 할 수 없다.
     */
    @Test
    @DisplayName("역 사이에 새로운 역을 등록할 경우 기존 역 사이 길이보다 크거나 같으면 등록할 수 없다")
    public void 새로운_역_등록_실패_길이() {
    }

    /**
     * Given 2개의 지하철역을 생성하고
     * Given 1개의 지하철 노선을 생성하고
     * When 이미 등록되어 있는 상행역, 하행역으로 등록한다면
     * Then 지하철 구간 등록을 할 수 없다.
     */
    @Test
    @DisplayName("상행역과 하행역이 이미 노선에 모두 등록되어 있다면 등록할 수 없다")
    public void 새로운_역_등록_실패_이미_존재() {
    }

    /**
     * Given 2개의 지하철역을 생성하고
     * Given 1개의 지하철 노선을 생성하고
     * When 이미 등록되어 있는 상행역, 하행역으로 등록한다면
     * Then 지하철 구간 등록을 할 수 없다.
     */
    @Test
    @DisplayName("노선에 새롭게 구간등록 하려는 상행역과 하행역 중 하나라도 포함되어 있지 않으면 등록할 수 없다")
    public void 새로운_역_등록_실패_포함_안됨() {
    }

}
