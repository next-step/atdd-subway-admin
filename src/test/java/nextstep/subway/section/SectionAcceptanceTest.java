package nextstep.subway.section;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import io.restassured.specification.ResponseSpecification;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.dto.SectionRequest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static nextstep.subway.AcceptanceFixture.*;
import static nextstep.subway.line.LineAcceptanceFixture.지하철_노선_생성_요청;
import static nextstep.subway.line.LineAcceptanceFixture.지하철_노선_조회_요청;
import static nextstep.subway.section.SectionAcceptanceFixture.노선_구간_제거_요청;
import static nextstep.subway.section.SectionAcceptanceFixture.지하철_구간_생성_요청;
import static nextstep.subway.station.StationAcceptanceFixture.지하철역_생성_요청;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

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
     * Given 기존 지하철 노선에 상행 지하철을 기준으로 구간을 생성 요청하고,
     * When 해당 노선에 구간을 요청하면
     * Then 구간을 추가할 수 있다.
     */
    @DisplayName("역 사이에 새로운 역(상행연결)을 등록할 경우")
    @Test
    void connected_up_station() {
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

    /**
     * Given 기존 지하철 노선에 하행 지하철을 기준으로 구간을 생성 요청하고,
     * When 해당 노선에 구간을 요청하면
     * Then 구간을 추가할 수 있다.
     */
    @DisplayName("역 사이에 새로운 역(하행연결)을 등록할 경우")
    @Test
    void connected_down_station() {
        // given
        ExtractableResponse<Response> 사당역 = 지하철역_생성_요청("사당역");
        SectionRequest 사당_강남_구간_요청 = new SectionRequest(식별_아이디_조회(사당역), 식별_아이디_조회(강남역), 5);

        // when
        ExtractableResponse<Response> 신림_사당_구간_응답 = 지하철_구간_생성_요청(
                식별_아이디_조회(신림_강남_노선), 사당_강남_구간_요청
        );

        // then
        요청_성공(신림_사당_구간_응답);
    }

    /**
     * Given 기존 지하철 노선에 상행 방면으로 구간을 생성하고,
     * When 해당 노선에 구간을 요청하면
     * Then 구간을 추가할 수 있다.
     */
    @DisplayName("새로운 역을 상행 종점으로 등록할 경우")
    @Test
    void new_station_up_station_add() {
        // given
        ExtractableResponse<Response> 신도림역 = 지하철역_생성_요청("신도림역");
        SectionRequest 신도림_신림_구간_요청 = new SectionRequest(식별_아이디_조회(신도림역), 식별_아이디_조회(신림역), 5);

        // when
        ExtractableResponse<Response> 신도림_신림_구간_응답 = 지하철_구간_생성_요청(
                식별_아이디_조회(신림_강남_노선), 신도림_신림_구간_요청
        );

        // then
        요청_성공(신도림_신림_구간_응답);
    }

    /**
     * Given 기존 지하철 노선에 하행 방면으로 구간을 생성하고,
     * When 해당 노선에 구간을 요청하면
     * Then 구간을 추가할 수 있다.
     */
    @DisplayName("새로운 역을 하행 종점으로 등록할 경우")
    @Test
    void new_station_down_station_add() {
        // given
        ExtractableResponse<Response> 잠실역 = 지하철역_생성_요청("잠실역");
        ExtractableResponse<Response> 왕십리 = 지하철역_생성_요청("왕십리");
        SectionRequest 잠실_강남_구간_요청 = new SectionRequest(식별_아이디_조회(강남역), 식별_아이디_조회(잠실역), 5);
        SectionRequest 잠실_왕십리_구간_요청 = new SectionRequest(식별_아이디_조회(잠실역), 식별_아이디_조회(왕십리), 5);

        // when
        ExtractableResponse<Response> 신림_잠실_구간_응답 = 지하철_구간_생성_요청(
                식별_아이디_조회(신림_강남_노선), 잠실_강남_구간_요청
        );
        ExtractableResponse<Response> 신림_왕십리_구간_응답 = 지하철_구간_생성_요청(
                식별_아이디_조회(신림_강남_노선), 잠실_왕십리_구간_요청
        );

        // then
        Assertions.assertAll(
                () -> 요청_성공(신림_잠실_구간_응답),
                () -> 요청_성공(신림_왕십리_구간_응답)
        );

    }

    /**
     * Given 기존 노선에 새로운 지하철 구간을 요청하고,
     * When 해당 지하철역 노선을 확인하면
     * Then 지하철 노선 구간을 확인할 수 있다.
     */
    @DisplayName("노선을 생성한 후 구간 설정 후 노선의 리스트 확인")
    @Test
    void line_add_section() {
        // given
        ExtractableResponse<Response> 사당역 = 지하철역_생성_요청("사당역");
        SectionRequest 신림_사당_구간_요청 = new SectionRequest(식별_아이디_조회(신림역), 식별_아이디_조회(사당역), 5);
        지하철_구간_생성_요청(식별_아이디_조회(신림_강남_노선), 신림_사당_구간_요청);

        // when
        ExtractableResponse<Response> 신림_사당_강남_노선_응답 = 지하철_노선_조회_요청(식별_아이디_조회(신림_강남_노선));
        List<String> 지하철_노선_구간 = 제이슨_경로_얻기(신림_사당_강남_노선_응답).getList("stations.name");

        // then
        assertThat(지하철_노선_구간).containsExactly("신림역", "사당역", "강남역");
    }

    /**
     * Given 기존 지하철 노선의 길이와 동일한 구간 생성하고
     * When 해당 노선에 생성한 구간을 요청하면
     * Then 구간을 추가할 수 없다.
     */
    @DisplayName("역 사이에 새로운 역을 등록할 경우 기존 역 사이 길이보다 크거나 같으면 등록을 할 수 없음")
    @Test
    void between_station_distance_over() {
        // given
        ExtractableResponse<Response> 사당역 = 지하철역_생성_요청("사당역");
        SectionRequest 사당_강남_구간_요청 = new SectionRequest(식별_아이디_조회(사당역), 식별_아이디_조회(강남역), 10);

        // when
        ExtractableResponse<Response> 신림_사당_구간_응답 = 지하철_구간_생성_요청(
                식별_아이디_조회(신림_강남_노선), 사당_강남_구간_요청
        );

        // then
        요청_실패(신림_사당_구간_응답);
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

    @DisplayName("2개의 구간이 있는 노선에서 종점 상행역 제거")
    @Test
    void delete_first_station_of_line() {

        // given
        ExtractableResponse<Response> 사당역 = 지하철역_생성_요청("사당역");
        SectionRequest 신림_사당_구간_요청 = new SectionRequest(식별_아이디_조회(신림역), 식별_아이디_조회(사당역), 5);
        지하철_구간_생성_요청(식별_아이디_조회(신림_강남_노선), 신림_사당_구간_요청);

        // when
        ExtractableResponse<Response> 신림역_제거_응답 = 노선_구간_제거_요청(
                식별_아이디_조회(신림_강남_노선), 식별_아이디_조회(신림역)
        );
        ExtractableResponse<Response> 사당_강남_노선_응답 = 지하철_노선_조회_요청(식별_아이디_조회(신림_강남_노선));
        List<String> 지하철_노선_구간 = 제이슨_경로_얻기(사당_강남_노선_응답).getList("stations.name");

        // then
        Assertions.assertAll(
                () -> 삭제_요청_성공(신림역_제거_응답),
                () -> assertThat(지하철_노선_구간).containsExactly("사당역", "강남역")
        );
    }

    @DisplayName("2개의 구간이 있는 노선에서 종점 하행역 제거")
    @Test
    void delete_last_station_of_line() {

        // given
        ExtractableResponse<Response> 사당역 = 지하철역_생성_요청("사당역");
        SectionRequest 신림_사당_구간_요청 = new SectionRequest(식별_아이디_조회(신림역), 식별_아이디_조회(사당역), 5);
        지하철_구간_생성_요청(식별_아이디_조회(신림_강남_노선), 신림_사당_구간_요청);

        // when
        ExtractableResponse<Response> 강남역_제거_응답 = 노선_구간_제거_요청(
                식별_아이디_조회(신림_강남_노선), 식별_아이디_조회(강남역)
        );
        ExtractableResponse<Response> 사당_강남_노선_응답 = 지하철_노선_조회_요청(식별_아이디_조회(신림_강남_노선));
        List<String> 지하철_노선_구간 = 제이슨_경로_얻기(사당_강남_노선_응답).getList("stations.name");

        // then
        Assertions.assertAll(
                () -> 삭제_요청_성공(강남역_제거_응답),
                () -> assertThat(지하철_노선_구간).containsExactly("신림역", "사당역")
        );
    }



    @DisplayName("2개의 구간이 있는 노선에서 가운데 역을 제거")
    @Test
    void delete_middle_station_of_line() {

        // given
        ExtractableResponse<Response> 사당역 = 지하철역_생성_요청("사당역");
        SectionRequest 신림_사당_구간_요청 = new SectionRequest(식별_아이디_조회(신림역), 식별_아이디_조회(사당역), 5);
        지하철_구간_생성_요청(식별_아이디_조회(신림_강남_노선), 신림_사당_구간_요청);
        ExtractableResponse<Response> 신림_사당_강남_노선_응답 = 지하철_노선_조회_요청(식별_아이디_조회(신림_강남_노선));

        String uri = "/lines/" + 식별_아이디_조회(신림_사당_강남_노선_응답) + "/sections";
        // when
        ExtractableResponse<Response> 사당역_제거_응답 = RestAssured.given().log().all()
                .param("stationId", 식별_아이디_조회(사당역))
                .when()
                .delete(uri)
                .then().log().all()
                .extract();

        fail("인수 테스트 작성중");
    }
}
