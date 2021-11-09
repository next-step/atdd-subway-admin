package nextstep.subway.station;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.junit.jupiter.api.Assertions.assertAll;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.mapper.TypeRef;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.station.dto.StationResponse;
import org.assertj.core.api.AbstractIntegerAssert;
import org.assertj.core.api.AbstractListAssert;
import org.assertj.core.api.ObjectAssert;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.springframework.http.HttpStatus;

@DisplayName("지하철역 관련 기능")
class StationAcceptanceTest extends AcceptanceTest {

    @DisplayName("지하철역을 생성한다.")
    @Test
    void
    createStation() {
        // given
        String gangnam = "강남역";

        //when
        ExtractableResponse<Response> response = 지하철역_생성_요청(gangnam);

        // then
        지하철_역_생성됨(response, gangnam);
    }

    @ParameterizedTest(name = "[{index}] 지하철 역 이름이 \"{0}\" 으로는 생성할 수 없다.")
    @DisplayName("이름이 비어있는 상태로 지하철역을 생성한다.")
    @NullAndEmptySource
    void createStation_emptyName_400(String emptyOrNull) {
        //when
        ExtractableResponse<Response> response = 지하철역_생성_요청(emptyOrNull);

        // then
        지하철_역_생성_실패됨(response);
    }

    @DisplayName("기존에 존재하는 지하철역 이름으로 지하철역을 생성한다.")
    @Test
    void createStationWithDuplicateName() {
        // given
        String gangnamStation = "강남역";
        지하철_역_등록되어_있음(gangnamStation);

        // when
        ExtractableResponse<Response> response = 지하철역_생성_요청(gangnamStation);

        // then
        지하철_역_생성_실패됨(response);
    }

    @DisplayName("지하철역을 조회한다.")
    @Test
    void getStations() {
        /// given
        StationResponse createdGangnam = 지하철_역_등록되어_있음("강남역").as(StationResponse.class);
        StationResponse createdYeoksam = 지하철_역_등록되어_있음("역삼역").as(StationResponse.class);

        // when
        ExtractableResponse<Response> response = 지하철_역_목록_조회_요청();

        //then
        assertAll(
            () -> 지하철_역_목록_응답됨(response),
            () -> 지하철_역_목록_포함됨(response, Arrays.asList(createdGangnam, createdYeoksam))
        );
    }

    @DisplayName("지하철역을 제거한다.")
    @Test
    void deleteStation() {
        // given
        ExtractableResponse<Response> createdResponse = 지하철_역_등록되어_있음("강남역");

        // when
        ExtractableResponse<Response> response = 지하철_역_삭제_요청(createdResponse);

        // then
        지하철_역_삭제됨(response);
    }

    private void 지하철_역_생성_실패됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode())
            .isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    private AbstractListAssert<?, List<? extends Tuple>, Tuple, ObjectAssert<Tuple>> 지하철_역_목록_포함됨(
        ExtractableResponse<Response> response, List<StationResponse> expectedStations) {
        List<StationResponse> stationResponses = response.as(new TypeRef<List<StationResponse>>() {
        });
        return assertThat(stationResponses)
            .extracting(StationResponse::getId, StationResponse::getName)
            .containsExactly(
                expectedStations.stream()
                    .map(station -> tuple(station.getId(), station.getName()))
                    .toArray(Tuple[]::new)
            );
    }

    private AbstractIntegerAssert<?> 지하철_역_목록_응답됨(ExtractableResponse<Response> response) {
        return assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    private ExtractableResponse<Response> 지하철_역_목록_조회_요청() {
        return RestAssured.given().log().all()
            .when()
            .get("/stations")
            .then().log().all()
            .extract();
    }

    private AbstractIntegerAssert<?> 지하철_역_삭제됨(ExtractableResponse<Response> response) {
        return assertThat(response.statusCode())
            .isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    private ExtractableResponse<Response> 지하철_역_삭제_요청(
        ExtractableResponse<Response> createdResponse) {
        return RestAssured.given().log().all()
            .when()
            .delete(headerLocation(createdResponse))
            .then().log().all()
            .extract();
    }

    private String headerLocation(ExtractableResponse<Response> response) {
        return response.header("Location");
    }

    private void 지하철_역_생성됨(ExtractableResponse<Response> response, String expectedName) {
        StationResponse station = response.as(StationResponse.class);
        assertAll(
            () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value()),
            () -> assertThat(headerLocation(response)).isNotBlank(),
            () -> assertThat(station.getId()).isNotNull(),
            () -> assertThat(station.getName()).isEqualTo(expectedName),
            () -> assertThat(station.getCreatedDate()).isNotNull(),
            () -> assertThat(station.getModifiedDate()).isNotNull()
        );
    }

    private ExtractableResponse<Response> 지하철역_생성_요청(String name) {
        return RestAssured.given().log().all()
            .body(stationBody(name))
            .contentType(ContentType.JSON)
            .when()
            .post("/stations")
            .then().log().all()
            .extract();
    }

    private ExtractableResponse<Response> 지하철_역_등록되어_있음(String name) {
        return RestAssured.given()
            .body(stationBody(name))
            .contentType(ContentType.JSON)
            .post("/stations")
            .then()
            .extract();
    }

    private Map<String, String> stationBody(String name) {
        Map<String, String> params = new HashMap<>();
        params.put("name", name);
        return params;
    }
}
