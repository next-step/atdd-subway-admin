package nextstep.subway.line;

import static nextstep.subway.station.StationAcceptanceTest.지하철역_등록되어_있음;
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
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.station.dto.StationRequest;
import nextstep.subway.station.dto.StationResponse;

@DisplayName("지하철 노선 관련 기능")
public class LineAcceptanceTest extends AcceptanceTest {
    
    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void createLine() {
        // given
        StationResponse 강남역 = 지하철역_등록되어_있음(StationRequest.from("강남역"));
        StationResponse 양재역 = 지하철역_등록되어_있음(StationRequest.from("양재역"));
        
        LineRequest 신분당선 = LineRequest.of("신분당선", "bg-red-600", 강남역.getId(), 양재역.getId(), 10);
        
        // when
        ExtractableResponse<Response> response = 지하철_노선_생성_요청(신분당선);
        
        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    }

    @DisplayName("기존에 존재하는 지하철 노선 이름으로 지하철 노선을 생성한다.")
    @Test
    void createLine2() {
        // given
        StationResponse 강남역 = 지하철역_등록되어_있음(StationRequest.from("강남역"));
        StationResponse 양재역 = 지하철역_등록되어_있음(StationRequest.from("양재역"));
        
        LineRequest 신분당선 = LineRequest.of("신분당선", "bg-red-600", 강남역.getId(), 양재역.getId(), 10);
        
        지하철_노선_등록되어_있음(신분당선);

        // when
        ExtractableResponse<Response> response = 지하철_노선_생성_요청(신분당선);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("지하철 노선 목록을 조회한다.")
    @Test
    void getLines() {
        // given
        StationResponse 강남역 = 지하철역_등록되어_있음(StationRequest.from("강남역"));
        StationResponse 양재역 = 지하철역_등록되어_있음(StationRequest.from("양재역"));
        StationResponse 교대역 = 지하철역_등록되어_있음(StationRequest.from("교대역"));
        
        LineRequest 신분당선 = LineRequest.of("신분당선", "bg-red-600", 강남역.getId(), 양재역.getId(), 10);
        LineRequest 이호선 = LineRequest.of("2호선", "bg-red-600", 교대역.getId(), 강남역.getId(), 8);
        
        LineResponse 신분당선_응답 = 지하철_노선_등록되어_있음(신분당선);
        LineResponse 이호선_응답 = 지하철_노선_등록되어_있음(이호선);

        // when
        ExtractableResponse<Response> response = 지하철_노선_목록_조회_요청();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        
        지하철_노선_목록_포함됨(Arrays.asList(신분당선_응답, 이호선_응답), new ArrayList<>(response.jsonPath().getList(".",LineResponse.class)));
    }

    @DisplayName("지하철 노선을 조회한다.")
    @Test
    void getLine() {
        // given
        StationResponse 강남역 = 지하철역_등록되어_있음(StationRequest.from("강남역"));
        StationResponse 양재역 = 지하철역_등록되어_있음(StationRequest.from("양재역"));
        
        LineRequest 신분당선 = LineRequest.of("신분당선", "bg-red-600", 강남역.getId(), 양재역.getId(), 10);
        
        LineResponse 신분당선_응답 = 지하철_노선_등록되어_있음(신분당선);

        // when
        ExtractableResponse<Response> response =  지하철_노선_조회_요청(신분당선_응답.getId());

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void updateLine() {
        // given
        StationResponse 강남역 = 지하철역_등록되어_있음(StationRequest.from("강남역"));
        StationResponse 양재역 = 지하철역_등록되어_있음(StationRequest.from("양재역"));
        
        LineRequest 구분당선 = LineRequest.of("구분당선", "bg-blue-600", 강남역.getId(), 양재역.getId(), 13);
        LineRequest 신분당선 = LineRequest.of("신분당선", "bg-red-600", 강남역.getId(), 양재역.getId(), 10);
        
        LineResponse 구분당선_응답 = 지하철_노선_등록되어_있음(구분당선);

        // when
        ExtractableResponse<Response> response =  지하철_노선_수정_요청(신분당선, 구분당선_응답);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @DisplayName("지하철 노선을 제거한다.")
    @Test
    void deleteLine() {
        // given
        StationResponse 강남역 = 지하철역_등록되어_있음(StationRequest.from("강남역"));
        StationResponse 양재역 = 지하철역_등록되어_있음(StationRequest.from("양재역"));
        
        LineRequest 신분당선 = LineRequest.of("신분당선", "bg-red-600", 강남역.getId(), 양재역.getId(), 10);
        
        LineResponse 신분당선_응답 = 지하철_노선_등록되어_있음(신분당선);

        // when
        ExtractableResponse<Response> response =  지하철_노선_제거_요청(신분당선_응답);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }
    
    
    private ExtractableResponse<Response> 지하철_노선_생성_요청(LineRequest request) {
        return RestAssured
                .given().log().all()
                .body(request)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then().log().all()
                .extract();
    }
    
    private LineResponse 지하철_노선_등록되어_있음(LineRequest request) {
        return 지하철_노선_생성_요청(request).as(LineResponse.class);
    }
    
    private ExtractableResponse<Response> 지하철_노선_목록_조회_요청() {
        return RestAssured
                .given().log().all()
                .when()
                .get("/lines")
                .then().log().all()
                .extract();
    }
    
    private ExtractableResponse<Response> 지하철_노선_조회_요청(Long id) {
        return RestAssured
                .given().log().all()
                .when()
                .get("/lines/{id}", id)
                .then().log().all()
                .extract();
    }
    
    private void 지하철_노선_목록_포함됨(List<LineResponse> expectedResponse, List<LineResponse> actualResponse) {
        List<Long> expected = expectedResponse.stream()
                .map(LineResponse::getId)
                .collect(Collectors.toList());
        
        List<Long> actual = actualResponse.stream()
                            .map(LineResponse::getId)
                            .collect(Collectors.toList());
        
        assertThat(actual).containsAll(expected);
    }
    
    private ExtractableResponse<Response> 지하철_노선_수정_요청(LineRequest request, LineResponse response) {
        return RestAssured
                .given().log().all()
                .body(request)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .put("/lines/{id}", response.getId())
                .then().log().all()
                .extract();
    }
    
    private ExtractableResponse<Response> 지하철_노선_제거_요청(LineResponse response) {
        return RestAssured
                .given().log().all()
                .when()
                .delete("/lines/{id}", response.getId())
                .then().log().all()
                .extract();
    }
}
