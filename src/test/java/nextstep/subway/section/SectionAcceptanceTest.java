package nextstep.subway.section;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.station.dto.StationRequest;
import org.junit.jupiter.api.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("구간 관련 기능 테스트")
public class SectionAcceptanceTest extends AcceptanceTest {

    @BeforeEach
    public void setUp(){
        super.setUp();
    }

    @DisplayName("구간 등록 테스트")
    @TestFactory
    Stream<DynamicTest> getSections(){
        return Stream.of(
                // given
                // 지하철 노선 등록되어 있음
                DynamicTest.dynamicTest("하나의 노선에 추가 구간을 등록할 수 있다", () -> {
                    //when
                    지하철역_여러_생성();
                    지하철_노선_등록(new LineRequest("1호선", "Purple", 1L, 2L, 10));
                    ExtractableResponse<Response> response = 지하철_노선_추가_등록(new SectionRequest(2L, 3L, 10), 1L);

                    // then
                    // 지하철 구간 등록됨
                    assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
                })
        );
    }

    void 지하철역_여러_생성() {
        지하철역_생성(new StationRequest("구로디지털단지역"));
        지하철역_생성(new StationRequest("신대방역"));
        지하철역_생성(new StationRequest("신림역"));
        지하철역_생성(new StationRequest("대방역"));
        지하철역_생성(new StationRequest("서울대입구역"));
        지하철역_생성(new StationRequest("사당역"));
    }

    ExtractableResponse<Response> 지하철역_생성(StationRequest stationRequest) {
        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(stationRequest)
                .when()
                .post("/stations")
                .then().log().all()
                .extract();
    }

    ExtractableResponse<Response> 지하철_노선_등록(LineRequest lineRequest) {
        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(lineRequest)
                .when()
                .post("/lines")
                .then().log().all()
                .extract();
    }


}
