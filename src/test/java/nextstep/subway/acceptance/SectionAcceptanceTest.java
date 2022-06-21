package nextstep.subway.acceptance;

import static nextstep.subway.utils.AssertionsUtils.assertCreated;
import static nextstep.subway.utils.AssertionsUtils.assertInternalServerError;
import static nextstep.subway.utils.LineAcceptanceTestUtils.지하철_노선_생성_요청;
import static nextstep.subway.utils.ResponseBodyExtractUtils.getIdAsLong;
import static nextstep.subway.utils.SectionAcceptanceTestUtils.지하철_구간_생성_요청;
import static nextstep.subway.utils.StationsAcceptanceUtils.지하철역_생성_요청;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import javax.annotation.Resource;
import nextstep.subway.config.AcceptanceTest;
import nextstep.subway.dto.line.LineResponse;
import nextstep.subway.dto.line.section.CreateSectionRequest;
import nextstep.subway.utils.TearDownUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.boot.web.server.LocalServerPort;

@DisplayName("구간 관련 기능")
@AcceptanceTest
public class SectionAcceptanceTest {

    private Long 신분당선;
    private Long 논현역;
    private Long 정자역;

    @LocalServerPort
    int port;

    @Resource
    protected TearDownUtils tearDownUtils;

    @BeforeEach
    void setUp() {
        if (RestAssured.port == RestAssured.UNDEFINED_PORT) {
            RestAssured.port = port;
        }
        tearDownUtils.tableClear();
        지하철_노선_생성();
    }

    private void 지하철_노선_생성() {
        LineResponse lineResponse = 지하철_노선_생성_요청("신분당선", "논현역", "정자역").as(LineResponse.class);
        논현역 = lineResponse.getStations().get(0).getId();
        정자역 = lineResponse.getStations().get(1).getId();
        신분당선 = lineResponse.getId();
    }

    /**
     * Given `논현-(100)-정자` 구간이 존재하는 노선을 생성한다.
     * When 노선에 신규 상행종점역인 `신사-(10)-논현` 구간을 추가한다.
     * Then 노선 조회 시, 추가한 `신사-(10)-논현` 구간을 확인할 수 있다.
     */
    @Test
    @DisplayName("지하철 노선에 신규 상행종점역 구간을 추가한다.")
    public void addFinalUpSection() {
        // Given
        Long 신사역 = getIdAsLong(지하철역_생성_요청("신사역"));

        // When
        Response 지하철_구간_생성_응답 = 지하철_구간_생성_요청(신분당선, new CreateSectionRequest(신사역, 논현역, 10));

        // Then
        JsonPath jsonPath = 지하철_구간_생성_응답.jsonPath();
        assertAll(
            () -> assertCreated(지하철_구간_생성_응답),
            () -> assertThat(jsonPath.getList("stations.id", Long.class)).containsExactly(신사역, 논현역, 정자역)
        );
    }

    /**
     * Given `논현-(100)-정자` 구간이 존재하는 노선을 생성한다.
     * When 노선에 신규 하행종점역인 `정자-(10)-미금` 구간을 추가한다.
     * Then 노선 조회 시, 추가한 `정자-(10)-미금` 구간을 확인할 수 있다.
     */
    @Test
    @DisplayName("지하철 노선에 신규 하행종점역 구간을 추가한다.")
    public void addFinalDownSection() {
        // Given
        Long 미금역 = getIdAsLong(지하철역_생성_요청("미금역"));

        // When
        Response 지하철_구간_생성_응답 = 지하철_구간_생성_요청(신분당선, new CreateSectionRequest(정자역, 미금역, 10));

        JsonPath jsonPath = 지하철_구간_생성_응답.jsonPath();
        assertAll(
            () -> assertCreated(지하철_구간_생성_응답),
            () -> assertThat(jsonPath.getList("stations.id", Long.class)).containsExactly(논현역, 정자역, 미금역)
        );
    }

    /**
     * Given `논현-(100)-정자` 구간이 존재하는 노선을 생성한다.
     * When 노선 중간에 상행역이 동일한 `논현-(10)-신논현` 구간을 추가한다
     * Then 노선 조회 시, 중간에 추가된 `논현-(10)-신논현` 구간을 확인할 수 있다.
     */
    @Test
    @DisplayName("지하철 노선에 상행역이 동일한 구간을 추가한다.")
    public void addSectionWhenSameUpStation() {
        // Given
        Long 신논현역 = getIdAsLong(지하철역_생성_요청("신논현역"));

        // When
        Response 지하철_구간_생성_응답 = 지하철_구간_생성_요청(신분당선, new CreateSectionRequest(논현역, 신논현역, 10));

        // Then
        JsonPath jsonPath = 지하철_구간_생성_응답.jsonPath();
        assertAll(
            () -> assertCreated(지하철_구간_생성_응답),
            () -> assertThat(jsonPath.getList("stations.id", Long.class)).containsExactly(논현역, 신논현역, 정자역)
        );
    }

