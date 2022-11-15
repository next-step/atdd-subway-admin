package nextstep.subway.section;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.dto.SectionRequest;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import static nextstep.subway.AcceptanceFixture.식별_아이디_조회;
import static nextstep.subway.AcceptanceFixture.요청_성공;
import static nextstep.subway.line.LineAcceptanceFixture.지하철_노선_생성_요청;
import static nextstep.subway.section.SectionAcceptanceFixture.지하철_구간_생성_요청;
import static nextstep.subway.station.StationAcceptanceFixture.지하철역_생성_요청;

@DisplayName("지하철 구간 등록 인수 테스트")
public class SectionAcceptanceTest extends AcceptanceTest {

    private ExtractableResponse<Response> 신림역;
    private ExtractableResponse<Response> 강남역;
    private ExtractableResponse<Response> 신림_강남_노선;

    @BeforeEach
    void set_up() {
        신림역 = 지하철역_생성_요청("신림역");
        강남역 = 지하철역_생성_요청("강남역");


        신림_강남_노선 = 지하철_노선_생성_요청(
                "신림강남노선", "red", 식별_아이디_조회(신림역), 식별_아이디_조회(강남역), 10
        );
    }

    /**
     * When 지하철 노선에 지하철 구간 등록 요청
     * Then 지하철 노선에 지하철역 등록됨
     */
    @DisplayName("노선에 구간을 등록한다.")
    @Test
    void add_section() {
        // given
        ExtractableResponse<Response> 사당역 = 지하철역_생성_요청("사당역");
        SectionRequest 신림_사당_구간_요청 = new SectionRequest(식별_아이디_조회(신림역), 식별_아이디_조회(사당역), 5);

        // when
        ExtractableResponse<Response> 신림_사당_구간_응답 = 지하철_구간_생성_요청(
                식별_아이디_조회(신림_강남_노선), 신림_사당_구간_요청
        );

        // then
        요청_성공(신림_사당_구간_응답);
    }

    @DisplayName("새로운 역을 상행 종점으로 등록할 경우")
    @Test
    void new_station_up_station_add() {

    }

    @DisplayName("노선을 생성한 후 구간 설정 후 노선의 리스트 확인")
    @Test
    void line_add_section() {

    }
}
