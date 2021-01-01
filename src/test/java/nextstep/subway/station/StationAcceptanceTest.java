package nextstep.subway.station;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.station.dto.StationResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static nextstep.subway.station.StationAcceptanceTestRequest.지하철역_생성_요청;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("지하철역 관련 기능")
public class StationAcceptanceTest extends AcceptanceTest {
    @DisplayName("지하철역을 생성한다.")
    @Test
    void createStation() {
        // When
        ExtractableResponse<Response> 강남역 = 지하철역_생성_요청("강남역");

        // then
        지하철_생성_완료(강남역);
    }

    @DisplayName("기존에 존재하는 지하철역 이름으로 지하철역을 생성한다.")
    @Test
    void createStationWithDuplicateName() {
        // given
        String stationName = "강남역";
        지하철역_생성_요청(stationName);

        // when
        ExtractableResponse<Response> response = 지하철역_생성_요청(stationName);

        // then
        지하철역_생성_실패(response);
    }

    @DisplayName("지하철역을 조회한다.")
    @Test
    void getStations() {
        /// given
        ExtractableResponse<Response> 강남역 = 지하철역_생성_요청("강남역");
        ExtractableResponse<Response> 역삼역 = 지하철역_생성_요청("역삼역");

        // when
        ExtractableResponse<Response> response = 지하철역_모두_조회();

        // then
        지하철역_리스트_조회_완료(강남역, 역삼역, response);
    }

    @DisplayName("지하철역을 제거한다.")
    @Test
    void deleteStation() {
        // given
        ExtractableResponse<Response> 강남역 = 지하철역_생성_요청("강남역");

        // when
        ExtractableResponse<Response> response = 지하철역_삭제_요청(강남역);

        // then
        지하철역_삭제_성공(response);
    }

    private void 지하철_생성_완료(ExtractableResponse<Response> response) {
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value()),
                () -> assertThat(response.header("Location")).isNotBlank()
        );
    }

    private void 지하철역_생성_실패(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    private ExtractableResponse<Response> 지하철역_모두_조회() {
        return RestAssured.given().log().all()
                .when()
                .get("/stations")
                .then().log().all()
                .extract();
    }

    private void 지하철역_리스트_조회_완료(ExtractableResponse<Response> createResponse1, ExtractableResponse<Response> createResponse2, ExtractableResponse<Response> response) {
        List<Long> expectedLineIds = Stream.of(createResponse1, createResponse2)
                .map(it -> Long.parseLong(it.header("Location").split("/")[2]))
                .collect(Collectors.toList());
        List<Long> resultLineIds = response.jsonPath().getList(".", StationResponse.class).stream()
                .map(StationResponse::getId)
                .collect(Collectors.toList());
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(resultLineIds).containsAll(expectedLineIds)
        );
    }

    private ExtractableResponse<Response> 지하철역_삭제_요청(ExtractableResponse<Response> response) {
        return RestAssured.given().log().all()
                .when()
                .delete(response.header("Location"))
                .then().log().all()
                .extract();
    }

    private void 지하철역_삭제_성공(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }
}
