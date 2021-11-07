package nextstep.subway.station;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.mapper.TypeRef;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.station.dto.StationResponse;

@DisplayName("지하철역 관련 기능")
class StationAcceptanceTest extends AcceptanceTest {

    @DisplayName("지하철역을 생성한다.")
    @Test
    void createStation() {
        // given, when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
            .body(stationBody("강남역"))
            .contentType(ContentType.JSON)
            .when()
            .post("/stations")
            .then().log().all()
            .extract();

        // then
        assertAll(
            () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value()),
            () -> assertThat(response.header("Location")).isNotBlank()
        );
    }

    @DisplayName("기존에 존재하는 지하철역 이름으로 지하철역을 생성한다.")
    @Test
    void createStationWithDuplicateName() {
        // given
        Map<String, String> body = stationBody("강남역");
        createStation(body);

        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
            .body(body)
            .contentType(ContentType.JSON)
            .when()
            .post("/stations")
            .then()
            .log().all()
            .extract();

        // then
        assertThat(response.statusCode())
            .isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("지하철역을 조회한다.")
    @Test
    void getStations() {
        /// given
        StationResponse createdGangnam = createStation(stationBody("강남역")).as(StationResponse.class);
        StationResponse createdYeoksam = createStation(stationBody("역삼역")).as(StationResponse.class);

        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
            .when()
            .get("/stations")
            .then().log().all()
            .extract();

        //then
        List<StationResponse> stationResponses = response.as(new TypeRef<List<StationResponse>>() {
        });
        assertAll(
            () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
            () -> assertThat(stationResponses)
                .extracting(StationResponse::getId, StationResponse::getName)
                .containsExactly(
                    tuple(createdGangnam.getId(), createdGangnam.getName()),
                    tuple(createdYeoksam.getId(), createdYeoksam.getName())
                )
        );
    }

    @DisplayName("지하철역을 제거한다.")
    @Test
    void deleteStation() {
        // given
        String uri = createStation(stationBody("강남역"))
            .header("Location");

        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
            .when()
            .delete(uri)
            .then().log().all()
            .extract();

        // then
        assertThat(response.statusCode())
            .isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    private ExtractableResponse<Response> createStation(Map<String, String> body) {
        return RestAssured.given()
            .body(body)
            .contentType(ContentType.JSON)
            .post("/stations")
            .then()
            .extract();
    }

    private Map<String, String> stationBody(String 강남역) {
        Map<String, String> params = new HashMap<>();
        params.put("name", 강남역);
        return params;
    }
}
