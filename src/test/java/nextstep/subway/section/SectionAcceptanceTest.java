package nextstep.subway.section;

import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import nextstep.subway.BaseAcceptanceTest;
import nextstep.subway.dto.SectionResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;

@DisplayName("구간 관련 기능")
@Sql({"classpath:section_acceptance_test.sql"})
public class SectionAcceptanceTest extends BaseAcceptanceTest {
    private final long SINNONHYUN_ID = 1L;
    private final String SINNONHYUN_NAME = "신논현역";

    private final long GANGNAM_ID = 2L;
    private final String GANGNAM_NAME = "강남역";

    private final long YANGJAE_ID = 3L;
    private final String YANGJAE_NAME = "양재역";

    private final long JUNGJA_ID = 4L;
    private final String JUNGJA_NAME = "정자역";

    private final long MIGEUM_ID = 5L;
    private final String MIGEUM_NAME = "미금역";

    private final long SINBUNDANGSUN_ID = 1L;
    private final String SINBUNDANGSUN_NAME = "신분당선";

    private final long DISTANCE_FROM_GANGNAME_TO_JUNGJA = 30L;

    /**
     * When 하행종점역이 아닌 기존 역을 상행선으로 새로운 구간을 등록하면
     * Then 새로운 구간이 등록된다
     * Then 구간 목록 조회 시 생성한 구간을 찾을 수 있다
     * Then 기존 구간의 길이가 새로 생성된 구간의 길이만큼 줄어든다
     */
    @DisplayName("하행종점역이 아닌 기존 역을 상행선으로 새 구간을 등록한다")
    @Test
    void registerSectionWithExistingUpStation() {
        // when
        final Long distance = 10L;
        final ExtractableResponse<Response> registerResponse = 구간을_등록한다(GANGNAM_ID, YANGJAE_ID, distance);

        // then
        assertThat(registerResponse.statusCode()).isEqualTo(HttpStatus.OK.value());

        // then
        final List<SectionResponse> sections = 구간_목록을_조회한다().jsonPath().getList(".", SectionResponse.class);
        assertThat(sections).contains(registerResponse.body().as(SectionResponse.class));

        // then
        final SectionResponse modifiedSection = sections.stream()
                .filter(sectionResponse -> sectionResponse.getDownStationName().equals("정자역"))
                .findFirst()
                .get();
        assertThat(modifiedSection.getDistance()).isEqualTo(DISTANCE_FROM_GANGNAME_TO_JUNGJA - 10);
    }

    /**
     * When 상행종점역이 아닌 기존 역을 하행선으로 새로운 구간을 등록하면
     * Then 새로운 구간이 등록된다
     * Then 구간 목록 조회 시 생성한 구간을 찾을 수 있다
     * Then 기존 구간의 길이가 새로 생성된 구간의 길이만큼 줄어든다
     */
    @DisplayName("상행종점역이 아닌 기존 역을 하행선으로 새 구간을 등록한다")
    @Test
    void registerSectionWithExistingDownStation() {
        // when
        final Long distance = 10L;
        final ExtractableResponse<Response> registerResponse = 구간을_등록한다(YANGJAE_ID, JUNGJA_ID, distance);

        // then
        assertThat(registerResponse.statusCode()).isEqualTo(HttpStatus.OK.value());

        // then
        final List<SectionResponse> sections = 구간_목록을_조회한다().jsonPath().getList(".", SectionResponse.class);
        assertThat(sections).contains(registerResponse.body().as(SectionResponse.class));

        // then
        final SectionResponse modifiedSection = sections.stream()
                .filter(sectionResponse -> sectionResponse.getUpStationName().equals("강남역"))
                .findFirst()
                .get();
        assertThat(modifiedSection.getDistance()).isEqualTo(DISTANCE_FROM_GANGNAME_TO_JUNGJA - 10);
    }

