package nextstep.subway.station;

import static nextstep.subway.station.StationAcceptanceFixture.지하철_생성_결과에서_지하철역_번호를_조회한다;
import static nextstep.subway.station.StationAcceptanceFixture.지하철_역을_삭제한다;
import static nextstep.subway.station.StationAcceptanceFixture.지하철_역을_생성한다;
import static nextstep.subway.station.StationAcceptanceFixture.지하철역_목록을_조회한다;
import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;

@DisplayName("지하철역 관련 기능")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class StationAcceptanceTest {
    @LocalServerPort
    int port;

    @BeforeEach
    public void setUp() {
        if (RestAssured.port == RestAssured.UNDEFINED_PORT) {
            RestAssured.port = port;
        }
    }

    /**
     * When 지하철역을 생성하면
     * Then 지하철역이 생성된다
     * Then 지하철역 목록 조회 시 생성한 역을 찾을 수 있다
     */
    @DisplayName("지하철역을 생성한다.")
    @ParameterizedTest
    @ValueSource(strings = {"화정역", "시청역"})
    void createStation(String 지하철_역명) {
        // when
        ExtractableResponse<Response> 지하철역_생성결과 = 지하철_역을_생성한다(지하철_역명);

        // then
        지하철역_생성상태가_정상임을_확인할_수_있다(지하철역_생성결과);

        // then
        List<String> 지하철_목록 = 지하철역_목록을_조회한다();
        지하철역_목록중_해당_지하철을_찾을_수_있다(지하철_역명, 지하철_목록);
    }

    /**
     * Given 지하철역을 생성하고
     * When 기존에 존재하는 지하철역 이름으로 지하철역을 생성하면
     * Then 지하철역 생성이 안된다
     */
    @DisplayName("기존에 존재하는 지하철역 이름으로 지하철역을 생성한다.")
    @ParameterizedTest
    @ValueSource(strings = {"서울역", "행신역", "DMC역"})
    void createStationWithDuplicateName(String 지하철_역명) {
        // given
        지하철_역을_생성한다(지하철_역명);

        // when
        ExtractableResponse<Response> 지하철_생성_결과 = 지하철_역을_생성한다(지하철_역명);

        // then
        지하철역_생성_상태가_잘못된_요청_상태임을_확인_한다(지하철_생성_결과);
    }

    /**
     * Given 2개의 지하철역을 생성하고
     * When 지하철역 목록을 조회하면
     * Then 2개의 지하철역을 응답 받는다
     */
    @DisplayName("지하철역을 조회한다.")
    @ParameterizedTest
    @CsvSource(value = {("화곡역, 마곡역"), ("목동역, 오목교역")})
    void getStations(String 지하철_역명1, String 지하철_역명2) {
        // given
        지하철_역을_생성한다(지하철_역명1);
        지하철_역을_생성한다(지하철_역명2);

        // when
        List<String> 지하철_목록 = 지하철역_목록을_조회한다();

        // then
        지하철역_목록중_해당_지하철을_찾을_수_있다(지하철_역명1, 지하철_목록);
        지하철역_목록중_해당_지하철을_찾을_수_있다(지하철_역명2, 지하철_목록);
    }

    /**
     * Given 지하철역을 생성하고
     * When 그 지하철역을 삭제하면
     * Then 그 지하철역 목록 조회 시 생성한 역을 찾을 수 없다
     */
    @DisplayName("지하철역을 제거한다.")
    @ParameterizedTest
    @ValueSource(strings = {"선정릉역", "마두역", "반포역"})
    void deleteStation(String 지하철_역명) {
        // given
        ExtractableResponse<Response> 지하철_생성_결과 = 지하철_역을_생성한다(지하철_역명);
        Long 지하철역_번호 = 지하철_생성_결과에서_지하철역_번호를_조회한다(지하철_생성_결과);

        // when
        지하철_역을_삭제한다(지하철역_번호);

        // then
        List<String> 지하철역_목록 = 지하철역_목록을_조회한다();
        지하철역_목록중_해당_지하철을_찾을_수_없다(지하철_역명, 지하철역_목록);
    }

    private void 지하철역_생성상태가_정상임을_확인할_수_있다(ExtractableResponse<Response> 지하철역_생성결과) {
        assertThat(지하철역_생성결과.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    }

    private void 지하철역_목록중_해당_지하철을_찾을_수_있다(String 지하철_역명, List<String> 지하철_목록) {
        assertThat(지하철_목록).containsAnyOf(지하철_역명);
    }

    private void 지하철역_목록중_해당_지하철을_찾을_수_없다(String 지하철_역명, List<String> 지하철_목록) {
        assertThat(지하철_목록).doesNotContain(지하철_역명);
    }

    private void 지하철역_생성_상태가_잘못된_요청_상태임을_확인_한다(ExtractableResponse<Response> 지하철_생성_결과) {
        assertThat(지하철_생성_결과.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }
}
