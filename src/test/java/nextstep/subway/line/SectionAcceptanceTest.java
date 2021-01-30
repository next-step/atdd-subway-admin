package nextstep.subway.line;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.SectionRequest;
import nextstep.subway.station.StationAcceptanceTest;
import nextstep.subway.station.dto.StationResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

@DisplayName("지하철 노선의 구간 관련 기능")
public class SectionAcceptanceTest extends AcceptanceTest {

    SectionRequest sectionRequest;

    @BeforeEach
    public void setUp() {
        super.setUp();
        StationResponse 강남역 = StationAcceptanceTest.지하철역_생성("강남역").as(StationResponse.class);
        StationResponse 교대역 = StationAcceptanceTest.지하철역_생성("교대역").as(StationResponse.class);
        StationResponse 선릉역 = StationAcceptanceTest.지하철역_생성("선릉역").as(StationResponse.class);
        StationResponse 역삼역 = StationAcceptanceTest.지하철역_생성("역삼역").as(StationResponse.class);
        StationResponse 삼성역 = StationAcceptanceTest.지하철역_생성("삼성역").as(StationResponse.class);
        LineRequest lineRequest = new LineRequest("2호선", "green", 강남역.getId(), 선릉역.getId(), 10);
        LineAcceptanceTest.지하철_노선_생성_요청(lineRequest);
        sectionRequest = new SectionRequest(강남역.getId(), 선릉역.getId(), 100);

        //given
    }

    @Test
    @DisplayName("역 사이에 새로운 역을 등록할 경우")
    void name() {
        // when
        // 지하철_노선에_지하철역_등록_요청
        ExtractableResponse<Response> response = 지하철_노선에_지하철역_등록_요청(sectionRequest);

        // then
        // 지하철_노선에_지하철역_등록됨

    }

    private ExtractableResponse<Response> 지하철_노선에_지하철역_등록_요청(SectionRequest sectionRequest) {
        return RestAssured.given().log().all().
            body(sectionRequest).
            contentType(MediaType.APPLICATION_JSON_VALUE).
            when().
            post("/lines/").
            then().
            log().all().
            extract();
    }

    @Test
    @DisplayName("새로운 역을 상행 종점으로 등록할 경우")
    void name1() {
    }

    @Test
    @DisplayName("새로운 역을 하행 종점으로 등록할 경우")
    void name2() {
    }

    @Test
    @DisplayName("역 사이에 새로운 역을 등로갈 경우 기존 역 사이 길이보다 크거나 같으면 등록을 할 수 없음")
    void name3() {
    }

    @Test
    @DisplayName("A-B, B-C 에서 B-C를 등록하는 경우")
    void name4() {
    }

    @Test
    @DisplayName("A-B, B-C 에서 A-C를 등록하는 경우")
    void name5() {
    }

    @Test
    @DisplayName("상행역과 하행역 둘 중 하나도 포함되어 있지 않으면 추가할 수 없음")
    void name6() {
    }


}
