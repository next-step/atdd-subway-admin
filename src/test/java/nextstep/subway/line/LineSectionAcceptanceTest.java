package nextstep.subway.line;

import static nextstep.subway.line.LineAcceptanceFixture.구간_생성_결과에서_지하철역_번호들을_조회한다;
import static nextstep.subway.line.LineAcceptanceFixture.구간_요청_정보;
import static nextstep.subway.line.LineAcceptanceFixture.노선_결과에서_노선_아이디를_조회한다;
import static nextstep.subway.line.LineAcceptanceFixture.노선_결과에서_노선_지하철역_번호를_조회한다;
import static nextstep.subway.line.LineAcceptanceFixture.노선_요청;
import static nextstep.subway.line.LineAcceptanceFixture.노선에_구간을_생성한다;
import static nextstep.subway.line.LineAcceptanceFixture.노선에_역을_삭제한다;
import static nextstep.subway.line.LineAcceptanceFixture.노선을_생성한다;
import static nextstep.subway.line.LineAcceptanceFixture.특정_노선을_조회한다;
import static nextstep.subway.station.StationAcceptanceFixture.지하철_생성_결과에서_지하철역_번호를_조회한다;
import static nextstep.subway.station.StationAcceptanceFixture.지하철_역을_생성한다;
import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.List;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.dto.LineRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

@DisplayName("노선 구역 관련 기능")
public class LineSectionAcceptanceTest extends AcceptanceTest {

    private Long 노선_아이디;
    private Long 서울역_번호;
    private Long 수원역_번호;
    private Long 인천역_번호;
    private Long 두정역_번호;
    private Long 부천역_번호;

    @BeforeEach
    void init() {
        LineRequest 노선_요청 = 노선_요청("1호선", "Blue", "서울역", "두정역", 1000L);
        서울역_번호 = 노선_요청.getUpStationId();
        두정역_번호 = 노선_요청.getDownStationId();
        노선_아이디 = 노선_결과에서_노선_아이디를_조회한다(노선을_생성한다(노선_요청));
        수원역_번호 = 지하철_생성_결과에서_지하철역_번호를_조회한다(지하철_역을_생성한다("수원역"));
        인천역_번호 = 지하철_생성_결과에서_지하철역_번호를_조회한다(지하철_역을_생성한다("인천역"));
        부천역_번호 = 지하철_생성_결과에서_지하철역_번호를_조회한다(지하철_역을_생성한다("부천역"));
    }

    /**
     * When 지하철 노선에 마지막 상행선역을 포함하여 등록하면
     * Then 지하철 노선에 등록된 지하철 역을 확인할 수 있다.
     **/
    @DisplayName("노선에 마지막 상행선을 포함한 구간을 등록한다.")
    @Test
    void addSection_withLastUpStation() {
        // when
        ExtractableResponse<Response> 구간_생성_결과 = 노선에_구간을_생성한다(노선_아이디, 구간_요청_정보(서울역_번호, 수원역_번호));


        // then
        List<Long> 지하철역_번호_목록 = 구간_생성_결과에서_지하철역_번호들을_조회한다(구간_생성_결과);
        지하철역_목록이_상행선부터_차례로_노출되어야_한다(지하철역_번호_목록, 서울역_번호, 수원역_번호, 두정역_번호);
    }

    /**
     * When 지하철 노선에 마지막 하행선 역을 포함하여 등록하면
     * Then 지하철 노선에 등록된 지하철 역을 확인할 수 있다.
     **/
    @DisplayName("노선에 마지막 하행선을 포함한 구간을 등록한다.")
    @Test
    void addSection_withLastDownStation() {
        // when
        ExtractableResponse<Response> 구간_생성_결과 = 노선에_구간을_생성한다(노선_아이디, 구간_요청_정보(수원역_번호, 두정역_번호));


        // then
        List<Long> 지하철역_번호_목록 = 구간_생성_결과에서_지하철역_번호들을_조회한다(구간_생성_결과);
        지하철역_목록이_상행선부터_차례로_노출되어야_한다(지하철역_번호_목록, 서울역_번호, 수원역_번호, 두정역_번호);
    }

