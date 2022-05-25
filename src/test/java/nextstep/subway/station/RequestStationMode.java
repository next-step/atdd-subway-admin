package nextstep.subway.station;

import io.restassured.specification.RequestSpecification;

import java.util.function.Function;
import io.restassured.response.Response;

public enum RequestStationMode {
    CREATE(requestSpecification -> requestSpecification.post("/stations")),
    SEARCH_ALL(requestSpecification -> requestSpecification.get("/stations"));

    final Function<RequestSpecification, Response> function;

    RequestStationMode(Function<RequestSpecification, Response> function) {
        this.function = function;
    }

    public Function<RequestSpecification, Response> getFunction() {
        return function;
    }
}
