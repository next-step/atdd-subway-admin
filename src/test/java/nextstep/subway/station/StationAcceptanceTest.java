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

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철역 관련 기능")
public class StationAcceptanceTest extends AcceptanceTest {

    //given
    public static final StationRequest 강남역 = new StationRequest("강남역");
    public static final StationRequest 역삼역 = new StationRequest("역삼역");

    public static ExtractableResponse<Response> 지하철_역_생성_요청(StationRequest request) {
        return RestAssured.given().log().all()
                .body(request)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/stations")
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 지하철_역_등록되어_있음(StationRequest request) {
        return 지하철_역_생성_요청(request);
    }

    @DisplayName("지하철역을 생성한다.")
    @Test
    void createStation() {
        // when
        ExtractableResponse<Response> response = 지하철_역_생성_요청(강남역);

        // then
        지하철_역_생성됨(response);
    }

    private void 지하철_역_생성됨(ExtractableResponse<Response> response) {
        요청_결과_검증(response, HttpStatus.CREATED);
        assertThat(response.header("Location")).isNotBlank();
    }

    @DisplayName("기존에 존재하는 지하철역 이름으로 지하철역을 생성한다.")
    @Test
    void createStationWithDuplicateName() {
        // given
        지하철_역_등록되어_있음(강남역);

        // when
        ExtractableResponse<Response> response = 지하철_역_생성_요청(강남역);

        // then
        지하철_역_생성_실패(response);
    }

    private void 지하철_역_생성_실패(ExtractableResponse<Response> response) {
        요청_결과_검증(response, HttpStatus.BAD_REQUEST);
    }

    @DisplayName("지하철역을 조회한다.")
    @Test
    void getStations() {
        /// given
        ExtractableResponse<Response> createResponse1 = 지하철_역_생성_요청(강남역);
        ExtractableResponse<Response> createResponse2 = 지하철_역_생성_요청(역삼역);

        // when
        ExtractableResponse<Response> response = 지하철_역_조회();

        // then
        지하철_역_조회됨(response, Stream.of(createResponse1, createResponse2));
    }

    private ExtractableResponse<Response> 지하철_역_조회() {
        return RestAssured.given().log().all()
                .when()
                .get("/stations")
                .then().log().all()
                .extract();
    }

    private void 지하철_역_조회됨(ExtractableResponse<Response> response, Stream<ExtractableResponse<Response>> created) {
        요청_결과_검증(response, HttpStatus.OK);
        List<Long> expectedLineIds = created
                .map(it -> Long.parseLong(it.header("Location").split("/")[2]))
                .collect(Collectors.toList());
        List<Long> resultLineIds = response.jsonPath().getList(".", StationResponse.class).stream()
                .map(StationResponse::getId)
                .collect(Collectors.toList());
        assertThat(resultLineIds).containsAll(expectedLineIds);
    }

    @DisplayName("지하철역을 제거한다.")
    @Test
    void deleteStation() {
        // given
        ExtractableResponse<Response> createResponse = 지하철_역_등록되어_있음(강남역);

        // when
        String uri = createResponse.header("Location");
        ExtractableResponse<Response> response = 지하철_역_삭제_요청(uri);

        // then
        지하철_역_삭제됨(response);
    }

    private ExtractableResponse<Response> 지하철_역_삭제_요청(String uri) {
        return RestAssured.given().log().all()
                .when()
                .delete(uri)
                .then().log().all()
                .extract();
    }

    private void 지하철_역_삭제됨(ExtractableResponse<Response> response) {
        요청_결과_검증(response, HttpStatus.NO_CONTENT);
    }
}
