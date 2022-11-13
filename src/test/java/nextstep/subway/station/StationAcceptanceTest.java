package nextstep.subway.station;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static nextstep.subway.AcceptanceFixture.식별_아이디_조회;
import static nextstep.subway.station.StationAcceptanceFixture.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("지하철역 인수 테스트")
public class StationAcceptanceTest extends AcceptanceTest {

    /**
     * When 지하철역을 생성하면
     * Then 지하철역이 생성된다. 지하철역 목록 조회 시 생성한 역을 찾을 수 있다
     */
    @DisplayName("지하철역을 생성한다.")
    @Test
    void createStation() {
        // when
        ExtractableResponse<Response> 지하철역_생성_응답 = 지하철역_생성_요청("강남역");

        // then
        assertAll(
                () -> assertThat(지하철역_생성_응답.statusCode()).isEqualTo(HttpStatus.CREATED.value()),
                () -> assertThat(지하철역_이름_조회()).containsAnyOf("강남역")
        );
    }

    /**
     * Given 지하철역을 생성하고
     * When 기존에 존재하는 지하철역 이름으로 지하철역을 생성하면
     * Then 지하철역 생성이 안된다
     */
    @DisplayName("기존에 존재하는 지하철역 이름으로 지하철역을 생성한다.")
    @Test
    void createStationWithDuplicateName() {
        // given
        지하철역_생성_요청("강남역");

        // when
        ExtractableResponse<Response> 지하철역_생성_응답 = 지하철역_생성_요청("강남역");

        // then
        assertThat(지하철역_생성_응답.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    /**
     * Given 2개의 지하철역을 생성하고
     * When 지하철역 목록을 조회하면
     * Then 2개의 지하철역을 응답 받는다
     */
    @DisplayName("지하철역을 조회한다.")
    @Test
    void getStations() {
        // given
        지하철역_생성_요청("잠실역");
        지하철역_생성_요청("강남역");

        // then
        assertAll(
                () -> assertThat(지하철역_이름_조회().size()).isEqualTo(2),
                () -> assertThat(지하철역_이름_조회()).containsExactly("잠실역", "강남역")
        );
    }

    /**
     * Given 지하철역을 생성하고
     * When 그 지하철역을 삭제하면
     * Then 그 지하철역 목록 조회 시 생성한 역을 찾을 수 없다
     */
    @DisplayName("지하철역을 제거한다.")
    @Test
    void deleteStation() {
        // given
        ExtractableResponse<Response> 지하철역_생성_응답 = 지하철역_생성_요청("강남역");
        Long stationId = 식별_아이디_조회(지하철역_생성_응답);

        // when
        지하철역_삭제(stationId);

        // then
        assertThat(지하철역_이름_조회().size()).isEqualTo(0);
    }

}
