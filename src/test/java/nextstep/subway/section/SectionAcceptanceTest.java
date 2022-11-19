package nextstep.subway.section;

import static nextstep.subway.line.LineAcceptanceRestAssured.지하철노선_생성;
import static nextstep.subway.line.LineAcceptanceRestAssured.지하철노선_조회;
import static nextstep.subway.section.SectionAcceptanceRestAssured.지하철구간_제거;
import static nextstep.subway.section.SectionAcceptanceRestAssured.지하철구간_추가;
import static nextstep.subway.station.StationAcceptanceRestAssured.지하철역_생성;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import io.restassured.path.json.JsonPath;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.common.BaseAcceptanceTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

@DisplayName("구간 관련 기능")
public class SectionAcceptanceTest extends BaseAcceptanceTest {

    private Long 잠실역_id;
    private Long 가락시장역_id;
    private Long 노선_id;

    @BeforeEach
    public void setUp() {
        super.setUp();

        // given
        잠실역_id = 지하철역_생성("잠실역").jsonPath().getLong("id");
        가락시장역_id = 지하철역_생성("가락시장역").jsonPath().getLong("id");
        노선_id = 지하철노선_생성("8호선", "분홍색", 잠실역_id, 가락시장역_id, 10).jsonPath().getLong("id");
    }


