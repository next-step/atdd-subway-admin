package nextstep.subway.station;


import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static nextstep.subway.station.StationAcceptanceStep.*;


@DisplayName("지하철역 관련 기능")
public class StationAcceptanceTest extends AcceptanceTest {
    @DisplayName("지하철역을 생성한다.")
    @Test
    void createStation() {
        // when
        ExtractableResponse<Response> response = 지하철역_생성_요청("강남역");

        // then
        지하철역_생성_응답상태_201_검증(response);
    }

    @DisplayName("기존에 존재하는 지하철역 이름으로 지하철역을 생성한다.")
    @Test
    void createStationWithDuplicateName() {
        //given
        등록된_지하철역("강남역");

        // when
        ExtractableResponse<Response> response = 지하철역_생성_요청("강남역");

        // then
        지하철역_생성_응답상태_400_검증(response);
    }

    @DisplayName("지하철역을 조회한다.")
    @Test
    void getStations() {
        // given
        ExtractableResponse<Response> createResponse1 = 등록된_지하철역("종각역");
        ExtractableResponse<Response> createResponse2 = 등록된_지하철역("역삼역");

        // when
        ExtractableResponse<Response> response = 지하철역_목록_조회_요청();

        // then
        지하철역_목록_응답상태_200_검증(response);
        지하철역_목록_검증(response, Arrays.asList(createResponse1, createResponse2));
    }

    @DisplayName("지하철역을 제거한다.")
    @Test
    void deleteStation() {
        // given
        ExtractableResponse<Response> createResponse = 등록된_지하철역("강남역");

        // when
        ExtractableResponse<Response> response = 지하철역_제거_요청(createResponse);

        // then
        지하철역_삭제_응답상태_204_검증(response);
    }
}

