package nextstep.subway.station;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.station.dto.StationResponse;
import nextstep.subway.utils.CommonMethod;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철역 관련 기능")
public class StationAcceptanceTest extends AcceptanceTest {

    private static final String URL = "/stations";

    @DisplayName("지하철역을 생성한다.")
    @Test
    public void create_station() {
        // given
        String name = "강남역";

        // when
        ExtractableResponse<Response> response = createStation(name);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isNotBlank();
    }

    @DisplayName("기존에 존재하는 지하철역 이름으로 지하철역을 생성한다.")
    @Test
    void create_station_with_duplicated_name() {
        // given
        String name = "강남역";
        createStation(name);

        // when
        ExtractableResponse<Response> response = createStation(name);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("지하철역을 조회한다.")
    @Test
    void get_stations() {
        /// given
        Long firstId = getIdWithResponse(createStation("강남역"));
        Long secondId = getIdWithResponse(createStation("역삼역"));

        // when
        ExtractableResponse<Response> response = getStations();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        List<Long> resultLineIds = getIdsWithResponse(response);
        assertThat(resultLineIds).containsAll(Arrays.asList(firstId, secondId));
    }

    @DisplayName("지하철역을 제거한다.")
    @Test
    void deleteStation() {
        // given
        ExtractableResponse<Response> createResponse = createStation("강남역");

        // when
        String uri = createResponse.header("Location");
        ExtractableResponse<Response> response = RestAssured.given().log().all()
            .when()
            .delete(uri)
            .then().log().all()
            .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    public static ExtractableResponse<Response> createStation(String name) {
        Map<String, Object> params = new HashMap<>();
        params.put("name", name);
        return CommonMethod.create(params, URL);
    }

    private ExtractableResponse<Response> updateStation(String name) {
        Map<String, Object> params = new HashMap<>();
        params.put("name", name);
        return CommonMethod.update(params, URL);
    }

    private ExtractableResponse<Response> getStations() {
        return CommonMethod.get(URL);
    }

    private List<Long> getIdsWithResponse(ExtractableResponse<Response> response) {
        return response.jsonPath().getList(".", StationResponse.class).stream()
            .map(it -> it.getId())
            .collect(Collectors.toList());
    }

    private Long getIdWithResponse(ExtractableResponse<Response> response) {
        return response.jsonPath().getObject(".", StationResponse.class).getId();
    }

}
