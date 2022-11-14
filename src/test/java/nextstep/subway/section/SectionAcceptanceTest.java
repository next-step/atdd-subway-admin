package nextstep.subway.section;

import nextstep.subway.RestAssuredSetUp;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("지하철 구간 기능")
public class SectionAcceptanceTest extends RestAssuredSetUp {
    /**
     * When 지하철노선을 생성하면
     * Then 지하철노선이 생성된다
     * Then 지하철노선 목록 조회 시 생성한 역을 찾을 수 있다
     */
    @DisplayName("역 사이에 새로운 역을 등록할 경우 기존 역 사이 길이보다 크거나 같으면 등록을 할 수 없음")
    @Test
    void createSectionWithOverDistance() {

    }


}
