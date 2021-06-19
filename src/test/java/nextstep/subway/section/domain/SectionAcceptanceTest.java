package nextstep.subway.section.domain;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
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

import java.util.HashMap;
import java.util.Map;

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
        StationResponse 교대역 = StationAcceptanceTest.지하철역_등록("교대역");

        // when
        Map<String, String> params = new HashMap<>();
        params.put("upStationId",강남역.getId().toString());
        params.put("downStationId",교대역.getId().toString());
        params.put("distance","10");

        ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/lines/"+이호선.getId()+"/sections")
                .then().log().all().extract();

        // then
        Assertions.assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    }


}
