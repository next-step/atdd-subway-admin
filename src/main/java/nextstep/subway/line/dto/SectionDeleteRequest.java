package nextstep.subway.line.dto;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

/**
 * @author : leesangbae
 * @project : subway
 * @since : 2021-01-05
 */
public class SectionDeleteRequest {

    @NotNull
    @Positive
    private Long stationId;

    public Long getStationId() {
        return stationId;
    }
}
