package nextstep.subway.line;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.line.dto.SectionRequest;
import nextstep.subway.station.StationAcceptanceTest;
import nextstep.subway.station.dto.StationResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

@DisplayName("지하철 노선의 구간 관련 기능")
public class SectionAcceptanceTest extends AcceptanceTest {

    public static final int DEFAULT_DISTANCE = 20;
    StationResponse 강남역;
    StationResponse 교대역;
    StationResponse 선릉역;
    StationResponse 역삼역;
    StationResponse 삼성역;
    LineRequest line2Request;
    LineResponse line2Response;

    @BeforeEach
    public void setUp() {
        super.setUp();
        //given
        교대역 = StationAcceptanceTest.지하철역_생성("교대역").as(StationResponse.class);
        강남역 = StationAcceptanceTest.지하철역_생성("강남역").as(StationResponse.class);
        선릉역 = StationAcceptanceTest.지하철역_생성("선릉역").as(StationResponse.class);
        역삼역 = StationAcceptanceTest.지하철역_생성("역삼역").as(StationResponse.class);
        삼성역 = StationAcceptanceTest.지하철역_생성("삼성역").as(StationResponse.class);

        line2Request = new LineRequest("2호선", "green", 강남역.getId(), 역삼역.getId(), DEFAULT_DISTANCE);
        line2Response = LineAcceptanceTest.지하철_노선_생성_요청(line2Request).as(LineResponse.class);
    }

    @Test
    @DisplayName("새로운 역을 상행 종점으로 등록할 경우")
    void name1() {
        //given
        SectionRequest sectionRequest = new SectionRequest(교대역.getId(), 강남역.getId(), 10);
        // when
        // 지하철_노선에_지하철역_등록_요청
        ExtractableResponse<Response> response = 지하철_노선에_지하철역_등록_요청(line2Response, sectionRequest);

        // then
        // 지하철_노선에_지하철역_등록됨역
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    @DisplayName("새로운 역을 하행 종점으로 등록할 경우")
    void name2() {
        //given
        SectionRequest sectionRequest = new SectionRequest(역삼역.getId(), 삼성역.getId(), 10);
        // when
        // 지하철_노선에_지하철역_등록_요청
        ExtractableResponse<Response> response = 지하철_노선에_지하철역_등록_요청(line2Response, sectionRequest);

        // then
        // 지하철_노선에_지하철역_등록됨역
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    @DisplayName("역 사이에 새로운 역을 등록할 경우 기존 역 사이 길이보다 크거나 같으면 등록을 할 수 없음")
    void name3() {
        //given
        SectionRequest sectionRequest = new SectionRequest(선릉역.getId(), 역삼역.getId(), DEFAULT_DISTANCE);

        // when
        // then
        assertThatThrownBy(()->{
            지하철_노선에_지하철역_등록_요청(line2Response, sectionRequest);
        }).isInstanceOf(IllegalArgumentException.class);


    }

    @Test
    @DisplayName("A-B, B-C 에서 B-C를 등록하는 경우")
    void name4() {
        //given
        SectionRequest sectionRequest = new SectionRequest(선릉역.getId(), 역삼역.getId(), 10);
        지하철_노선에_지하철역_등록_요청(line2Response, sectionRequest);

        // when
        // then
        assertThatThrownBy(()->{
            지하철_노선에_지하철역_등록_요청(line2Response, sectionRequest);
        }).isInstanceOf(IllegalArgumentException.class);


    }

    @Test
    @DisplayName("A-B, B-C 에서 A-C를 등록하는 경우")
    void name5() {
        //given
        SectionRequest sectionRequest = new SectionRequest(강남역.getId(), 선릉역.getId(), 10);
        지하철_노선에_지하철역_등록_요청(line2Response, sectionRequest);

        // when
        // then
        assertThatThrownBy(()->{
            지하철_노선에_지하철역_등록_요청(line2Response, sectionRequest);
        }).isInstanceOf(IllegalArgumentException.class);


    }

    @Test
    @DisplayName("상행역과 하행역 둘 중 하나도 포함되어 있지 않으면 추가할 수 없음")
    void name6() {
        //given
        SectionRequest sectionRequest = new SectionRequest(교대역.getId(), 삼성역.getId(), 10);
        지하철_노선에_지하철역_등록_요청(line2Response, sectionRequest);

        // when
        // then
        assertThatThrownBy(()->{
            지하철_노선에_지하철역_등록_요청(line2Response, sectionRequest);
        }).isInstanceOf(IllegalArgumentException.class);


    }

    private ExtractableResponse<Response> 지하철_노선에_지하철역_등록_요청(LineResponse lineResponse, SectionRequest sectionRequest) {
        return RestAssured.given().log().all().
            body(sectionRequest).
            contentType(MediaType.APPLICATION_JSON_VALUE).
            when().
            post("/lines/{lineId}/sections", lineResponse.getId()).
            then().
            log().all().
            extract();
    }

}
