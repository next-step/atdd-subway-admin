package nextstep.subway.line;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.BaseAcceptanceTest;
import nextstep.subway.common.RestAssuredTemplate;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.dto.LineRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import static nextstep.subway.station.StationAcceptanceTest.지하철역_생성됨;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철 노선 관련 기능")
public class LineAcceptanceTest extends BaseAcceptanceTest {

    @BeforeEach
    void setUp(@Autowired LineRepository lineRepository) {
        lineRepository.deleteAll();
    }

    /**
     * When 지하철 노선을 생성하면
     * Then 지하철 노선 목록 조회 시 생성한 노선을 찾을 수 있다
     */
    @Test
    void 지하철노선_생성() {
        // when
        ExtractableResponse<Response> response = 지하철_노선_생성됨("신분당선", "bg-red-600", 10);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    }

    /**
     * Given 2개의 지하철 노선을 생성하고
     * When 지하철 노선 목록을 조회하면
     * Then 지하철 노선 목록 조회 시 2개의 노선을 조회할 수 있다.
     */
    @Test
    void 지하철노선_목록_조회() {

    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 조회하면
     * Then 생성한 지하철 노선의 정보를 응답받을 수 있다.
     */
    @Test
    void 지하철노선_조회() {
        
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 수정하면
     * Then 해당 지하철 노선 정보는 수정된다
     */
    @Test
    void 지하철노선_수정() {

    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 삭제하면
     * Then 해당 지하철 노선 정보는 삭제된다
     */
    @Test
    void 지하철노선_삭제() {

    }

    private ExtractableResponse<Response> 지하철_노선_생성됨(String name, String color, Integer distance) {
        Long upStationId = 지하철역_생성됨("지하철역").body().jsonPath().getLong("id");
        Long downStationId = 지하철역_생성됨("새로운지하철역").body().jsonPath().getLong("id");

        return RestAssuredTemplate.post(new LineRequest(name, color, upStationId, downStationId, distance), "/lines");
    }

}
