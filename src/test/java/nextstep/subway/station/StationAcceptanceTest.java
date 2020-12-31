package nextstep.subway.station;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.utils.DatabaseCleanup;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.server.LocalServerPort;

import static nextstep.subway.station.StationAcceptanceTestSupport.*;

@DisplayName("지하철역 관련 기능")
public class StationAcceptanceTest extends AcceptanceTest {

	@Autowired
    private DatabaseCleanup databaseCleanup;

    @LocalServerPort
    private int port;

    @Override
    @BeforeEach
    public void setUp() {
        RestAssured.port = port;
        databaseCleanup.execute();
    }

    @DisplayName("지하철역을 생성한다.")
    @Test
    void createStation() {
    	// when
        ExtractableResponse<Response> response = 지하철역_생성_요청("강남역");

        // then
	    지하철역_생성_성공(response);
    }

	@DisplayName("기존에 존재하는 지하철역 이름으로 지하철역을 생성한다.")
    @Test
    void createStationWithDuplicateName() {
        // given
		지하철역_생성_요청("강남역");

        // when
        ExtractableResponse<Response> response = 지하철역_생성_요청("강남역");

        // then
		지하철역_생성_실패됨(response);
    }

	@DisplayName("지하철역을 조회한다.")
    @Test
    void getStations() {
        /// given
        ExtractableResponse<Response> createResponse1 = 지하철역_생성_요청("강남역");
        ExtractableResponse<Response> createResponse2 = 지하철역_생성_요청("역삼역");

        // when
        ExtractableResponse<Response> response = 지하철역_얻기();

        // then
		지하철역_조회_검사(createResponse1, createResponse2, response);
	}

	@DisplayName("지하철역을 제거한다.")
    @Test
    void deleteStation() {
        // given
        ExtractableResponse<Response> createResponse = 지하철역_생성_요청("강남역");

        // when
		ExtractableResponse<Response> response = 지하철역_삭제_요청(createResponse);

		// then
		지하철역_삭제_성공(response);
	}

}