    /**
     * Given 2개의 지하철역을 생성하고
     * Given 1개의 지하철 노선을 생성하고
     * Given 1개의 지하철역을 생성하고
     * When 지하철 구간을 등록하면
     * Then 지하철 노선을 조회 시 추가된 구간을 확인할 수 있다.
     */
    @Test
    @DisplayName("역 사이에 새로운 역을 등록할 경우")
    public void 새로운_역_등록() {
        // given
        Long 석촌역_id = 지하철역_생성("석촌역").jsonPath().getLong("id");

        // when
        ExtractableResponse<Response> 지하철구간_추가_결과 = 지하철구간_추가(노선_id, 잠실역_id, 석촌역_id, 3);
        assertThat(지하철구간_추가_결과.statusCode()).isEqualTo(HttpStatus.CREATED.value());

        // then
        ExtractableResponse<Response> 지하철노선_조회_결과 = 지하철노선_조회(노선_id);
        JsonPath jsonPath = 지하철노선_조회_결과.jsonPath();
        assertAll(
                () -> assertThat(지하철노선_조회_결과.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(jsonPath.getList("stations")).hasSize(3),
                () -> assertThat(jsonPath.getList("stations.name")).containsExactly("잠실역", "석촌역", "가락시장역")
        );
    }

    /**
     * Given 2개의 지하철역을 생성하고
     * Given 1개의 지하철 노선을 생성하고
     * Given 1개의 지하철역을 생성하고
     * When 새로운 역을 상행 종점으로 구간을 등록하면
     * Then 지하철 노선을 조회 시 추가된 구간을 확인할 수 있다.
     */
    @Test
    @DisplayName("새로운 역을 상행 종점으로 등록할 경우")
    public void 새로운_역_등록_상행종점() {
        // given
        Long 강변역_id = 지하철역_생성("강변역").jsonPath().getLong("id");

        // when
        ExtractableResponse<Response> 지하철구간_추가_결과 = 지하철구간_추가(노선_id, 강변역_id, 잠실역_id, 3);
        assertThat(지하철구간_추가_결과.statusCode()).isEqualTo(HttpStatus.CREATED.value());

        // then
        ExtractableResponse<Response> 지하철노선_조회_결과 = 지하철노선_조회(노선_id);
        JsonPath jsonPath = 지하철노선_조회_결과.jsonPath();
        assertAll(
                () -> assertThat(지하철노선_조회_결과.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(jsonPath.getList("stations")).hasSize(3),
                () -> assertThat(jsonPath.getList("stations.name")).containsExactly("강변역", "잠실역", "가락시장역")
        );
    }

    /**
     * Given 2개의 지하철역을 생성하고
     * Given 1개의 지하철 노선을 생성하고
     * Given 1개의 지하철역을 생성하고
     * When 새로운 역을 하행 종점으로 구간을 등록하면
     * Then 지하철 노선을 조회 시 추가된 구간을 확인할 수 있다.
     */
    @Test
    @DisplayName("새로운 역을 하행 종점으로 등록할 경우")
    public void 새로운_역_등록_하행종점() {
        // given
        Long 문정역_id = 지하철역_생성("문정역").jsonPath().getLong("id");

        // when
        ExtractableResponse<Response> 지하철구간_추가_결과 = 지하철구간_추가(노선_id, 가락시장역_id, 문정역_id, 3);
        assertThat(지하철구간_추가_결과.statusCode()).isEqualTo(HttpStatus.CREATED.value());

        // then
        ExtractableResponse<Response> 지하철노선_조회_결과 = 지하철노선_조회(노선_id);
        JsonPath jsonPath = 지하철노선_조회_결과.jsonPath();
        assertAll(
                () -> assertThat(지하철노선_조회_결과.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(jsonPath.getList("stations")).hasSize(3),
                () -> assertThat(jsonPath.getList("stations.name")).containsExactly("잠실역", "가락시장역", "문정역")
        );
    }

    /**
     * Given 2개의 지하철역을 생성하고
     * Given 1개의 지하철 노선을 생성하고
     * Given 1개의 지하철역을 생성하고
     * When 새로운 역의 구간 길이를 기존 구간 길이보다 크거나 같게 등록하면
     * Then 지하철 구간 등록을 할 수 없다.
     */
    @Test
    @DisplayName("역 사이에 새로운 역을 등록할 경우 기존 역 사이 길이보다 크거나 같으면 등록할 수 없다")
    public void 새로운_역_등록_실패_길이() {
        // given
        Long 석촌역_id = 지하철역_생성("석촌역").jsonPath().getLong("id");

        // when
        ExtractableResponse<Response> 지하철구간_추가_결과 = 지하철구간_추가(노선_id, 잠실역_id, 석촌역_id, 10);

        // then
        assertThat(지하철구간_추가_결과.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    /**
     * Given 2개의 지하철역을 생성하고
     * Given 1개의 지하철 노선을 생성하고
     * When 이미 등록되어 있는 상행역, 하행역으로 등록한다면
     * Then 지하철 구간 등록을 할 수 없다.
     */
    @Test
    @DisplayName("상행역과 하행역이 이미 노선에 모두 등록되어 있다면 등록할 수 없다")
    public void 새로운_역_등록_실패_이미_존재() {
        // when
        ExtractableResponse<Response> 지하철구간_추가_결과 = 지하철구간_추가(노선_id, 잠실역_id, 가락시장역_id, 3);

        // then
        assertThat(지하철구간_추가_결과.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    /**
     * Given 2개의 지하철역을 생성하고
     * Given 1개의 지하철 노선을 생성하고
     * When 이미 등록되어 있는 상행역, 하행역으로 등록한다면
     * Then 지하철 구간 등록을 할 수 없다.
     */
    @Test
    @DisplayName("노선에 새롭게 구간등록 하려는 상행역과 하행역 중 하나라도 포함되어 있지 않으면 등록할 수 없다")
    public void 새로운_역_등록_실패_포함_안됨() {
        // given
        Long 석촌역_id = 지하철역_생성("석촌역").jsonPath().getLong("id");
        Long 문정역_id = 지하철역_생성("문정역").jsonPath().getLong("id");

        // when
        ExtractableResponse<Response> 지하철구간_추가_결과 = 지하철구간_추가(노선_id, 석촌역_id, 문정역_id, 3);

        // then
        assertThat(지하철구간_추가_결과.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }


    /**
     * Given 2개의 지하철역을 생성하고
     * Given 1개의 지하철 노선을 생성하고
     * Given 1개의 지하철역을 생성하고
     * Given 1개의 지하철 구간을 추가하고
     * When 종점을 가진 구간을 제거할 경우
     * Then 지하철 노선을 조회 시 역이 제외된 것을 확인할 수 있다
     * */
    @Test
    @DisplayName("노선의 종점 구간을 제거하는 경우")
    void 구간삭제_종점() {
        // given
        Long 석촌역_id = 지하철역_생성("석촌역").jsonPath().getLong("id");
        지하철구간_추가(노선_id, 가락시장역_id, 석촌역_id, 3);

        // when
        ExtractableResponse<Response> 지하철구간_제거_결과 = 지하철구간_제거(노선_id, 석촌역_id);
        assertThat(지하철구간_제거_결과.statusCode()).isEqualTo(HttpStatus.OK.value());

        // then
        ExtractableResponse<Response> 지하철노선_조회_결과 = 지하철노선_조회(노선_id);
        JsonPath jsonPath = 지하철노선_조회_결과.jsonPath();
        assertAll(
                () -> assertThat(지하철노선_조회_결과.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(jsonPath.getList("stations")).hasSize(2),
                () -> assertThat(jsonPath.getList("stations.name")).containsExactly("잠실역", "가락시장역")
        );
    }

    /**
     * Given 2개의 지하철역을 생성하고
     * Given 1개의 지하철 노선을 생성하고
     * Given 1개의 지하철역을 생성하고
     * Given 1개의 지하철 구간을 추가하고
     * When 중간역을 가진 구간을 제거할 경우
     * Then 지하철 노선을 조회 시 역이 제외된 것을 확인할 수 있다
     * */
    @Test
    @DisplayName("노선의 중간 구간을 제거하는 경우")
    void 구간삭제_중간() {
        // given
        Long 석촌역_id = 지하철역_생성("석촌역").jsonPath().getLong("id");
        지하철구간_추가(노선_id, 잠실역_id, 석촌역_id, 3);

        // when
        ExtractableResponse<Response> 지하철구간_제거_결과 = 지하철구간_제거(노선_id, 석촌역_id);
        assertThat(지하철구간_제거_결과.statusCode()).isEqualTo(HttpStatus.OK.value());

        // then
        ExtractableResponse<Response> 지하철노선_조회_결과 = 지하철노선_조회(노선_id);
        JsonPath jsonPath = 지하철노선_조회_결과.jsonPath();
        assertAll(
                () -> assertThat(지하철노선_조회_결과.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(jsonPath.getList("stations")).hasSize(2),
                () -> assertThat(jsonPath.getList("stations.name")).containsExactly("잠실역", "가락시장역")
        );
    }

    /**
     * Given 2개의 지하철역을 생성하고
     * Given 1개의 지하철 노선을 생성하고
     * When 남은 마지막 구간을 제거하려 할 때
     * Then 구간을 제거할 수 없다
     * */
    @Test
    @DisplayName("노선의 남은 마지막 구간을 제거하는 경우, 삭제할 수 없다")
    void 구간삭제_마지막_구간() {
        // when
        ExtractableResponse<Response> 지하철구간_제거_결과 = 지하철구간_제거(노선_id, 잠실역_id);

        // then
        assertThat(지하철구간_제거_결과.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    /**
     * Given 2개의 지하철역을 생성하고
     * Given 1개의 지하철 노선을 생성하고
     * When 존재하지 않는 구간을 삭제하려 하면
     * Then 구간을 제거할 수 없다
     * */
    @Test
    @DisplayName("노선에 존재하지 않는 구간을 제거하는 경우, 삭제할 수 없다")
    void 구간삭제_존재하지_않는_구간_구간() {
        // when
        ExtractableResponse<Response> 지하철구간_제거_결과 = 지하철구간_제거(노선_id, -1L);

        // then
        assertThat(지하철구간_제거_결과.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }
}
