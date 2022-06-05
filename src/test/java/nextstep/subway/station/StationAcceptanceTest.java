package nextstep.subway.station;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.BaseAcceptanceTest;
import nextstep.subway.common.RestAssuredTemplate;
import nextstep.subway.dto.StationRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철역 관련 기능")
public class StationAcceptanceTest extends BaseAcceptanceTest {

    /**
     * When 지하철역을 생성하면
     * Then 지하철역이 생성된다
     * Then 지하철역 목록 조회 시 생성한 역을 찾을 수 있다
     */
    @DisplayName("지하철역을 생성한다.")
    @Test
    void createStation() {
        // given
        String name = "강남역";

        // when
        ExtractableResponse<Response> response = 지하철역_생성됨(name);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());

        // then
        List<String> stationNames = 지하철역_전체_조회();
        assertThat(stationNames).containsAnyOf(name);
    }

    /**
     * Given 지하철역을 생성하고
     * When 기존에 존재하는 지하철역 이름으로 지하철역을 생성하면
     * Then 지하철역 생성이 안된다
     */
    @DisplayName("기존에 존재하는 지하철역 이름으로 지하철역을 생성한다.")
    @Test
    void createStationWithDuplicateName() {
        // given
        String name = "강남역";

        지하철역_생성됨(name);

        // when
        ExtractableResponse<Response> response = 지하철역_생성됨(name);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    /**
     * Given 2개의 지하철역을 생성하고
     * When 지하철역 목록을 조회하면
     * Then 2개의 지하철역을 응답 받는다
     */
    @DisplayName("지하철역을 조회한다.")
    @Test
    void getStations() {
        // given
        지하철역_생성됨("강남역");

        지하철역_생성됨("잠실역");

        // when
        List<String> stationNames = 지하철역_전체_조회();

        //then
        assertThat(stationNames).containsExactly("강남역", "잠실역");
    }

    /**
     * Given 지하철역을 생성하고
     * When 그 지하철역을 삭제하면
     * Then 그 지하철역 목록 조회 시 생성한 역을 찾을 수 없다
     */
    @DisplayName("지하철역을 제거한다.")
    @Test
    void deleteStation() {
        // given
        long stationId = 지하철역_생성됨("강남역").body().jsonPath().getLong("id");

        // when
        지하철역_삭제(stationId);

        // then
        List<String> stationNames = 지하철역_전체_조회();

        assertThat(stationNames).isEmpty();
    }

    public static ExtractableResponse<Response> 지하철역_생성됨(String name) {
        return RestAssuredTemplate.post("/stations", new StationRequest(name));
    }

    public static ExtractableResponse<Response> 지하철역_삭제(long stationId) {
        return RestAssuredTemplate.delete("/stations/" + stationId);
    }

    public static List<String> 지하철역_전체_조회() {
        return RestAssuredTemplate.get("/stations").jsonPath().getList("name", String.class);
    }

}
