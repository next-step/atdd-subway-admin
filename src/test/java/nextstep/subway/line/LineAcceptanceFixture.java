package nextstep.subway.line;

import static nextstep.subway.utils.RestAssuredUtils.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.restassured.path.json.JsonPath;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.line.domain.Line;
import nextstep.subway.station.domain.Station;

public class LineAcceptanceFixture {

    public static final String PATH = "/lines";

    enum LineFixture {
        FIRST(new Line(1L, "1호선", "bg-blue-600"), "1", "2", "100"),
        SECOND(new Line(2L, "2호선", "bg-green-600"), "1", "2", "100");

        private final Line line;
        private final String upStationId;
        private final String downStationId;
        private final String distance;

        LineFixture(final Line line, final String upStationId, final String downStationId, final String distance) {
            this.line = line;
            this.upStationId = upStationId;
            this.downStationId = downStationId;
            this.distance = distance;
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

        public String getUpStationId() {
            return upStationId;
        }

        public String getDownStationId() {
            return downStationId;
        }

        public String getDistance() {
            return distance;
        }

        Line line() {
            return line;
        }
    }

    enum SectionFixture {
        SECTION("2", "3", "50"),
        DUPLICATED_SECTION("1", "2", "50"),
        SECTION_NEW_STATION("3", "4", "50"),
        TOO_LONG_DISTANCE("1", "3", "100");

        private final String upStation;
        private final String downStation;
        private final String distance;

        SectionFixture(final String upStation, final String downStation, final String distance) {
            this.upStation = upStation;
            this.downStation = downStation;
            this.distance = distance;
        }

        public String getUpStation() {
            return upStation;
        }

        public String getDownStation() {
            return downStation;
        }

        public String getDistance() {
            return distance;
        }
    }

    static ExtractableResponse<Response> 지하철_노선_생성_요청(final String path, final LineFixture lineFixture) {
        final Map<String, String> params = new HashMap<>();
        params.put("name", lineFixture.getName());
        params.put("color", lineFixture.getColor());
        params.put("upStationId", lineFixture.getUpStationId());
        params.put("downStationId", lineFixture.getDownStationId());
        params.put("distance", lineFixture.getDistance());

        return post(path, params);
    }

    static ExtractableResponse<Response> 지하철_구간에_지하철역_삭제_요청(final String path) {
        return delete(path);
    }

    static ExtractableResponse<Response> 지하철_구간에_지하철역_등록_요청(final String path, final SectionFixture sectionFixture) {
        final Map<String, String> params = new HashMap<>();
        params.put("downStationId", sectionFixture.getDownStation());
        params.put("upStationId", sectionFixture.getUpStation());
        params.put("distance", sectionFixture.getDistance());

        return post(path, params);
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
        final JsonPath jsonPath = response.jsonPath();

        return new Line(jsonPath.getLong("id"), jsonPath.getString("name"), jsonPath.getString("color"));
    }

    static List<Station> toStations(final ExtractableResponse<Response> response) {
        final JsonPath jsonPath = response.jsonPath();

        return jsonPath.getList("stations", Station.class);
    }
}
