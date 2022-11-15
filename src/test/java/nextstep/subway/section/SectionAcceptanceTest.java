package nextstep.subway.section;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.dto.SectionRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static nextstep.subway.AcceptanceFixture.*;
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

    @DisplayName("역 사이에 새로운 역을 등록할 경우 기존 역 사이 길이보다 크거나 같으면 등록을 할 수 없음")
    @Test
    void test() {

    }

    /**
     * Given 상행역과 하행역이 등록된 구간을 노선에 등록
     * When 위와 같은 구간을 생성하여 노선에 등록
     * Then 추가 불가 오류 확인
     */
    @DisplayName("상행역과 하행역이 이미 노선에 모두 등록되어 있다면 추가할 수 없음")
    @Test
    void register_up_and_down_stations() {

        // when
        SectionRequest 신림_강남_구간_요청 = new SectionRequest(식별_아이디_조회(신림역), 식별_아이디_조회(강남역), 5);
        ExtractableResponse<Response> 지하철_구간_생성_요청 = 지하철_구간_생성_요청(식별_아이디_조회(신림_강남_노선), 신림_강남_구간_요청);

        // then
        요청_실패(지하철_구간_생성_요청);

    }

    /**
     * Given 상행역과 하행역이 등록된 구간을 노선에 등록
     * When 상행역과 하행역이 포함되지 않은 구간 생성후 노선에 등록
     * Then 추가 불가 오류 확인
     */
    @DisplayName("상행역과 하행역 둘 중 하나도 포함되어 있지 않으면 추가할 수 없음")
    @Test
    void not_found_up_and_down_stations() {

        // given
        ExtractableResponse<Response> 사당역 = 지하철역_생성_요청("사당역");
        ExtractableResponse<Response> 잠실역 = 지하철역_생성_요청("잠실역");
        SectionRequest 사당_잠실_구간_요청 = new SectionRequest(식별_아이디_조회(사당역), 식별_아이디_조회(잠실역), 5);

        // when
        ExtractableResponse<Response> 지하철_구간_생성_요청 = 지하철_구간_생성_요청(식별_아이디_조회(신림_강남_노선), 사당_잠실_구간_요청);

        // then
        요청_실패(지하철_구간_생성_요청);

    }
}
