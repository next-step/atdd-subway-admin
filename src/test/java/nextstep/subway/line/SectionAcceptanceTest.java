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
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
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

    @ParameterizedTest
    @CsvSource({"노선 중앙에 downStation 추가하기,1,3,3",
            "노선 중앙에 upStation 추가하기,3,2,3",
            "새로운 상행역 추가,3,1,3",
            "새로운 하행역 추가,2,3,3"
    })
    @DisplayName("노선 중앙에 downStation 추가하기")
    public void addDownStationInMiddle(String testName, Long upStationId, Long downStationId, Long distance) {
        System.out.println(testName);
        // given
        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .body(SectionRequest.builder()
                        .upStationId(upStationId)
                        .downStationId(downStationId)
                        .distance(distance)
                        .build())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines/1/sections")
                .then().log().all()
                .extract();
        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.body().jsonPath().getObject(".",SectionResponse.class)).isEqualTo(
                SectionResponse.of(new Station(upStationId), new Station(downStationId),distance)
        );
    }

    @Test
    @DisplayName("distance가 기존보다 클때 에러")
    public void newSectionOverDistance() {
        // given
        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .body(SectionRequest.builder()
                        .upStationId(1L)
                        .downStationId(3L)
                        .distance(11L)
                        .build())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines/1/sections")
                .then().log().all()
                .extract();
        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    @DisplayName("distance가 기존보다 클때 에러")
    public void addDownStationInMiddle() {
        // given
        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .body(SectionRequest.builder()
                        .upStationId(1L)
                        .downStationId(3L)
                        .distance(11L)
                        .build())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines/1/sections")
                .then().log().all()
                .extract();
        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.body().jsonPath().getString("message")).contains("new Section should be smaller than exist Section");
    }

    @Test
    @DisplayName("upStation 과 downStation이 모두 라인에 이미 등록되어있는경우")
    public void upStationAndDownStationAlreadyInLine() {
        // given
        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .body(SectionRequest.builder()
                        .upStationId(1L)
                        .downStationId(2L)
                        .distance(3L)
                        .build())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines/1/sections")
                .then().log().all()
                .extract();
        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.body().jsonPath().getString("message")).contains("a section should be in line");
    }

    @Test
    @DisplayName("upStation과 downStation중 존재하지 않는 역인경우")
    public void upStationOrDownStationIsNotExists() {
        // given
        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .body(SectionRequest.builder()
                        .upStationId(3L)
                        .downStationId(5L)
                        .distance(3L)
                        .build())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines/1/sections")
                .then().log().all()
                .extract();
        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
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
