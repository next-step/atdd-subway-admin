package nextstep.subway.station;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.station.dto.StationRequest;
import nextstep.subway.station.dto.StationResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철역 관련 기능")
public class StationAcceptanceTest extends AcceptanceTest {
    @DisplayName("지하철역을 생성한다.")
    @Test
    void createStation() {
        // given
        StationRequest 강남역 = 강남역_생성_파라미터();

        // when
        ExtractableResponse<Response> response = 지하철_역_생성_요청(강남역);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isNotBlank();
    }

    @DisplayName("기존에 존재하는 지하철역 이름으로 지하철역을 생성한다.")
    @Test
    void createStationWithDuplicateName() {
        // given
        StationRequest 강남역 = 강남역_생성_파라미터();
        지하철_역_생성_요청(강남역);

        // when
        ExtractableResponse<Response> response = 지하철_역_생성_요청(강남역);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("지하철역을 조회한다.")
    @Test
    void getStations() {
        /// given
        ExtractableResponse<Response> createResponse1 = 지하철_역_생성_요청(강남역_생성_파라미터());
        ExtractableResponse<Response> createResponse2 = 지하철_역_생성_요청(역삼역_생성_파라미터());

        // when
        ExtractableResponse<Response> response = 지하철_역_조회_요청();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        List<Long> expectedLineIds = Arrays.asList(createResponse1, createResponse2).stream()
                .map(it -> Long.parseLong(it.header("Location").split("/")[2]))
                .collect(Collectors.toList());
        List<Long> resultLineIds = response.jsonPath().getList(".", StationResponse.class).stream()
                .map(it -> it.getId())
                .collect(Collectors.toList());
        assertThat(resultLineIds).containsAll(expectedLineIds);
    }

    @DisplayName("지하철역을 제거한다.")
    @Test
    void deleteStation() {
        // given
        ExtractableResponse<Response> createResponse = 지하철_역_생성_요청(강남역_생성_파라미터());

        // when
        ExtractableResponse<Response> response = 지하철_역_삭제_요청(createResponse);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }


    public static ExtractableResponse<Response> 지하철_역_생성_요청(StationRequest request) {
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .body(request)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/stations")
                .then().log().all()
                .extract();
        return response;
    }

    private ExtractableResponse<Response> 지하철_역_조회_요청() {
        return RestAssured.given().log().all()
                .when()
                .get("/stations")
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> 지하철_역_삭제_요청(ExtractableResponse<Response> createResponse) {
        String uri = createResponse.header("Location");
        return RestAssured.given().log().all()
                .when()
                .delete(uri)
                .then().log().all()
                .extract();
    }

    public static StationRequest 강남역_생성_파라미터() {
        return new StationRequest("강남역");
    }

    public static StationRequest 역삼역_생성_파라미터() {
        return new StationRequest("역삼역");
    }

    public static StationRequest 광교역_생성_파라미터() {
        return new StationRequest("역삼역");
    }

    public static StationRequest 신규역_생성_파라미터(String name) {
        return new StationRequest(name);
    }
}

