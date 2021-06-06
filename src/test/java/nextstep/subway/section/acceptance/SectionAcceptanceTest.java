package nextstep.subway.section.acceptance;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.station.dto.StationResponse;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.HashMap;
import java.util.Map;

import static nextstep.subway.line.accpetance.step.LineAcceptanceStep.지하철_노선_등록되어_있음;
import static nextstep.subway.station.step.StationAcceptanceStep.지하철_역_등록되어_있음;

@DisplayName("구간 관련 기능")
public class SectionAcceptanceTest extends AcceptanceTest {
    private StationResponse 강남역;
    private StationResponse 잠실역;
    private StationResponse 선릉역;
    private LineResponse 이호선;

    @BeforeEach
    public void setUp() {
        super.setUp();

        // given
        강남역 = 지하철_역_등록되어_있음("교대역").as(StationResponse.class);
        잠실역 = 지하철_역_등록되어_있음("잠실역").as(StationResponse.class);
        선릉역 = 지하철_역_등록되어_있음("선릉역").as(StationResponse.class);

        이호선 = 지하철_노선_등록되어_있음(
                "2호선", "green", 강남역.getId(), 잠실역.getId(), 10
        ).as(LineResponse.class);
    }


    @DisplayName("노선에 구간을 등록한다.")
    @Test
    void addSection() {
        // when
        // 지하철_노선에_지하철역_등록_요청
        // when
        Map<String, String> params = new HashMap<>();
        params.put("upStationId", 강남역.getId() + "");
        params.put("downStationId", 선릉역.getId() + "");
        params.put("distance", 5 + "");
        ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/{lineId}/section", 이호선.getId())
                .then().log().all().extract();

        // then
        Assertions.assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());


        // then
        // 지하철_노선에_지하철역_등록됨
    }
}
