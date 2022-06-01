package nextstep.subway.api;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.api.request.LineRequest;

import java.util.List;

public class LineApi extends BaseApi {
    private static String KEY_NAME = "name";

    public LineApi() {
        super("/lines");
    }

    public String urlWithId(Long id) {
        return String.format(super.baseUrl + "/%d", id);
    }

    public ExtractableResponse<Response> create(String name, String color, Long upStationId, Long downStationId) {
        return super.create(LineRequest.createParams(name, color, upStationId.toString(), downStationId.toString()));
    }

    public long createId(String name, String color, Long upStationId, Long downStationId) {
        return this.create(name, color, upStationId, downStationId).jsonPath().getLong("id");
    }

    public ExtractableResponse<Response> findById(Long id) {
        return super.findById(id);
    }

    public String findName(Long id) {
        return this.findById(id).jsonPath().getString(KEY_NAME);
    }

    public List<String> findNames() {
        return super.findAll().jsonPath().getList(KEY_NAME, String.class);
    }

    public ExtractableResponse<Response> update(Long id, String name, String color) {
        return super.update(id, LineRequest.updateParams(name, color));
    }

    public ExtractableResponse<Response> delete(Long id) {
        return super.delete(id);
    }
}
