package nextstep.subway.line;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.dto.LineResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static nextstep.subway.api.AssertMethod.*;
import static nextstep.subway.api.HttpMethod.*;
import static nextstep.subway.fixture.StationFixture.*;

@DisplayName("지하철 노선 관련 기능")
public class LineAcceptanceTest extends AcceptanceTest {
    @BeforeEach
    void setUpInLineAcceptanceTest() {
        createStationInAdvance();
    }

    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void createLine() {
        // when
        ExtractableResponse<Response> response = 지하철_노선_등록(팔호선_역_모음.get("암사역"));

        // then
        지하철_노선_생성_확인(response, 팔호선_역_모음.get("암사역"));
    }

    @DisplayName("기존에 존재하는 지하철 노선 이름으로 지하철 노선을 생성한다.")
    @Test
    void createLine2() {
        // given
        지하철_노선_등록(팔호선_역_모음.get("암사역"));

        // when
        ExtractableResponse<Response> response = 지하철_노선_등록(팔호선_역_모음.get("암사역"));

        // then
        지하철_노선_실패_확인(response);
    }

    @DisplayName("지하철 노선 목록을 조회한다.")
    @Test
    void getLines() {
        // given
        ExtractableResponse<Response> createdResponse1 = 지하철_노선_등록(팔호선_역_모음.get("잠실역"));
        ExtractableResponse<Response> createdResponse2 = 지하철_노선_등록(이호선_역_모음.get("강남역"));

        // when
        ExtractableResponse response = 지하철_노선_목록_조회();

        // then
        지하철_노선_목록_응답_확인(response);
        지하철_노선_목록_포함_확인(response, createdResponse1.as(LineResponse.class));
        지하철_노선_목록_포함_확인(response, createdResponse2.as(LineResponse.class));
    }

    @DisplayName("지하철 노선을 조회한다.")
    @Test
    void getLine() {
        // given
        ExtractableResponse<Response> createdResponse = 지하철_노선_등록(팔호선_역_모음.get("암사역"));

        // when
        ExtractableResponse<Response> response = 지하철_노선_조회(createdResponse);

        // then
        지하철_노선_정상_응답_확인(response);
        지하철_노선_포함_확인(response, createdResponse.as(LineResponse.class));
    }

    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void updateLine() {
        // given
        ExtractableResponse<Response> createdResponse = 지하철_노선_등록(팔호선_역_모음.get("암사역"));

        // when
        ExtractableResponse response = 지하철_노선_수정(createdResponse, 이호선_역_모음.get("강남역"));

        // then
        지하철_노선_정상_응답_확인(response);
        지하철_노선_수정_확인(지하철_노선_조회(createdResponse), 이호선_역_모음.get("강남역"));
    }

    @DisplayName("지하철 노선을 제거한다.")
    @Test
    void deleteLine() {
        // given
        ExtractableResponse<Response> createdResponse = 지하철_노선_등록(팔호선_역_모음.get("암사역"));

        // when
        ExtractableResponse<Response> response = 지하철_노선_제거(createdResponse);

        // then
        지하철_노선_삭제_확인(response);
        지하철_노선_조회_없음_확인(createdResponse);
    }
}
