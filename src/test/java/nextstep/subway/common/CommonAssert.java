package nextstep.subway.common;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.springframework.http.HttpStatus;

import static nextstep.subway.station.StationAcceptanceTest.assertStatus;

public class CommonAssert {
    public static void 요청이_실패한다(ExtractableResponse<Response> response) {
        assertStatus(response, HttpStatus.BAD_REQUEST);
    }
}
