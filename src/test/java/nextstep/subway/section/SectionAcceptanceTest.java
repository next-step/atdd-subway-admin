package nextstep.subway.section;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.common.AcceptanceTest;
import nextstep.subway.dto.LineRequest;
import nextstep.subway.dto.LineResponse;
import nextstep.subway.dto.SectionRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.List;

import static nextstep.subway.line.LineAcceptanceTest.LineAcceptanceTemplate.id_추출;
import static nextstep.subway.line.LineAcceptanceTest.LineAcceptanceTemplate.지하철_노선_생성;
import static nextstep.subway.section.SectionAcceptanceTest.SectionAcceptanceTemplate.*;
import static nextstep.subway.station.StationAcceptanceTest.StationAcceptanceTemplate.지하철역_생성;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("구간 관련 기능 구현")
public class SectionAcceptanceTest extends AcceptanceTest {
    private Long 양재역_id;
    private Long 정자역_id;
    private LineResponse 신분당선;

    @BeforeEach
    public void setUp() {
        super.setUp();

        양재역_id = id_추출(지하철역_생성("양재역"));
        정자역_id = id_추출(지하철역_생성("정자역"));

        LineRequest 노선 = new LineRequest("신분당선", "red", 양재역_id, 정자역_id, 10);
        신분당선 = 지하철_노선_생성(노선).as(LineResponse.class);
    }

    /**
     * Given 새로운 구간에 대한 지하철역을 생성하고
     * When 기존에 존재하는 지하철 노선에 새로운 구간을 등록하면
     * Then 지하철 노선에 구간이 등록된다.
     */
    @Test
    void 노선에_구간을_등록한다() {
        // given
        Long 판교역_id = id_추출(지하철역_생성("판교역"));
        SectionRequest 신규_구간 = new SectionRequest(판교역_id, 정자역_id, 3);

        // when
        ExtractableResponse<Response> 신규_구간이_등록된_노선 = 노선에_신규_구간을_등록(신분당선, 신규_구간);

        // then
        노선에_신규_구간이_정상_등록된다(신규_구간이_등록된_노선);
    }

    /**
     * Given 역 사이에 새로운 역을 등록하고
     * When 기존에 존재하는 지하철 노선에 새로운 구간을 등록하면
     * Then 새로운 구간 길이를 뺀 나머지 구간 길이가 새롭게 추가된 역과의 길이로 설정된다.
     */
    @Test
    void 역_사이에_새로운_구간을_등록한다() {
        // given
        Long 판교역_id = id_추출(지하철역_생성("판교역"));
        SectionRequest 신규_구간 = new SectionRequest(판교역_id, 정자역_id, 3);

        // when
        ExtractableResponse<Response> 신규_구간이_등록된_노선 = 노선에_신규_구간을_등록(신분당선, 신규_구간);

        // then
        신규_구간과_분리된_구간_길이의_합이_원래_구간의_길이와_같다(신규_구간이_등록된_노선, 10);
    }

    /**
     * Given 새로운 역을 상행 종점으로 등록하고
     * When 새로운 구간을 등록하면
     * Then 지하철 노선에 구간이 등록된다.
     */
    @Test
    void 새로운_역을_상행_종점으로_등록한다() {
        // given
        Long 강남역_id = id_추출(지하철역_생성("강남역"));
        SectionRequest 신규_구간 = new SectionRequest(강남역_id, 양재역_id, 3);

        // when
        ExtractableResponse<Response> 신규_구간이_등록된_노선 = 노선에_신규_구간을_등록(신분당선, 신규_구간);

        // then
        노선에_신규_구간이_정상_등록된다(신규_구간이_등록된_노선);
    }

    /**
     * Given 새로운 역을 하행 종점으로 등록하고
     * When 새로운 구간을 등록하면
     * Then 지하철 노선에 구간이 등록된다.
     */
    @Test
    void 새로운_역을_하행_종점으로_등록한다() {
        // given
        Long 광교역_id = id_추출(지하철역_생성("광교역"));
        SectionRequest 신규_구간 = new SectionRequest(정자역_id, 광교역_id, 3);

        // when
        ExtractableResponse<Response> 신규_구간이_등록된_노선 = 노선에_신규_구간을_등록(신분당선, 신규_구간);

        // then
        노선에_신규_구간이_정상_등록된다(신규_구간이_등록된_노선);
    }

    /**
     * Given 기존 역 사이 길이보다 크거나 같은 새로운 역을 등록하고
     * When 새로운 구간을 등록하면
     * Then 지하철 노선에 구간이 등록되지 않는다.
     */
    @Test
    void 기존_역_사이_길이보다_크거나_같은_구간은_등록할_수_없다() {
        // given
        Long 판교역_id = id_추출(지하철역_생성("판교역"));
        SectionRequest 신규_구간 = new SectionRequest(판교역_id, 정자역_id, 10);

        // when
        ExtractableResponse<Response> 신규_구간이_등록되지_않음 = 노선에_신규_구간을_등록(신분당선, 신규_구간);

        // then
        노선에_신규_구간이_등록되지_않는다(신규_구간이_등록되지_않음);
    }

    public static class SectionAcceptanceTemplate {
        public static void 노선에_신규_구간이_정상_등록된다(ExtractableResponse<Response> 신규_구간이_등록된_노선) {
            assertThat(신규_구간이_등록된_노선.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        }

        public static ExtractableResponse<Response> 노선에_신규_구간을_등록(LineResponse 신분당선, SectionRequest 신규_구간) {
            return RestAssured
                    .given().log().all()
                    .body(신규_구간)
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .when().post("lines/" + 신분당선.getId() + "/sections")
                    .then().log().all()
                    .extract();
        }

        public static void 신규_구간과_분리된_구간_길이의_합이_원래_구간의_길이와_같다(ExtractableResponse<Response> 신규_구간이_등록된_노선, int totalDistance) {
            List<Integer> 구간들 = 신규_구간이_등록된_노선.jsonPath().getList("sections.distance");
            int 구간_길이의_합 = 구간들.get(0) + 구간들.get(1);
            assertThat(구간_길이의_합).isEqualTo(totalDistance);
        }

        public static void 노선에_신규_구간이_등록되지_않는다(ExtractableResponse<Response> 신규_구간이_등록되지_않음) {
            assertThat(신규_구간이_등록되지_않음.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        }
    }
}