    /**
     * Given `논현-(100)-정자` 구간이 존재하는 노선을 생성한다.
     * When 노선 중간에 하행역이 동일한 `신논현-(10)-정자` 구간을 추가한다
     * Then 노선 조회 시, 중간에 추가된 `신논현-(10)-정자` 구간을 확인할 수 있다.
     */
    @Test
    @DisplayName("지하철 노선에 하행역이 동일한 구간을 추가한다.")
    public void addSectionWhenSameDownStation() {
        // Given
        Long 신논현역 = getIdAsLong(지하철역_생성_요청("신논현역"));

        // When
        Response 지하철_구간_생성_응답 = 지하철_구간_생성_요청(신분당선, new CreateSectionRequest(신논현역, 정자역, 10));

        // Then
        JsonPath jsonPath = 지하철_구간_생성_응답.jsonPath();
        assertAll(
            () -> assertCreated(지하철_구간_생성_응답),
            () -> assertThat(jsonPath.getList("stations.id", Long.class)).containsExactly(논현역, 신논현역, 정자역)
        );
    }

    /**
     * Given `논현-(100)-정자` 구간이 존재하는 노선을 생성한다.
     * When 동일한 `논현-(100)-정자` 구간을 추가한다.
     * Then 동일 구간 추가 시, 오류가 발생한다.
     */
    @Test
    @DisplayName("동일한 구간을 추가하는 경우 예외가 발생한다.")
    public void throwException_WhenAddSameSections() {
        // When
        Response 지하철_구간_생성_응답 = 지하철_구간_생성_요청(신분당선, new CreateSectionRequest(논현역, 정자역, 100));

        // Then
        assertInternalServerError(지하철_구간_생성_응답);
    }

    /**
     * Given `논현-(100)-정자` 구간이 존재하는 노선을 생성한다.
     * When 접점이 없는 역이 포함된 `강남-양재` 구간을 추가한다.
     * Then 접점이 없는 역이 포함된 구간 추가 시 오류가 발생한다.
     */
    @Test
    @DisplayName("접점이 없는 역이 포함된 구간을 추가하는 경우 예외가 발생한다.")
    public void throwException_WhenStationsIsNotConnected() {
        // Given
        Long 강남역 = getIdAsLong(지하철역_생성_요청("강남역"));
        Long 양재역 = getIdAsLong(지하철역_생성_요청("양재역"));

        // When
        Response 지하철_구간_생성_응답 = 지하철_구간_생성_요청(신분당선, new CreateSectionRequest(강남역, 양재역, 10));

        // Then
        assertInternalServerError(지하철_구간_생성_응답);
    }

    /**
     * Given `논현-(50)-신논현-(50)-정자` 구간이 존재하는 노선을 생성한다.
     * When 추가하는 구간의 길이가 유효하지 않은 구간을 추가한다.
     *     When - 추가하는 구간의 길이가 0 혹은 음수인 경우
     *     When - 추가하는 구간의 길이가 연결 구간의 길이보다 크거나 같은 경우
     *     When - 추가하는 구간의 길이가 전체 구간의 길이보다 크거나 같은 경우
     * Then 구간의 길이가 유효하지 않는 경우 오류가 발생한다.
     */
    @ParameterizedTest
    @ValueSource(ints = {Integer.MIN_VALUE, 0, 50, 51, 100, 101})
    @DisplayName("구간의 길이가 유효하지 않은 경우 예외가 발생한다.")
    public void throwException_WhenAddInvalidSectionDistance(int 유효하지_않은_구간_길이) {
        // Given
        Long 신논현역 = getIdAsLong(지하철역_생성_요청("신논현역"));
        Long 강남역 =  getIdAsLong(지하철역_생성_요청("강남역"));
        지하철_구간_생성_요청(신분당선, new CreateSectionRequest(논현역, 신논현역, 50));

        // When
        Response 지하철_구간_생성_응답 = 지하철_구간_생성_요청(신분당선, new CreateSectionRequest(강남역, 정자역, 유효하지_않은_구간_길이));

        // Then
        assertInternalServerError(지하철_구간_생성_응답);
    }
}
