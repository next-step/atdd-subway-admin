package nextstep.subway.fixture;

import nextstep.subway.api.HttpMethod;
import nextstep.subway.line.domain.Line;
import nextstep.subway.station.dto.StationResponse;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static nextstep.subway.fixture.LineFixture.*;

public class StationFixture {
    public static final HashMap<String, String> 암사역 = new HashMap<String, String>() {{
        put("name", "암사역");
    }};

    public static final HashMap<String, String> 천호역 = new HashMap<String, String>() {{
        put("name", "천호역");
    }};

    public static final HashMap<String, String> 강동구청역 = new HashMap<String, String>() {{
        put("name", "강동구청역");
    }};

    public static final HashMap<String, String> 몽촌토성역 = new HashMap<String, String>() {{
        put("name", "몽촌토성역");
    }};

    public static final HashMap<String, String> 잠실역 = new HashMap<String, String>() {{
        put("name", "잠실역");
    }};

    public static Long 암사역_아이디;
    public static Long 천호역_아이디;
    public static Long 강동구청역_아이디;
    public static Long 몽촌토성역_아이디;
    public static Long 잠실역_아이디;

    public static Map<String, StationResponse> 역_생성_응답;

    public static Map<String, Map<String, String>> 팔호선_구간 = new HashMap<>();

    public static void createStationInAdvance() {
        역_생성_응답 = 팔호선_역.stream()
            .map(HttpMethod::지하철_역_등록)
            .map(e -> e.jsonPath().getObject(".", StationResponse.class))
            .collect(Collectors.toMap(StationResponse::getName, Function.identity()));
        구간_미리_생성하기(팔호선, 팔호선_구간, 역_생성_응답, 팔호선_역, 팔호선_구간_거리);

        암사역_아이디 = 역_생성_응답.get("암사역").getId();
        천호역_아이디 = 역_생성_응답.get("천호역").getId();
        강동구청역_아이디 = 역_생성_응답.get("강동구청역").getId();
        몽촌토성역_아이디 = 역_생성_응답.get("몽촌토성역").getId();
        잠실역_아이디 = 역_생성_응답.get("잠실역").getId();
    }

    private static void 구간_미리_생성하기(Line line, Map<String, Map<String, String>> sections
        , Map<String, StationResponse> responses, List<Map<String, String>> stations, List<Integer> distances) {

        for (int i = 0; i < stations.size() - 1; i++) {
            StationResponse upStationResponse = responses.get(stations.get(i).get("name"));
            StationResponse downStationResponse = responses.get(stations.get(i + 1).get("name"));
            String upStationId = String.valueOf(upStationResponse.getId());
            String downStationId = String.valueOf(downStationResponse.getId());
            String distance = String.valueOf(distances.get(i));

            sections.put(upStationResponse.getName() + "-" + downStationResponse.getName(), new HashMap<String, String>() {{
                put("name", line.getName());
                put("color", line.getColor());
                put("upStationId", upStationId);
                put("downStationId", downStationId);
                put("distance", distance);
            }});
        }
    }
}
