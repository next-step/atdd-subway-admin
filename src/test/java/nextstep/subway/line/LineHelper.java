package nextstep.subway.line;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.line.dto.LineRequest;

public class LineHelper {

    public static Long 지하철_노선_등록되어_있음(LineRequest lineRequest) {
        ExtractableResponse<Response> createResponse = LineApiRequests.지하철_노선_생성_요청(lineRequest);
        return Long.valueOf(createResponse.header("Location").split("/")[2]);
    }
}
