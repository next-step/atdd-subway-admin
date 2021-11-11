package nextstep.subway.station;

import static nextstep.subway.station.StationAcceptanceMethods.*;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.station.dto.StationRequest;

@DisplayName("지하철역 관련 기능")
public class StationAcceptanceTest extends AcceptanceTest {
    private static final String STATION_URL_PATH = "/stations";
    private static final String SLASH_SIGN = "/";

    @DisplayName("지하철역을 생성한다.")
    @Test
    void createStation() {
        // given
        StationRequest stationRequest = StationRequest.from("강남역");

        // when
        ExtractableResponse<Response> response = 지하철_역_생성_요청(STATION_URL_PATH, stationRequest);

        // then
        지하철_역_생성됨(response);
    }

    @DisplayName("기존에 존재하는 지하철역 이름으로 지하철역을 생성한다.")
    @Test
    void createStationWithDuplicateName() {
        // given
        StationRequest stationRequest = StationRequest.from("강남역");
        지하철_역_등록되어_있음(STATION_URL_PATH, stationRequest);

        // when
        ExtractableResponse<Response> response = 지하철_역_생성_요청(STATION_URL_PATH, stationRequest);

        // then
        지하철_역_생성_실패됨(response);
    }

    @DisplayName("지하철역을 조회한다.")
    @Test
    void getStations() {
        /// given
        StationRequest firstStationRequest = StationRequest.from("강남역");
        StationRequest secondStationRequest = StationRequest.from("역삼역");

        ExtractableResponse<Response> firstCreateResponse = 지하철_역_등록되어_있음(STATION_URL_PATH, firstStationRequest);
        ExtractableResponse<Response> secondCreateResponse = 지하철_역_등록되어_있음(STATION_URL_PATH, secondStationRequest);

        // when
        ExtractableResponse<Response> response = 지하철_역_목록_조회_요청(STATION_URL_PATH);

        // then
        List<Long> createStationIds = Stream.of(firstCreateResponse, secondCreateResponse)
                                            .map(this::parseIdFromLocationHeader)
                                            .collect(Collectors.toList());
        지하철_역_목록_응답됨(response);
        지하철_역_목록_포함됨(response, createStationIds);
    }

    @DisplayName("지하철역을 제거한다.")
    @Test
    void deleteStation() {
        // given
        StationRequest stationRequest = StationRequest.from("강남역");
        ExtractableResponse<Response> createResponse = 지하철_역_등록되어_있음(STATION_URL_PATH, stationRequest);

        // when
        String uri = createResponse.header(LOCATION_HEADER_NAME);
        ExtractableResponse<Response> response = 지하철_역_제거_요청(uri);

        // then
        지하철_역_삭제됨(response);
    }
}
