package nextstep.subway.section;

import nextstep.subway.AcceptanceTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("구간 관련 기능")
public class SectionAcceptanceTest extends AcceptanceTest {
    @DisplayName("새로운 역을 상행 종점으로 등록한다.")
    @Test
    void addSectionAsFirstStation() {
        //  - 조건 : 기존의 first downStation == 요청 downStation
        //  - 새로운 상행 종점역이 추가되고 기존 상행 종점역에 길이가 추가된다.
        // when
        // 지하철_노선에_지하철역_등록_요청

        // then
        // 지하철_노선에_지하철역_등록됨
    }

    @DisplayName("새로운 역을 하행 종점으로 등록한다.")
    @Test
    void addSectionAsLastStation() {
        //  - 조건 : 기존의 last downStation == 요청 upStation
        //  - 새로운 구간이 추가되고 기존 유지, 새로운 역을 등록한다.
        // when
        // 지하철_노선에_지하철역_등록_요청

        // then
        // 지하철_노선에_지하철역_등록됨
    }

    @DisplayName("역 사이에 새로운 역을 등록한다.")
    @Test
    void addSectionAsNewStation() {
        //  - 조건 : 위의 두 조건이 아닐 경우 (그러나 downStation 중에 하나가 걸쳐있다.) && 예외에 속하지 않는 경우
        //  - 새로운 길이를 뺀 나머지를 새롭게 추가된 역과의 길이로 설정한다.
        // when
        // 지하철_노선에_지하철역_등록_요청

        // then
        // 지하철_노선에_지하철역_등록됨
    }

    @DisplayName("역 사이 요청 역의 길이가 기존 역 길이보다 긴 경우 등록할 수 없다.")
    @Test
    void addSectionWithLongerDistanceThanOrigin() {
        //  - 조건 : 위의 두 조건이 아닐 경우 (그러나 downStation 중에 하나가 걸쳐있다.) && 예외에 속하지 않는 경우
        //  - 새로운 길이를 뺀 나머지를 새롭게 추가된 역과의 길이로 설정한다.
        // when
        // 지하철_노선에_지하철역_등록_요청

        // then
        // 지하철_노선에_지하철역_등록_실패
    }

    @DisplayName("기존에 두가지 역이 모두 등록 되어 있는 경우 등록할 수 없다.")
    @Test
    void addSectionWithAlreadyRegisteredStations() {
        // when
        // 지하철_노선에_지하철역_등록_요청

        // then
        // 지하철_노선에_지하철역_등록_실패
    }

    @DisplayName("추가할 역이 모두 등록 되어 있지 않은 경우 등록할 수 없다.")
    @Test
    void addSectionWithNoRegisteredStations() {
        // when
        // 지하철_노선에_지하철역_등록_요청

        // then
        // 지하철_노선에_지하철역_등록_실패
    }
}