    /**
     * When 상행종점역을 하행선으로 새로운 구간을 등록하면
     * Then 새로운 구간이 등록된다
     * Then 구간 목록 조회 시 생성한 구간을 찾을 수 있다
     */
    @DisplayName("상행종점역을 하행선으로 새 구간을 등록한다")
    @Test
    void registerSectionWithFinalUpStationAsDownStation() {
        // when
        final Long distance = 10L;
        final ExtractableResponse<Response> registerResponse = 구간을_등록한다(SINNONHYUN_ID, GANGNAM_ID, distance);

        // then
        assertThat(registerResponse.statusCode()).isEqualTo(HttpStatus.OK.value());

        // then
        final List<SectionResponse> sections = 구간_목록을_조회한다().jsonPath().getList(".", SectionResponse.class);
        assertThat(sections).contains(registerResponse.body().as(SectionResponse.class));
    }

    /**
     * When 하행종점역을 상행선으로 새로운 구간을 등록하면
     * Then 새로운 구간이 등록된다
     * Then 구간 목록 조회 시 생성한 구간을 찾을 수 있다
     */
    @DisplayName("하행종점역을 상행선으로 새 구간을 등록한다")
    @Test
    void registerSectionWithFinalDownStationAsUpStation() {
        // when
        final Long distance = 10L;
        final ExtractableResponse<Response> registerResponse = 구간을_등록한다(JUNGJA_ID, MIGEUM_ID, distance);

        // then
        assertThat(registerResponse.statusCode()).isEqualTo(HttpStatus.OK.value());

        // then
        final List<SectionResponse> sections = 구간_목록을_조회한다().jsonPath().getList(".", SectionResponse.class);
        assertThat(sections).contains(registerResponse.body().as(SectionResponse.class));
    }

