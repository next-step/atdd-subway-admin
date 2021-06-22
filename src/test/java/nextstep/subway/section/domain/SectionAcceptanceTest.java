package nextstep.subway.section.domain;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.exception.IncorrectSectionException;
import nextstep.subway.line.LineAcceptanceTest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.station.StationAcceptanceTest;
import nextstep.subway.station.dto.StationResponse;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("지하철 구간 관련 기능")
public class SectionAcceptanceTest extends AcceptanceTest {
    private StationResponse 강남역;
    private StationResponse 사당역;
    private LineResponse 이호선;



    @BeforeEach
    public void setUp() {
        super.setUp();
        강남역 = StationAcceptanceTest.지하철역_등록("강남역");
        사당역 = StationAcceptanceTest.지하철역_등록("사당역");
        이호선 = LineAcceptanceTest.지하철노선_등록("2호선","green", 강남역.getId(), 사당역.getId(), 4000);
    }


    @DisplayName("노선에 구간을 등록한다")
    @Test
    void createSection() {
        //given
        String newStationName = "교대역";
        StationResponse 교대역 = StationAcceptanceTest.지하철역_등록(newStationName);
        int distance = 1000;
        Map<String, String> params = 구간_추가_파라미터생성(교대역.getId(), distance);

        // when
        ExtractableResponse<Response> response = 구간추가(params);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    }


    private Map<String, String> 구간_추가_파라미터생성(Long stationId, int distance) {
        Map<String, String> params = new HashMap<>();
        params.put("upStationId",강남역.getId().toString());
        params.put("downStationId",stationId.toString());
        params.put("distance", String.valueOf(distance));
        return params;
    }


    @DisplayName("노선의 중간 역을 제거한다")
    @Test
    void deleteMiddleSection() {
        //given
        String newStationName = "교대역";
        StationResponse 교대역 = StationAcceptanceTest.지하철역_등록(newStationName);
        int distance = 1000;
        Map<String, String> params = 구간_추가_파라미터생성(교대역.getId(), distance);
        구간추가(params);

        // when
        ExtractableResponse<Response> response = 구간제거(교대역.getId());

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        ExtractableResponse<Response> lineResponse = LineAcceptanceTest.searchLine(이호선.getId());
        LineResponse searchedLine = lineResponse.jsonPath().getObject(".", LineResponse.class);
        assertThat(searchedLine.getStations().get(0).getName()).isEqualTo("강남역");
        assertThat(searchedLine.getStations().get(1).getName()).isEqualTo("사당역");
    }

    @DisplayName("노선에 상행종점역을 제거한다")
    @Test
    void deleteFirstSectionByStation() {
        //given
        String newStationName = "교대역";
        StationResponse 교대역 = StationAcceptanceTest.지하철역_등록(newStationName);
        int distance = 1000;
        Map<String, String> params = 구간_추가_파라미터생성(교대역.getId(), distance);
        구간추가(params);

        // when
        ExtractableResponse<Response> response = 구간제거(강남역.getId());

        // then
        Assertions.assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        ExtractableResponse<Response> lineResponse = LineAcceptanceTest.searchLine(이호선.getId());
        LineResponse searchedLine = lineResponse.jsonPath().getObject(".", LineResponse.class);
        assertThat(searchedLine.getStations().get(0).getName()).isEqualTo("교대역");
        assertThat(searchedLine.getStations().get(1).getName()).isEqualTo("사당역");
    }

    @DisplayName("노선에 하행 종점역을 제거한다")
    @Test
    void deleteLastSectionByStation() {
        //given
        String newStationName = "교대역";
        StationResponse 교대역 = StationAcceptanceTest.지하철역_등록(newStationName);
        int distance = 1000;
        Map<String, String> params = 구간_추가_파라미터생성(교대역.getId(), distance);
        구간추가(params);

        // when
        ExtractableResponse<Response> response = 구간제거(사당역.getId());

        // then
        Assertions.assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        ExtractableResponse<Response> lineResponse = LineAcceptanceTest.searchLine(이호선.getId());
        LineResponse searchedLine = lineResponse.jsonPath().getObject(".", LineResponse.class);
        assertThat(searchedLine.getStations().get(0).getName()).isEqualTo("강남역");
        assertThat(searchedLine.getStations().get(1).getName()).isEqualTo("교대역");
    }

    @DisplayName("구간이 하나일때 에러 발생")
    @Test
    void deleteOnlyOneSectionByStationError() {

        // when
        ExtractableResponse<Response> response = 구간제거(사당역.getId());

        // then
        Assertions.assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("노선에 없는 역으로 삭제 요청시 에러 발생")
    @Test
    void deleteSectionByNoUseStationError() {
        //given
        String newStationName = "교대역";
        StationResponse 교대역 = StationAcceptanceTest.지하철역_등록(newStationName);
        int distance = 1000;
        Map<String, String> params = 구간_추가_파라미터생성(교대역.getId(), distance);
        구간추가(params);
        String noUseStationName = "종로역";
        StationResponse 종로역 = StationAcceptanceTest.지하철역_등록(noUseStationName);

        // when
        ExtractableResponse<Response> response = 구간제거(종로역.getId());

        // then
        Assertions.assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }



    private ExtractableResponse<Response> 구간추가(Map<String, String> params) {
        return RestAssured
                .given().log().all()
                .pathParam("lineId",이호선.getId())
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/lines/{lineId}/sections")
                .then().log().all().extract();
    }

    private ExtractableResponse<Response> 구간제거(Long stationId) {
        return RestAssured
                .given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .pathParam("lineId",이호선.getId())
                .when().queryParam("stationId",stationId).delete("/lines/{lineId}/sections")
                .then().log().all().extract();

    }




}
