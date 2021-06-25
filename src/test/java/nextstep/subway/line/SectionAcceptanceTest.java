package nextstep.subway.line;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.line.domain.Section;
import nextstep.subway.line.dto.SectionRequest;
import nextstep.subway.line.dto.SectionResponse;
import nextstep.subway.station.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class SectionAcceptanceTest extends AcceptanceTest {
    private Station 광교역;
    private Station 강남역;
    private Station 광교중앙역;
    private LineResponse 신분당선;

    @BeforeEach
    public void setup() {
        String name = "1호선";
        String color = "빨강";
        Long upStationId = 1L;
        Long downStationId = 2L;
        String distance = "10";

        광교역 = createStation("광교역");
        강남역 = createStation("강남역");
        광교중앙역 = createStation("광교중앙역");
        신분당선 = LineAcceptanceTest.createLine(LineRequest.builder()
                .name(name)
                .color(color)
                .upStationId(upStationId)
                .downStationId(downStationId)
                .distance(distance)
                .build()).as(LineResponse.class);
    }

    @Test
    @DisplayName("구간 추가하기 정상")
    public void addSection() {
        // given
        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .body(SectionRequest.builder()
                        .upStationId(1L)
                        .downStationId(3L)
                        .build())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/1/sections")
                .then().log().all()
                .extract();
        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.body().jsonPath().getObject(".",SectionResponse.class)).isEqualTo(
                new SectionResponse(1L)
        );
    }
    private Station createStation(String name) {
        // given
        Map<String, String> params = new HashMap<>();
        params.put("name", name);

        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/stations")
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isNotBlank();
        return response.body().jsonPath().getObject("$",Station.class);
    }
}
