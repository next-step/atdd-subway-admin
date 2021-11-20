package nextstep.subway.station;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.station.dto.StationRequest;
import nextstep.subway.station.dto.StationResponse;

@DisplayName("지하철역 관련 기능")
public class StationAcceptanceTest extends AcceptanceTest {
    
    private StationRequest 서울대입구역 = StationRequest.from("서울대입구역");
    private StationRequest 봉천역 = StationRequest.from("봉천역");
    
    @DisplayName("지하철역을 생성한다.")
    @Test
    void createStation() {
        // when
        ExtractableResponse<Response> response = 지하철역_생성_요청(서울대입구역);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isNotBlank();
    }

    @DisplayName("기존에 존재하는 지하철역 이름으로 지하철역을 생성한다.")
    @Test
    void createStationWithDuplicateName() {
        // given
        지하철역_등록되어_있음(서울대입구역);

        // when
        ExtractableResponse<Response> response = 지하철역_생성_요청(서울대입구역);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("지하철역을 조회한다.")
    @Test
    void getStations() {
        /// given
        StationResponse 서울대입구역_응답 = 지하철역_등록되어_있음(서울대입구역);
        StationResponse 봉천역_응답 = 지하철역_등록되어_있음(봉천역);

        // when
        ExtractableResponse<Response> response = 지하철역_목록_조회_요청();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        
        지하철역_목록_포함됨(Arrays.asList(서울대입구역_응답, 봉천역_응답), new ArrayList<>(response.jsonPath().getList(".",StationResponse.class)));
    }

    @DisplayName("지하철역을 제거한다.")
    @Test
    void deleteStation() {
        // given
        StationResponse 서울대입구역_응답 = 지하철역_등록되어_있음(서울대입구역);

        // when
        ExtractableResponse<Response> response =  지하철역_제거_요청(서울대입구역_응답);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }
    
    private ExtractableResponse<Response> 지하철역_생성_요청(StationRequest request) {
        return RestAssured
                .given().log().all()
                .body(request)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/stations")
                .then().log().all()
                .extract();
    }
    
    private StationResponse 지하철역_등록되어_있음(StationRequest request) {
        return 지하철역_생성_요청(request).as(StationResponse.class);
    }
    
    private ExtractableResponse<Response> 지하철역_목록_조회_요청() {
        return RestAssured
                .given().log().all()
                .when()
                .get("/stations")
                .then().log().all()
                .extract();
    }
    
    private void 지하철역_목록_포함됨(List<StationResponse> expectedResponse, List<StationResponse> actualResponse) {
        List<Long> expected = expectedResponse.stream()
                .map(StationResponse::getId)
                .collect(Collectors.toList());
        
        List<Long> actual = actualResponse.stream()
                            .map(StationResponse::getId)
                            .collect(Collectors.toList());
        
        assertThat(actual).containsAll(expected);
    }
    
    private ExtractableResponse<Response> 지하철역_제거_요청(StationResponse response) {
        return RestAssured
                .given().log().all()
                .when()
                .delete("/stations/{id}", response.getId())
                .then().log().all()
                .extract();
    }
}
