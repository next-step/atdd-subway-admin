package nextstep.subway.line.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SectionRequest {
    @NonNull
    private Long upStationId;
    @NonNull
    private Long downStationId;
    private int distance;
}

