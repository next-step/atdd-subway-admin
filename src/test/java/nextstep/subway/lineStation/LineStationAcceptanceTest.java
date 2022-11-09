package nextstep.subway.lineStation;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@DisplayName("지하철 구간 관련 기능")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class LineStationAcceptanceTest {
    /* 역 사이에 새로운 역을 등록
- [ ] 새로운 역을 상행 종점으로 등록
- [ ] 새로운 역을 하행 종점으로 등록*/

    /**
     * Given 역 2개와 노선을 생성하고
     * When 사이에 새로운 역을 등록하면
     * Then 노선 조회 시 3개 역이 조회된다.
     */
    @DisplayName("역 사이에 새로운 역을 등록한다.")
    @Test
    void 역_사이에_새로운_역_등록() {

    }

    /**
     * Given 역 2개와 노선을 생성하고
     * When 새로운 역을 상행 종점으로 등록하면
     * Then 노선 조회 시 첫번째 역이 새로운 역과 일치한다.
     */
    @DisplayName("새로운 역을 상행 종점으로 등록한다.")
    @Test
    void 새로운_역을_상행_종점으로_등록() {

    }

    /**
     * Given 역 2개와 노선을 생성하고
     * When 새로운 역을 하행 종점으로 등록하면
     * Then 노선 조회 시 마지막 역이 새로운 역과 일치한다.
     */
    @DisplayName("새로운 역을 하행 종점으로 등록한다.")
    @Test
    void 새로운_역을_하행_종점으로_등록() {

    }

    /**
     * Given 역 2개와 노선을 생성하고
     * When 사이에 기존 역 사이 길이보다 크거나 같은 역을 등록하면
     * Then 등록이 안된다.
     */
    @DisplayName("기존 역 사이 길이보다 크거나 같은 역을 등록한다.")
    @Test
    void 기존_역_사이와_같거나_긴_역_등록() {

    }

    /**
     * Given 역 2개와 노선을 생성하고
     * When 생성한 노선을 다시 노선에 등록하면
     * Then 등록이 안된다.
     */
    @DisplayName("이미 등록된 상행역/하행역을 등록한다.")
    @Test
    void 이미_등록된_상행역_하행역을_등록() {

    }

    /**
     * When 상행역 또는 하행역이 없는 노선에 역을 등록하면
     * Then 등록이 안된다.
     */
    @DisplayName("상행역/하행역이 없는 노선에 역을 등록한다.")
    @Test
    void 상행역_하행역이_없는_노선에_역_등록() {

    }
}