    /**
     * When 이미 등록된 구간을 등록하면
     * Then 400 에러가 발생한다
     */
    @DisplayName("이미 등록된 구간을 등록한다")
    @Test
    void registerExistingSection() {
        // when
        final Long distance = 10L;
        final ExtractableResponse<Response> registerResponse = 구간을_등록한다(GANGNAM_ID, JUNGJA_ID, distance);

        // then
        assertThat(registerResponse.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    /**
     * When 노선에 포함되지 않는 역으로만 구성된 구간을 등록하면
     * Then 400 에러가 발생한다
     */
    @DisplayName("노선에 포함되지 않는 역으로만 구성된 구간을 등록한다")
    @Test
    void registerSectionWithAllStationsNotIncludedInLine() {
        // when
        final Long distance = 10L;
        final ExtractableResponse<Response> registerResponse = 구간을_등록한다(SINNONHYUN_ID, YANGJAE_ID, distance);

        // then
        assertThat(registerResponse.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    /**
     * When 기존 구간보다 긴 구간을 등록하면
     * Then 400 에러가 발생한다
     */
    @DisplayName("기존 구간보다 긴 구간을 등록한다")
    @Test
    void registerSectionLongerThanExistingSection() {
        // when
        final Long distance = DISTANCE_FROM_GANGNAME_TO_JUNGJA + 1L;
        final ExtractableResponse<Response> registerResponse = 구간을_등록한다(GANGNAM_ID, JUNGJA_ID, distance);

        // then
        assertThat(registerResponse.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    /**
     * Given 2개의 구간이 등록되어 있을 때
     * When 상행종점역으로 구간을 삭제하면
     * Then 구간이 삭제된다
     * Then 두 번째, 세 번째 역으로 이루어진 구간만 남게 된다
     */
    @DisplayName("상행종점역으로 구간을 삭제한다")
    @Test
    void deleteSectionByFinalUpStation() {
        // given
        final long distance = 10L;
        구간을_등록한다(GANGNAM_ID, YANGJAE_ID, distance);

        // when
        final ExtractableResponse<Response> deleteResponse = 구간을_삭제한다(GANGNAM_ID);

        // then
        assertThat(deleteResponse.statusCode()).isEqualTo(HttpStatus.OK.value());

        // then
        final List<SectionResponse> afterSections = 구간_목록을_조회한다().jsonPath().getList(".", SectionResponse.class);
        assertThat(afterSections)
                .containsExactly(new SectionResponse(
                        SINBUNDANGSUN_NAME,
                        YANGJAE_NAME,
                        JUNGJA_NAME,
                        DISTANCE_FROM_GANGNAME_TO_JUNGJA - distance));
    }

    /**
     * Given 2개의 구간이 등록되어 있을 때
     * When 하행종점역으로 구간을 삭제하면
     * Then 구간이 삭제된다
     * Then 첫 번째, 두 번째 역으로 이루어진 구간만 남게 된다
     */
    @DisplayName("하행종점역으로 구간을 삭제한다")
    @Test
    void deleteSectionByFinalDownStation() {
        // given
        final long distance = 10L;
        구간을_등록한다(GANGNAM_ID, YANGJAE_ID, distance);

        // when
        final ExtractableResponse<Response> deleteResponse = 구간을_삭제한다(JUNGJA_ID);

        // then
        assertThat(deleteResponse.statusCode()).isEqualTo(HttpStatus.OK.value());

        // then
        final List<SectionResponse> afterSections = 구간_목록을_조회한다().jsonPath().getList(".", SectionResponse.class);
        assertThat(afterSections)
                .containsExactly(new SectionResponse(
                        SINBUNDANGSUN_NAME,
                        GANGNAM_NAME,
                        YANGJAE_NAME,
                        distance));
    }

    /**
     * Given 2개의 구간이 등록되어 있을 때
     * When 두 번째 역으로 구간을 삭제하면
     * Then 구간이 삭제된다
     * Then 첫 번째, 세 번째 역으로 이루어진 구간만 남게 된다
     */
    @DisplayName("종점역이 아닌 역으로 구간을 삭제한다")
    @Test
    void deleteSectionByMiddleStation() {
        // given
        final long distance = 10L;
        구간을_등록한다(GANGNAM_ID, YANGJAE_ID, distance);

        // when
        final ExtractableResponse<Response> deleteResponse = 구간을_삭제한다(JUNGJA_ID);

        // then
        assertThat(deleteResponse.statusCode()).isEqualTo(HttpStatus.OK.value());

        // then
        final List<SectionResponse> afterSections = 구간_목록을_조회한다().jsonPath().getList(".", SectionResponse.class);
        assertThat(afterSections)
                .containsExactly(new SectionResponse(
                        SINBUNDANGSUN_NAME,
                        GANGNAM_NAME,
                        JUNGJA_NAME,
                        DISTANCE_FROM_GANGNAME_TO_JUNGJA));
    }

    /**
     * Given 2개의 구간이 등록되어 있을 때
     * When 등록되지 않은 역으로 구간을 삭제하면
     * Then 400 에러가 발생한다
     */
    @DisplayName("등록되지 않은 역으로 구간을 삭제한다")
    @Test
    void deleteSectionByInvalidStation() {
        // given
        final long distance = 10L;
        구간을_등록한다(GANGNAM_ID, YANGJAE_ID, distance);

        // when
        final ExtractableResponse<Response> deleteResponse = 구간을_삭제한다(SINNONHYUN_ID);

        // then
        assertThat(deleteResponse.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    /**
     * When 구간이 하나일 때 구간을 삭제하면
     * Then 400 에러가 발생한다
     */
    @DisplayName("구간이 하나일 때 구간을 삭제한다")
    @Test
    void deleteSectionWhenOnlyOneSectionExists() {
        // when
        final ExtractableResponse<Response> deleteResponse = 구간을_삭제한다(GANGNAM_ID);

        // then
        assertThat(deleteResponse.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    private ExtractableResponse<Response> 구간을_등록한다(final long upStationId,
                                                   final long downStationId,
                                                   final long distance) {
        final Map<String, Object> params = new HashMap<>();
        params.put("upStationId", upStationId);
        params.put("downStationId", downStationId);
        params.put("distance", distance);

        return RestAssured
                .given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(params)
                .when().post("/lines/{id}/sections", SINBUNDANGSUN_ID)
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> 구간_목록을_조회한다() {
        return RestAssured
                .given().log().all()
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .when().get("/lines/{id}/sections", SINBUNDANGSUN_ID)
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> 구간을_삭제한다(final long stationId) {
        return RestAssured
                .given().log().all()
                .queryParam("stationId", stationId)
                .when().delete("/lines/{id}/sections", SINBUNDANGSUN_ID)
                .then().log().all()
                .extract();
    }
}
