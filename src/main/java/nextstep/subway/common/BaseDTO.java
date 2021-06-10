package nextstep.subway.common;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Map;
import java.util.stream.Collectors;

public abstract class BaseDTO<E> {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    protected abstract E toEntity();

    public final Map<String, String> toMap() {
        Map<String, Object> result = objectMapper.convertValue(this, Map.class);

        return result.keySet()
                .stream()
                .collect(Collectors.toMap(key -> key, key -> String.valueOf(result.get(key))));
    }
}
