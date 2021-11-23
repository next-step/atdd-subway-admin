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
import org.springframework.http.MediaType;

import static nextstep.subway.station.StationAcceptanceTest.*;
import static nextstep.subway.line.LineAcceptanceTest.*;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철 구간 관련 기능")
public class LineSectionAcceptanceTest extends AcceptanceTest {
    private static final String BASE_URI = "lines";
    private static final int DISTANCE_5 = 5;
    private static final int DISTANCE_10 = 10;

    private Long 구간_테스트_노선_ID;
    private Long 변경_상행종점역_ID;
    private Long 최초_상행종점역_ID;
    private Long 최초_하행종점역_ID;
    private Long 변경_하행_종점역_ID;
    private Long 사이_추가_역_ID;

    private static final int 거리_5 = 5;
    private static final int 거리_100 = 100;

    @BeforeEach
    void setUpLine() {
        변경_상행종점역_ID = 지하철_역_등록되어_있음(변경_상행종점역);
        최초_상행종점역_ID = 지하철_역_등록되어_있음(최초_상행종점역);
        최초_하행종점역_ID = 지하철_역_등록되어_있음(최초_하행종점역);
        변경_하행_종점역_ID = 지하철_역_등록되어_있음(변경_하행_종점역);
        사이_추가_역_ID = 지하철_역_등록되어_있음(사이_추가_역);

        final LineRequest 구간_테스트_노선 = LineRequest.of("구간_테스트_노선", "사일런트라이트색", 최초_상행종점역_ID, 최초_하행종점역_ID, 거리_100);
        구간_테스트_노선_ID = 지하철_노선_등록되어_있음(구간_테스트_노선);
    }


    @DisplayName("역 사이에 새로운 역을 등록한다")
    @Test
    void createSection() {
        SectionRequest request = SectionRequest.of(최초_상행종점역_ID, 사이_추가_역_ID, 거리_5);
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(request)
                .when()
                .post(BASE_URI + "/{id}/sections", 구간_테스트_노선_ID)
                .then().log().all().extract();

        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        LineResponse lineResponse = responseLine(response);
        assertThat(lineResponse.getStations()).extracting(StationResponse::getId).containsExactly(최초_상행종점역_ID, 사이_추가_역_ID, 최초_하행종점역_ID);
    }

    @DisplayName("새로운 역을 상행 종점에 등록한다.")
    @Test
    void createSection2() {
        SectionRequest request = SectionRequest.of(변경_상행종점역_ID, 최초_상행종점역_ID, 거리_5);
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(request)
                .when()
                .post(BASE_URI + "/{id}/sections", 구간_테스트_노선_ID)
                .then().log().all().extract();

        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        LineResponse lineResponse = responseLine(response);
        assertThat(lineResponse.getStations()).extracting(StationResponse::getId).containsExactly(변경_상행종점역_ID, 최초_상행종점역_ID, 최초_하행종점역_ID);
    }

}
