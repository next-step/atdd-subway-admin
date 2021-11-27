package nextstep.subway.section;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.LineAcceptanceTest;
import nextstep.subway.line.LineFixture;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.station.StationFixture;
import nextstep.subway.station.dto.StationResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("구간 관련 기능")
public class SectionAcceptanceTest extends AcceptanceTest {
    private LineResponse lineResponse;
    private StationResponse upStation;
    private StationResponse downStation;

    @Override
    @BeforeEach
    public void setUp() {
        super.setUp();

        upStation = StationFixture.ofStationResponse(StationFixture.requestCreateStations("강남역"));
        downStation = StationFixture.ofStationResponse(StationFixture.requestCreateStations("역삼역"));
        lineResponse = LineFixture.ofLineResponse(
                LineAcceptanceTest.requestCreateLineWithStation(upStation, downStation, 9, "2호선", "green")
        );

    }

    @DisplayName("새로운 역을 상행 종점으로 등록한다.")
    @Test
    void addSectionAsFirstStation() {
        // given
        // 지하철 역_생성
        StationResponse upStation = StationFixture.ofStationResponse(StationFixture.requestCreateStations("신분당역"));
        StationResponse downStation = this.upStation;
        Map<String, String> params = SectionFixture.createParams(upStation, downStation, 4);

        // when
        // 지하철_노선에_구간_등록_요청
        ExtractableResponse<Response> response = SectionFixture.requestAddSection(lineResponse.getId(), params);

        // then
        // 지하철_노선에_지하철역_등록됨
        checkResponseStatus(response, HttpStatus.OK);
    }

    @DisplayName("새로운 역을 하행 종점으로 등록한다.")
    @Test
    void addSectionAsLastStation() {
        // given
        // 지하철 역_생성
        StationResponse upStation = this.downStation;
        StationResponse downStation = StationFixture.ofStationResponse(StationFixture.requestCreateStations("잠실역"));
        Map<String, String> params = SectionFixture.createParams(upStation, downStation, 4);

        // when
        // 지하철_노선에_지하철역_등록_요청
        ExtractableResponse<Response> response = SectionFixture.requestAddSection(lineResponse.getId(), params);

        // then
        // 지하철_노선에_지하철역_등록됨
        checkResponseStatus(response, HttpStatus.OK);
    }

    @DisplayName("역 사이에 새로운 역을 등록한다.")
    @Test
    void addSectionAsNewStation() {
        // given
        // 지하철 역_생성
        StationResponse upStation = StationFixture.ofStationResponse(StationFixture.requestCreateStations("강남역삼사이역"));
        StationResponse downStation = this.downStation;
        Map<String, String> params = SectionFixture.createParams(upStation, downStation, 4);

        // when
        // 지하철_노선에_지하철역_등록_요청
        ExtractableResponse<Response> response = SectionFixture.requestAddSection(lineResponse.getId(), params);

        // then
        // 지하철_노선에_지하철역_등록됨
        checkResponseStatus(response, HttpStatus.OK);
    }

    @DisplayName("역 사이 요청 역의 길이가 기존 역 길이보다 긴 경우 등록할 수 없다.")
    @Test
    void addSectionWithLongerDistanceThanOrigin() {
        // given
        // 지하철 역_생성
        StationResponse upStation = StationFixture.ofStationResponse(StationFixture.requestCreateStations("강남역삼사이역"));
        StationResponse downStation = this.downStation;
        Map<String, String> params = SectionFixture.createParams(upStation, downStation, 10);

        // when
        // 지하철_노선에_지하철역_등록_요청
        ExtractableResponse<Response> response = SectionFixture.requestAddSection(lineResponse.getId(), params);

        // then
        // 지하철_노선에_지하철역_등록_실패
        checkResponseStatus(response, HttpStatus.BAD_REQUEST);
    }

    @DisplayName("기존에 두가지 역이 모두 등록 되어 있는 경우 등록할 수 없다.")
    @Test
    void addSectionWithAlreadyRegisteredStations() {
        // given
        // 지하철 역_생성
        Map<String, String> params = SectionFixture.createParams(upStation, downStation, 4);

        // when
        // 지하철_노선에_지하철역_등록_요청
        ExtractableResponse<Response> response = SectionFixture.requestAddSection(lineResponse.getId(), params);

        // then
        // 지하철_노선에_지하철역_등록_실패
        checkResponseStatus(response, HttpStatus.BAD_REQUEST);
    }

    @DisplayName("추가할 역이 모두 등록 되어 있지 않은 경우 등록할 수 없다.")
    @Test
    void addSectionWithNoRegisteredStations() {
        // given
        // 지하철 역_생성
        StationResponse upStation = StationFixture.ofStationResponse(StationFixture.requestCreateStations("새로운 역1"));
        StationResponse downStation = StationFixture.ofStationResponse(StationFixture.requestCreateStations("새로운 역2"));
        Map<String, String> params = SectionFixture.createParams(upStation, downStation, 4);

        // when
        // 지하철_노선에_지하철역_등록_요청
        ExtractableResponse<Response> response = SectionFixture.requestAddSection(lineResponse.getId(), params);

        // then
        // 지하철_노선에_지하철역_등록_실패
        checkResponseStatus(response, HttpStatus.BAD_REQUEST);
    }

    private void checkResponseStatus(ExtractableResponse<Response> response, HttpStatus status) {
        assertThat(response.statusCode()).isEqualTo(status.value());
    }
}
