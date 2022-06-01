package nextstep.subway.api;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.api.request.StationRequest;

import java.util.List;

public class StationApi extends BaseApi {
    public String urlWithId(Long id) {
        return String.format(super.baseUrl + "/%d", id);
    }

    public StationApi() {
        super("/stations");
    }

    public ExtractableResponse<Response> create(String name) {
        return super.create(StationRequest.createParams(name));
    }

    public List<String> findNames() {
        return super.findAll().jsonPath().getList("name", String.class);
    }

    public ExtractableResponse<Response> delete(Long id) {
        return super.delete(id);
    }
}
