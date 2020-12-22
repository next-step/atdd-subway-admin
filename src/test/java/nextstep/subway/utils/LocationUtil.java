package nextstep.subway.utils;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;

import java.util.List;
import java.util.stream.Collectors;

public class LocationUtil {
    public static List<Long> getIdsToLocationHeaders(List<ExtractableResponse<Response>> createResponse) {
        return createResponse.stream()
                .map(LocationUtil::getLocation)
                .collect(Collectors.toList());
    }

    public static long getLocation(ExtractableResponse<Response> it) {
        return Long.parseLong(it.header("Location").split("/")[2]);
    }
}
