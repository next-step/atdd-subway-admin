package nextstep.subway.section;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static nextstep.subway.AcceptanceFixture.식별_아이디_조회;
import static nextstep.subway.line.LineAcceptanceFixture.지하철_노선_생성_요청;
import static nextstep.subway.station.StationAcceptanceFixture.지하철역_생성_요청;

@DisplayName("구간 등록 인수 테스트")
public class SectionAcceptanceTest {

    ExtractableResponse<Response> 지하철_노선_생성_응답;
    @BeforeEach
    void set_up() {
        ExtractableResponse<Response> 강남역 = 지하철역_생성_요청("강남역");
        ExtractableResponse<Response> 잠실역 = 지하철역_생성_요청("잠실역");

        지하철_노선_생성_응답 = 지하철_노선_생성_요청(
                "강남잠실노선", "red", 식별_아이디_조회(강남역), 식별_아이디_조회(잠실역), 10
        );

    }

    /**
     * When 지하철 노선에 지하철역 등록 요청
     * Then 지하철 노선에 지하철역 등록됨
     */
    @DisplayName("노선에 구간을 등록한다.")
    @Test
    void add_section() {

    }

    @DisplayName("노선을 생성한 후 구간 설정 후 노선의 리스트 확인")
    @Test
    void line_add_section() {

    }
}
