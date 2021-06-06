package nextstep.subway.line;

import static nextstep.subway.utils.RestAssuredUtils.*;

import java.util.HashMap;
import java.util.Map;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.line.domain.Line;

public class LineAcceptanceFixture {

    public static final String PATH = "/lines";

    enum LineFixture {
        FIRST(new Line(1L, "1호선", "bg-blue-600")),
        SECOND(new Line(2L, "2호선", "bg-green-600"));

        private final Line line;

        LineFixture(final Line line) {
            this.line = line;
        }

        Long getId() {
            return line.getId();
        }

        String getName() {
            return line.getName();
        }

        String getColor() {
            return line.getColor();
        }

        Line line() {
            return line;
        }
    }

    static ExtractableResponse<Response> 지하철_노선_생성_요청(final String path, final LineFixture lineFixture) {
        final Map<String, String> params = new HashMap<>();
        params.put("name", lineFixture.getName());
        params.put("color", lineFixture.getColor());

        return post(PATH, params);
    }

    static ExtractableResponse<Response> 지하철_노션_조회_요청(final String path) {
        return get(path);
    }

    static ExtractableResponse<Response> 지하철_노선_수정_요청(final String path, final LineFixture lineFixture) {
        final Map<String, String> params = new HashMap<>();
        params.put("name", lineFixture.getName());
        params.put("color", lineFixture.getColor());

        return put(path, params);
    }

    static ExtractableResponse<Response> 지하철_노선_제거_요청(final String path) {
        return delete(path);
    }

    static Line toLine(final ExtractableResponse<Response> response) {
        return response.jsonPath()
            .getObject(".", Line.class);
    }
}
