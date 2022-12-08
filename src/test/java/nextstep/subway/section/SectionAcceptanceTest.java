package nextstep.subway.section;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.DatabaseClear;
import nextstep.subway.dto.LineRequest;
import nextstep.subway.dto.SectionRequest;
import nextstep.subway.dto.StationResponse;
import nextstep.subway.line.LineAcceptanceTest;
import nextstep.subway.station.StationAcceptanceTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("구간 관리 기능")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class SectionAcceptanceTest {
    @LocalServerPort
    int port;
    @Autowired
    private DatabaseClear databaseClear;

    private Long 교대역ID;
    private Long 강남역ID;
    private Long 선릉역ID;
    private Long 삼성역ID;
    private Long 종합운동장역ID;
    LineRequest lineRequest;
    ExtractableResponse<Response> lineResponse;

    @BeforeEach
    public void setUp() {
        if (RestAssured.port == RestAssured.UNDEFINED_PORT) {
            RestAssured.port = port;
        }
        databaseClear.execute();

        교대역ID = StationAcceptanceTest.지하철역_생성("교대역").as(StationResponse.class).getId();
        강남역ID = StationAcceptanceTest.지하철역_생성("강남역").as(StationResponse.class).getId();
        선릉역ID = StationAcceptanceTest.지하철역_생성("선릉역").as(StationResponse.class).getId();
        삼성역ID = StationAcceptanceTest.지하철역_생성("삼성역").as(StationResponse.class).getId();
        종합운동장역ID = StationAcceptanceTest.지하철역_생성("종합운동장").as(StationResponse.class).getId();
        lineRequest = new LineRequest("2호선", "bg-red-600", 강남역ID, 선릉역ID, 7);

        lineResponse = LineAcceptanceTest.지하철_노선_생성(lineRequest);
    }

    @DisplayName("새로운 구간 등록")
    @Test
    public void addSection() {
        //given
        Long 역삼역ID = StationAcceptanceTest.지하철역_생성("역삼역").as(StationResponse.class).getId();
        //when
        ExtractableResponse<Response> response = 지하철_구간_생성(new SectionRequest(강남역ID, 역삼역ID, 4), lineResponse.jsonPath().getLong("id"));
        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());

    }

    @DisplayName("새로운 역을 상행 종점으로 구간 등록")
    @Test
    void addSectionUpStation() {
        //when
        ExtractableResponse<Response> response = 지하철_구간_생성(new SectionRequest(교대역ID, 강남역ID, 4), lineResponse.jsonPath().getLong("id"));
        //then
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value()),
                () -> assertThat(response.body().jsonPath().getList("stations")).hasSize(3),
                () -> assertThat(response.body().jsonPath().getList("stations.name")).contains("강남역", "교대역", "선릉역"),
                () -> assertThat(assertThat(response.jsonPath().getInt("distance")).isEqualTo(11))
        );
    }

    @DisplayName("새로운 역을 하행 종점으로 구간 등록")
    @Test
    void addSectionDownStation() {
        //when
        ExtractableResponse<Response> response = 지하철_구간_생성(new SectionRequest(선릉역ID, 삼성역ID, 4), lineResponse.jsonPath().getLong("id"));
        //then
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value()),
                () -> assertThat(response.body().jsonPath().getList("stations")).hasSize(3),
                () -> assertThat(response.body().jsonPath().getList("stations.name")).contains("강남역", "선릉역", "삼성역"),
                () -> assertThat(assertThat(response.jsonPath().getInt("distance")).isEqualTo(11))
        );
    }

    @DisplayName("기존구간의 상행/하행역이 모두 같으면 구간 생성 불가능")
    @Test
    public void isValidExistSection() {
        //when
        ExtractableResponse<Response> response = 지하철_구간_생성(new SectionRequest(강남역ID, 선릉역ID, 4), lineResponse.jsonPath().getLong("id"));
        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    @DisplayName("상행역과 하행역 둘 중 하나도 포함되어 있지 않으면 구간 생성 불가능")
    @Test
    public void isValidNotContainSection() {
        //when
        ExtractableResponse<Response> response = 지하철_구간_생성(new SectionRequest(삼성역ID, 종합운동장역ID, 4), lineResponse.jsonPath().getLong("id"));
        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());

    }

    public ExtractableResponse<Response> 지하철_구간_생성(SectionRequest sectionRequest, Long id) {
        ExtractableResponse<Response> response =
                RestAssured.given().log().all()
                        .pathParam("id", id)
                        .body(sectionRequest)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .when().post("lines/{id}/sections")
                        .then().log().all()
                        .extract();

        return response;
    }

    public static ExtractableResponse<Response> 지하철_노선_조회(String id) {
        return RestAssured.given().log().all()
                .when().get("/lines/{id}", id)
                .then().log().all()
                .extract();
    }
}
