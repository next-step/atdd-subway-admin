package nextstep.subway.line;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.line.dto.SectionRequest;
import nextstep.subway.station.dto.StationResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.Arrays;
import java.util.List;

import static nextstep.subway.line.LineAcceptanceTest.지하철_노선_등록되어_있음;
import static nextstep.subway.line.LineAcceptanceTest.지하철_노선_조회_요청;
import static nextstep.subway.line.SectionAcceptanceTest.구간_요청_파라미터_생성;
import static nextstep.subway.line.SectionAcceptanceTest.지하철_구간_등록_요청;
import static nextstep.subway.station.StationAcceptanceTest.지하철_역_등록되어_있음;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

public class SectionDeleteAcceptanceTest extends AcceptanceTest {

    private StationResponse 강남역;
    private StationResponse 역삼역;
    private StationResponse 삼성역;
    private StationResponse 사당역;
    private String LineLocation;

    @BeforeEach
    void beforeEach() {
        강남역 = 지하철_역_등록되어_있음("강남역").as(StationResponse.class);
        역삼역 = 지하철_역_등록되어_있음("역삼역").as(StationResponse.class);
        삼성역 = 지하철_역_등록되어_있음("삼성역").as(StationResponse.class);
        사당역 = 지하철_역_등록되어_있음("사당역").as(StationResponse.class);

        LineRequest 수인분당선 = new LineRequest("수인분당선", "yellow", 강남역.getId(), 역삼역.getId(), 10);
        LineLocation = 지하철_노선_등록되어_있음(수인분당선).header("Location");

        //given
        SectionRequest request = 구간_요청_파라미터_생성(역삼역.getId(), 삼성역.getId(), 5);
        지하철_구간이_등록_되어_있음(request, LineLocation);
    }

    @DisplayName("하행 종점이 제거될 경우 종점의 상행역이 하행 종점이 된다.")
    @Test
    void lastDownStationDelete() {
        //when
        ExtractableResponse<Response> response = 지하철_구간_삭제_요청(삼성역.getId(), LineLocation);
        //then
        지하철_구간_삭제됨(response);
        지하철_구간_검증(Arrays.asList(강남역, 역삼역), 10);
    }

    @DisplayName("상행 종점이 제거될 경우 종점의 하행역이 상행 종점이 된다.")
    @Test
    void firstUpStationDelete() {
        //when
        ExtractableResponse<Response> response = 지하철_구간_삭제_요청(강남역.getId(), LineLocation);
        //then
        지하철_구간_삭제됨(response);
        지하철_구간_검증(Arrays.asList(역삼역, 삼성역), 5);
    }

    @DisplayName("중간역이 제거 될 경우 두 구간을 재배치한다.")
    @Test
    void middleStationDelete() {
        //when
        ExtractableResponse<Response> response = 지하철_구간_삭제_요청(역삼역.getId(), LineLocation);
        //then
        지하철_구간_삭제됨(response);
        지하철_구간_검증(Arrays.asList(강남역, 삼성역), 15);
    }

    private void 지하철_구간_검증(List<StationResponse> excepted, int totalDistance) {
        ExtractableResponse<Response> lineResponse = 지하철_노선_조회_요청(LineLocation);
        LineResponse actual = lineResponse.as(LineResponse.class);
        assertAll(
                () -> assertThat(actual.getStations()).containsExactlyElementsOf(excepted),
                () -> assertThat(actual.getTotalDistance()).isEqualTo(totalDistance)
        );
    }

    @DisplayName("노선에 등록되지 않은 역은 제거할 수 없다.")
    @Test
    void notIncludeLineSection() {
        //when
        ExtractableResponse<Response> response = 지하철_구간_삭제_요청(사당역.getId(), LineLocation);
        //then
        지하철_구간_삭제_실패됨(response);
    }

    @DisplayName("구간이 하나인 노선은 제거할 수 없다.")
    @Test
    void notDeleteOneSection() {
        //given
        지하철_구간_삭제되어_있음(삼성역.getId());
        //when
        ExtractableResponse<Response> response = 지하철_구간_삭제_요청(역삼역.getId(), LineLocation);
        //then
        지하철_구간_삭제_실패됨(response);
    }

    private void 지하철_구간_삭제되어_있음(Long stationId) {
        지하철_구간_삭제_요청(stationId, LineLocation);
    }

    @DisplayName("존재하지 않는 지하철 역으로 제거할 수 없다.")
    @Test
    void notFoundStation() {
        //when
        ExtractableResponse<Response> response = 존재하지_않는_지하철역_삭제_요청();
        //then
        지하철_구간_삭제_실패됨(response);
    }

    private ExtractableResponse<Response> 존재하지_않는_지하철역_삭제_요청() {
        return 지하철_구간_삭제_요청(10L, LineLocation);
    }

    @DisplayName("존재하지 않는 노선에 구간을 제거할 수 없다.")
    @Test
    void notFoundLineDelete() {
        //when
        ExtractableResponse<Response> response = 존재하지_않는_노선_구간_삭제_요청();
        //then
        지하철_구간_삭제_실패됨(response);
    }

    private ExtractableResponse<Response> 존재하지_않는_노선_구간_삭제_요청() {
        return 지하철_구간_삭제_요청(10L, "lines/3");
    }

    private ExtractableResponse<Response> 지하철_구간_삭제_요청(Long stationId, String location) {
        return RestAssured
                .given().log().all()
                .param("stationId", stationId)
                .when().delete(location + "/sections")
                .then().log().all().extract();
    }

    private void 지하철_구간이_등록_되어_있음(SectionRequest request, String location) {
        지하철_구간_등록_요청(request, location);
    }

    private void 지하철_구간_삭제됨(ExtractableResponse<Response> response) {
        요청_결과_검증(response, HttpStatus.OK);
    }

    private void 지하철_구간_삭제_실패됨(ExtractableResponse<Response> response) {
        요청_결과_검증(response, HttpStatus.BAD_REQUEST);
    }
}