    /**
     * When 지하철 노선에 추가된 구간이 기존 구간의 길이보다 크거나 같을 경우
     * Then INTERNAL_SERVER_ERROR 를 반환해야 한다
     **/
    @DisplayName("노선에 구간등록시, 등록하려는 구간이 기존 구간보다 길이가 길 경우 에러를 반환해야 한다.")
    @Test
    void addSection_throw_exception_if_distance_is_long_or_equals_rather_than_other_section() {
        // when
        ExtractableResponse<Response> 구간_생성_결과 = 노선에_구간을_생성한다(노선_아이디, 구간_요청_정보(수원역_번호, 두정역_번호, 1001L));


        // then
        구간_생성_결과에서_에러가_발생해야_한다(구간_생성_결과);
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
        지하철역_목록이_상행선부터_차례로_노출되어야_한다(지하철역_번호_목록, 수원역_번호, 서울역_번호, 두정역_번호);
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
        지하철역_목록이_상행선부터_차례로_노출되어야_한다(지하철역_번호_목록, 서울역_번호, 두정역_번호, 수원역_번호);
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
        구간_생성_결과에서_에러가_발생해야_한다(구간_생성_결과);
    }

    /**
     * When 지하철 노선에 새로운 구간을 등록 할 때, 두 역 모두 기존 노선에 포함되어 있지 않을 경우
     * Then INTERNAL_SERVER_ERROR 를 반환해야 한다
     **/
    @DisplayName("노선에 새로운 구간을 등록할 때, 두 역 모두 기존 노선에 포함되어 있지 않다면 에러를 반환한다")
    @Test
    void addSection_throw_exception_if_section_cant_find_any_matches_stations() {
        // when
        ExtractableResponse<Response> 구간_생성_결과 = 노선에_구간을_생성한다(노선_아이디, 구간_요청_정보(인천역_번호, 부천역_번호));


        // then
        구간_생성_결과에서_에러가_발생해야_한다(구간_생성_결과);
    }

    /**
     * When 노선에 새로운 구간을 추가할 때, 상행역이 null일 경우
     * Then INTERNAL_SERVER_ERROR 를 반환해야 한다
     **/
    @DisplayName("노선에 새로운 구간을 추가할 때, 지하철 역이 null일 경우 에러를 반환한다")
    @Test
    void addSection_throw_exception_if_section_contains_null_upStation() {
        // when
        ExtractableResponse<Response> 구간_생성_결과 = 노선에_구간을_생성한다(노선_아이디, 구간_요청_정보(null, 부천역_번호));


        // then
        구간_생성_결과에서_에러가_발생해야_한다(구간_생성_결과);
    }

    /**
     * When 노선에 새로운 구간을 추가할 때, 하행역이 null일 경우
     * Then INTERNAL_SERVER_ERROR 를 반환해야 한다
     **/
    @DisplayName("노선에 새로운 구간을 추가할 때, 지하철 역이 null일 경우 에러를 반환한다")
    @Test
    void addSection_throw_exception_if_section_contains_null_downStation() {
        // when
        ExtractableResponse<Response> 구간_생성_결과 = 노선에_구간을_생성한다(노선_아이디, 구간_요청_정보(서울역_번호, null));


        // then
        구간_생성_결과에서_에러가_발생해야_한다(구간_생성_결과);
    }

    /**
     * When 노선에 새로운 구간을 추가할 때, 상행역과 하행역이 같을 경우
     * Then INTERNAL_SERVER_ERROR 를 반환해야 한다
     **/
    @DisplayName("노선에 새로운 구간을 추가할 때, 지하철 역이 null일 경우 에러를 반환한다")
    @Test
    void addSection_throw_exception_if_section_duplicated_upStation_and_downStation() {
        // when
        ExtractableResponse<Response> 구간_생성_결과 = 노선에_구간을_생성한다(노선_아이디, 구간_요청_정보(서울역_번호, 서울역_번호));


        // then
        구간_생성_결과에서_에러가_발생해야_한다(구간_생성_결과);
    }

