package nextstep.subway.line;

import static nextstep.subway.line.LineAcceptanceMethods.*;
import static nextstep.subway.line.SectionAcceptanceMethods.*;
import static nextstep.subway.station.StationAcceptanceMethods.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.station.dto.StationResponse;

@DisplayName("지하철 구간 관련 기능")
public class SectionAcceptanceTest extends AcceptanceTest {

    StationResponse 상행종점;
    StationResponse 하행종점;
    StationResponse 추가될역;
    StationResponse 추추가될역;

    static LineResponse 신분당선;

    @BeforeEach
    public void setUp() {
        super.setUp();

        // given
        상행종점 = 지하철_역_등록되어_있음("강남역").as(StationResponse.class);
        하행종점 = 지하철_역_등록되어_있음("광교역").as(StationResponse.class);
        추가될역 = 지하철_역_등록되어_있음("양재역").as(StationResponse.class);
        추추가될역 = 지하철_역_등록되어_있음("광교중앙역").as(StationResponse.class);

        신분당선 = 지하철_노선_등록되어_있음(
                    new LineRequest("신분당선", "red darken-1",
                        상행종점.getId(), 하행종점.getId(), 120)).as(LineResponse.class);
    }

    @Test
    @DisplayName("역 사이에 새로운 역을 등록할 경우")
    void createSection() {
        // when
        ExtractableResponse<Response> response
            = 지하철_구간_생성_요청(상행종점, 추가될역, 30);

        // then
        지하철_구간_생성됨(response, 상행종점, 추가될역, 하행종점);
    }

    @Test
    @DisplayName("새로운 역을 상행 종점으로 등록할 경우")
    void createSectionWithUpStation() {
        // when
        ExtractableResponse<Response> response
            = 지하철_구간_생성_요청(추가될역, 상행종점, 40);

        // then
        지하철_구간_생성됨(response, 추가될역, 상행종점, 하행종점);
    }

    @Test
    @DisplayName("새로운 역을 하행 종점으로 등록할 경우")
    void createSectionWithDownStation() {
        // when
        ExtractableResponse<Response> response
            = 지하철_구간_생성_요청(하행종점, 추가될역, 40);

        // then
        지하철_구간_생성됨(response, 상행종점, 하행종점, 추가될역);
    }

    @Test
    @DisplayName("기존 역 사이 길이보다 크거나 같으면 등록을 할 수 없음")
    void createSectionFailedWithDistance() {
        ExtractableResponse<Response> response
            = 지하철_구간_생성_요청(상행종점, 추가될역, 120);

        지하철_구간_생성_실패됨(response);
    }

    @Test
    @DisplayName("상행역과 하행역이 이미 노선에 모두 등록되어 있다면 추가할 수 없음")
    void createSectionFailed_AlreadyExists() {
        ExtractableResponse<Response> response
            = 지하철_구간_생성_요청(상행종점, 하행종점, 60);

        지하철_구간_생성_실패됨(response);
    }

    @Test
    @DisplayName("상행역과 하행역 둘 중 하나도 포함되어 있지 않으면 추가할 수 없음")
    void createSectionFailed_NonOfThemExists() {
        ExtractableResponse<Response> response
            = 지하철_구간_생성_요청(추가될역, 추추가될역, 60);

        지하철_구간_생성_실패됨(response);
    }
}
