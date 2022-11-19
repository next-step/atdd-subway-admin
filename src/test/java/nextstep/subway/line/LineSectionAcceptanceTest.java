package nextstep.subway.line;

import static nextstep.subway.line.LineAcceptanceFixture.노선_결과에서_노선_아이디를_조회한다;
import static nextstep.subway.line.LineAcceptanceFixture.노선_요청;
import static nextstep.subway.line.LineAcceptanceFixture.노선을_생성한다;
import static nextstep.subway.station.StationAcceptanceFixture.지하철_생성_결과에서_지하철역_번호를_조회한다;
import static nextstep.subway.station.StationAcceptanceFixture.지하철_역을_생성한다;
import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.List;
import java.util.stream.Collectors;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.dto.LineRequest;
import nextstep.subway.dto.SectionRequest;
import nextstep.subway.dto.StationResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

@DisplayName("노선 구역 관련 기능")
public class LineSectionAcceptanceTest extends AcceptanceTest {

    private Long 노선_아이디;
    private Long 서울역_번호;
    private Long 수원역_번호;
    private Long 인천역_번호;
    private Long 두정역_번호;

    @BeforeEach
    void init() {
        LineRequest 노선_요청 = 노선_요청("1호선", "Blue", "서울역", "두정역", 1000L);
        서울역_번호 = 노선_요청.getUpStationId();
        두정역_번호 = 노선_요청.getDownStationId();
        노선_아이디 = 노선_결과에서_노선_아이디를_조회한다(노선을_생성한다(노선_요청));
        수원역_번호 = 지하철_생성_결과에서_지하철역_번호를_조회한다(지하철_역을_생성한다("수원역"));
        인천역_번호 = 지하철_생성_결과에서_지하철역_번호를_조회한다(지하철_역을_생성한다("인천역"));
    }

    /**
     * When 지하철 노선에 마지막 상행선역을 포함하여 등록하면
     * Then 지하철 노선에 등록된 지하철 역을 확인할 수 있다.
     **/
    @DisplayName("노선에 마지막 상행선을 포함한 구간을 등록한다.")
    @Test
    void addSection_withLastUpStation() {
        // when
        // 지하철_노선_구간_등록_요청
        ExtractableResponse<Response> 구간_생성_결과 = 노선에_구간을_생성한다(노선_아이디, 구간_요청_정보(서울역_번호, 수원역_번호));


        // then
        // 지하철_노선에_등록된_지하철역을_확인할_수_있다
        List<Long> 지하철역_번호_목록 = 구간_생성_결과에서_지하철역_번호들을_조회한다(구간_생성_결과);
        assertThat(지하철역_번호_목록).containsExactlyInAnyOrder(서울역_번호, 수원역_번호, 두정역_번호);
    }

    /**
     * When 지하철 노선에 마지막 하행선 역을 포함하여 등록하면
     * Then 지하철 노선에 등록된 지하철 역을 확인할 수 있다.
     **/
    @DisplayName("노선에 마지막 하행선을 포함한 구간을 등록한다.")
    @Test
    void addSection_withLastDownStation() {
        // when
        // 지하철_노선_구간_등록_요청
        ExtractableResponse<Response> 구간_생성_결과 = 노선에_구간을_생성한다(노선_아이디, 구간_요청_정보(수원역_번호, 두정역_번호));


        // then
        // 지하철_노선에_등록된_지하철역을_확인할_수_있다
        List<Long> 지하철역_번호_목록 = 구간_생성_결과에서_지하철역_번호들을_조회한다(구간_생성_결과);
        assertThat(지하철역_번호_목록).containsExactlyInAnyOrder(서울역_번호, 수원역_번호, 두정역_번호);
    }

