package nextstep.subway.acceptance;

import static nextstep.subway.utils.AssertionsUtils.assertInternalServerError;
import static nextstep.subway.utils.AssertionsUtils.assertOk;
import static nextstep.subway.utils.LineAcceptanceTestUtils.지하철_노선_생성_요청;
import static nextstep.subway.utils.LineAcceptanceTestUtils.지하철_노선_조회_요청;
import static nextstep.subway.utils.ResponseBodyExtractUtils.getIdAsLong;
import static nextstep.subway.utils.SectionAcceptanceTestUtils.지하철_구간_삭제_요청;
import static nextstep.subway.utils.SectionAcceptanceTestUtils.지하철_구간_생성_요청;
import static nextstep.subway.utils.StationsAcceptanceUtils.지하철역_생성_요청;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import java.util.stream.Stream;
import nextstep.subway.config.BaseTest;
import nextstep.subway.dto.line.LineResponse;
import nextstep.subway.dto.line.section.CreateSectionRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

@DisplayName("구간 삭제 관련 기능")
public class SectionRemoveAcceptanceTest extends BaseTest {

    private Long 신분당선;
    private Long 논현역;
    private Long 정자역;
    private Long 강남역;

    @BeforeEach
    public void setUp() {
        super.setUp();
        지하철_노선_생성();
        지하철_구간_추가();
    }

    private void 지하철_노선_생성() {
        LineResponse lineResponse = 지하철_노선_생성_요청("신분당선", "논현역", "정자역").as(LineResponse.class);
        논현역 = lineResponse.getStations().get(0).getId();
        정자역 = lineResponse.getStations().get(1).getId();
        신분당선 = lineResponse.getId();
    }

    private void 지하철_구간_추가() {
        강남역 = getIdAsLong(지하철역_생성_요청("강남역"));
        지하철_구간_생성_요청(신분당선, new CreateSectionRequest(강남역, 정자역, 30));
    }

    /**
     * Given `논현-(70)-강남-(30)-정자` 구간이 존재하는 노선을 생성한다.
     * When 노선의 상행종점역인 '논현역'을 삭제한다
     * Then 노선 조회 시, '강남-(30)-정자' 구간을 확인할 수 있다.
     */
    @Test
    @DisplayName("노선의 상행종점역을 삭제한다.")
    public void removeFinalUpStation() {
        // When
        Response 지하철_구간_삭제_응답 = 지하철_구간_삭제_요청(신분당선, 논현역);
        Response 지하철_노선_조회_응답 = 지하철_노선_조회_요청(신분당선);

        // Then
        JsonPath jsonPath = 지하철_노선_조회_응답.jsonPath();
        assertAll(
            () -> assertOk(지하철_구간_삭제_응답),
            () -> assertOk(지하철_노선_조회_응답),
            () -> assertThat(jsonPath.getList("stations.id", Long.class)).containsExactly(강남역, 정자역)
        );
    }

    /**
     * Given `논현-(70)-강남-(30)-정자` 구간이 존재하는 노선을 생성한다.
     * When 노선의 하행종점역인 '정자역'을 삭제한다
     * Then 노선 조회 시, '논현-(70)-강남' 구간을 확인할 수 있다.
     */
    @Test
    @DisplayName("노선의 하행종점역을 삭제한다.")
    public void removeFinalDownStation() {
        // When
        Response 지하철_구간_삭제_응답 = 지하철_구간_삭제_요청(신분당선, 정자역);
        Response 지하철_노선_조회_응답 = 지하철_노선_조회_요청(신분당선);

        // Then
        JsonPath jsonPath = 지하철_노선_조회_응답.jsonPath();
        assertAll(
            () -> assertOk(지하철_구간_삭제_응답),
            () -> assertOk(지하철_노선_조회_응답),
            () -> assertThat(jsonPath.getList("stations.id", Long.class)).containsExactly(논현역, 강남역)
        );
    }

    /**
     * Given `논현-(70)-강남-(30)-정자` 구간이 존재하는 노선을 생성한다.
     * When 노선 중간에 위치한 '강남역'을 삭제한다
     * Then 노선 조회 시, '논현-(100)-정자' 구간을 확인할 수 있다.
     */
    @Test
    @DisplayName("노선의 중간역을 삭제한다.")
    public void removeMiddleStation() {
        // When
        Response 지하철_구간_삭제_응답 = 지하철_구간_삭제_요청(신분당선, 강남역);
        Response 지하철_노선_조회_응답 = 지하철_노선_조회_요청(신분당선);

        // Then
        JsonPath jsonPath = 지하철_노선_조회_응답.jsonPath();
        assertAll(
            () -> assertOk(지하철_구간_삭제_응답),
            () -> assertOk(지하철_노선_조회_응답),
            () -> assertThat(jsonPath.getList("stations.id", Long.class)).containsExactly(논현역, 정자역)
        );
    }

    /**
     * Given `논현-(70)-강남-(30)-정자` 구간이 존재하는 노선을 생성한다.
     * When 노선에 포함되지 않은 '판교역'을 삭제한다
     * Then 노선에 포함되지 않은 판교역은 삭제할 수 없다.
     */
    @Test
    @DisplayName("노선에 포함되지 않은 역은 삭제 할 수 없다.")
    public void throwException_WhenLineHasOnlyOneSection() {
        // Given
        Long 판교역 = getIdAsLong(지하철역_생성_요청("판교역"));

        // When
        Response 지하철_구간_삭제_응답 = 지하철_구간_삭제_요청(신분당선, 판교역);

        // Then
        assertInternalServerError(지하철_구간_삭제_응답);
    }

    /**
     * Given `선릉-(100)-수서` 구간이 존재하는 노선을 생성한다.
     * When 구간이 하나뿐인 노선의 상행역 또는 하행역을 삭제한다.
     * Then 구간이 하나뿐인 노선의 상행역 또는 하행역은 삭제할 수 없다.
     */
    @ParameterizedTest
    @MethodSource
    @DisplayName("구간이 하나뿐인 노선의 상행역 또는 하행역은 삭제할 수 없다")
    public void throwException_WhenRemoveTargetStationIsNotContainsInLineStations(Long 노선, Long 삭제역, String givenDescription) {
        // When
        Response 지하철_구간_삭제_응답 = 지하철_구간_삭제_요청(노선, 삭제역);

        // Then
        assertInternalServerError(지하철_구간_삭제_응답, givenDescription);
    }

    private static Stream throwException_WhenRemoveTargetStationIsNotContainsInLineStations() {
        LineResponse lineResponse = 지하철_노선_생성_요청("분당선", "선릉역", "수서역").as(LineResponse.class);
        Long 분당선 = lineResponse.getId();
        Long 선릉역 = lineResponse.getStations().get(0).getId();
        Long 수서역 = lineResponse.getStations().get(1).getId();
        return Stream.of(
            Arguments.of(분당선, 선릉역, "구간이 하나뿐인 노선의 상행역을 삭제하는 경우"),
            Arguments.of(분당선, 수서역, "구간이 하나뿐인 노선의 하행역을 삭제하는 경우")
        );
    }
}