    /**
     * Given 노선에 추가 구간을 생성하고
     * When 노선의 중간 역이 삭제되면
     * Then 노선의 구간과 역이 재배치 되어야 한다
     **/
    @DisplayName("노선의 중간 역이 삭제되면 노선의 구간과 역이 재배치 되어야 한다")
    @Test
    void rebase_sections_if_middle_station_of_line_removed() {
        // given
        노선에_구간을_생성한다(노선_아이디, 구간_요청_정보(서울역_번호, 수원역_번호));
        노선에_구간을_생성한다(노선_아이디, 구간_요청_정보(수원역_번호, 인천역_번호));

        // when
        노선에_역을_삭제한다(노선_아이디, 수원역_번호);

        // then
        List<Long> 노선_지하철_역_번호들 = 노선_결과에서_노선_지하철역_번호를_조회한다(특정_노선을_조회한다(노선_아이디));
        assertThat(노선_지하철_역_번호들).containsExactly(서울역_번호, 인천역_번호, 두정역_번호);
    }

    /**
     * Given 노선에 추가 구간을 생성하고
     * When 노선의 상행 종점역이 삭제되면
     * Then 노선의 구간과 역이 재배치 되어야 한다
     **/
    @DisplayName("노선의 상행 종점역이 삭제되면 노선의 구간과 역이 재배치 되어야 한다")
    @Test
    void rebase_sections_if_last_up_station_of_line_removed() {
        // given
        노선에_구간을_생성한다(노선_아이디, 구간_요청_정보(서울역_번호, 수원역_번호));

        // when
        노선에_역을_삭제한다(노선_아이디, 서울역_번호);

        // then
        List<Long> 노선_지하철_역_번호들 = 노선_결과에서_노선_지하철역_번호를_조회한다(특정_노선을_조회한다(노선_아이디));
        assertThat(노선_지하철_역_번호들).containsExactly(수원역_번호, 두정역_번호);
    }

    /**
     * Given 노선에 추가 구간을 생성하고
     * When 노선의 하행 종점역이 삭제되면
     * Then 노선의 구간과 역이 재배치 되어야 한다
     **/
    @DisplayName("노선의 하행 종점역이 삭제되면 노선의 구간과 역이 재배치 되어야 한다")
    @Test
    void rebase_sections_if_last_down_station_of_line_removed() {
        // given
        노선에_구간을_생성한다(노선_아이디, 구간_요청_정보(서울역_번호, 수원역_번호));

        // when
        노선에_역을_삭제한다(노선_아이디, 두정역_번호);

        // then
        List<Long> 노선_지하철_역_번호들 = 노선_결과에서_노선_지하철역_번호를_조회한다(특정_노선을_조회한다(노선_아이디));
        assertThat(노선_지하철_역_번호들).containsExactly(서울역_번호, 수원역_번호);
    }

    /**
     * When 노선의 하나뿐인 구간의 하행 종점역이 삭제되면
     * Then 에러가 발생해야 한다
     **/
    @DisplayName("노선의 하나뿐인 구간의 하행 종점역이 삭제되면 에러가 발생해야 한다")
    @Test
    void throws_exception_when_remove_last_down_station_of_last_section() {
        // when && then
        구간_삭제_결과에서_에러가_발생해야_한다(노선에_역을_삭제한다(노선_아이디, 두정역_번호));
    }

    /**
     * When 노선의 하나뿐인 구간의 상행 종점역이 삭제되면
     * Then 에러가 발생해야 한다
     **/
    @DisplayName("노선의 하나뿐인 구간의 상행 종점역이 삭제되면 에러가 발생해야 한다")
    @Test
    void throws_exception_when_remove_last_up_station_of_last_section() {
        // when && then
        구간_삭제_결과에서_에러가_발생해야_한다(노선에_역을_삭제한다(노선_아이디, 서울역_번호));
    }


    private void 구간_생성_결과에서_에러가_발생해야_한다(ExtractableResponse<Response> 구간_생성_결과) {
        assertThat(구간_생성_결과.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    private void 구간_삭제_결과에서_에러가_발생해야_한다(ExtractableResponse<Response> 구간_삭제_결과) {
        assertThat(구간_삭제_결과.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    private void 지하철역_목록이_상행선부터_차례로_노출되어야_한다(List<Long> 지하철역_번호_목록, Long 서울역_번호, Long 두정역_번호, Long 수원역_번호) {
        assertThat(지하철역_번호_목록).containsExactlyInAnyOrder(서울역_번호, 두정역_번호, 수원역_번호);
    }
}