    /**
     * When 지하철 노선에 추가된 구간이 기존 구간의 길이보다 크거나 같을 경우
     * Then INTERNAL_SERVER_ERROR 를 반환해야 한다
     **/
    @DisplayName("노선에 구간등록시, 등록하려는 구간이 기존 구간보다 길이가 길 경우 에러를 반환해야 한다.")
    @Test
    void addSection_throw_exception_if_distance_is_long_or_equals_rather_than_other_section() {
        // when
        ExtractableResponse<Response> 구간_생성_결과 = 노선에_구간을_생성한다(노선_아이디, new SectionRequest(수원역_번호, 두정역_번호, 1001L));


        // then
        // 지하철_노선에_등록된_지하철역을_확인할_수_있다
        assertThat(구간_생성_결과.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    /**
     * When 지하철 노선에 새로운 상행선 구간을 등록하면
     * Then 지하철 노선의 상행선 역이 변경 된다.
     **/
    @DisplayName("노선에 상행선을 새롭게 등록할 수 있어야 한다")
    @Test
    void addSection_newLastUpStation() {
        // when
        ExtractableResponse<Response> 구간_생성_결과 = 노선에_구간을_생성한다(노선_아이디, 구간_요청_정보(수원역_번호, 서울역_번호));


        // then
        List<Long> 지하철역_번호_목록 = 구간_생성_결과에서_지하철역_번호들을_조회한다(구간_생성_결과);
        assertThat(지하철역_번호_목록).containsExactlyInAnyOrder(수원역_번호, 서울역_번호, 두정역_번호);
    }

    /**
     * When 지하철 노선에 새로운 하행선 구간을 등록하면
     * Then 지하철 노선의 하행선 역이 변경 된다.
     **/
    @DisplayName("노선에 마지막 하행선을 새롭게 등록할 수 있어야 한다")
    @Test
    void addSection_newLastDownStation() {
        // when
        ExtractableResponse<Response> 구간_생성_결과 = 노선에_구간을_생성한다(노선_아이디, 구간_요청_정보(두정역_번호, 수원역_번호));


        // then
        List<Long> 지하철역_번호_목록 = 구간_생성_결과에서_지하철역_번호들을_조회한다(구간_생성_결과);
        assertThat(지하철역_번호_목록).containsExactlyInAnyOrder(서울역_번호, 두정역_번호, 수원역_번호);
    }

    /**
     * When 지하철 노선에 새로운 구간을 등록 할 때, 두 역 모두 기존 노선에 포함되어 있다면
     * Then INTERNAL_SERVER_ERROR 를 반환해야 한다
     **/
    @DisplayName("노선에 새로운 구간을 등록할 때, 두 역 모두 기존 노선에 포함되어 있다면 에러를 반환한다")
    @Test
    void addSection_throw_exception_if_section_has_duplicated_stations() {
        // when
        ExtractableResponse<Response> 구간_생성_결과 = 노선에_구간을_생성한다(노선_아이디, 구간_요청_정보(서울역_번호, 두정역_번호));


        // then
        assertThat(구간_생성_결과.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    private SectionRequest 구간_요청_정보(Long 서울역_번호, Long 두정역_번호) {
        return new SectionRequest(서울역_번호, 두정역_번호, 1L);
    }

    private SectionRequest 구간_요청_정보(Long 서울역_번호, Long 두정역_번호, Long 길이) {
        return new SectionRequest(서울역_번호, 두정역_번호, 길이);
    }

    private List<Long> 구간_생성_결과에서_지하철역_번호들을_조회한다(ExtractableResponse<Response> 구간_생성_결과) {
        return 구간_생성_결과.jsonPath().getList("stations", StationResponse.class)
                .stream()
                .map(StationResponse::getId)
                .collect(Collectors.toList());
    }


    public static ExtractableResponse<Response> 노선에_구간을_생성한다(Long 노선_아이디, SectionRequest 구간_요청_정보) {
        return RestAssured.given().log().all()
                .body(구간_요청_정보)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .pathParam("id", 노선_아이디)
                .when().post("lines/{id}/sections")
                .then().log().all()
                .extract();
    }
}
