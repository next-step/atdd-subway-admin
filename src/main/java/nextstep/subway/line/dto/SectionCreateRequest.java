package nextstep.subway.line.dto;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

/**
 * @author : leesangbae
 * @project : subway
 * @since : 2020-12-30
 */
public class SectionCreateRequest {

    @NotNull
    @Positive
    private Long downStationId;

    @NotNull
    @Positive
    private Long upStationId;

    @Positive
    private int distance;

    private SectionCreateRequest() {
    }

    public Long getDownStationId() {
        return downStationId;
    }

    public Long getUpStationId() {
        return upStationId;
    }

    public int getDistance() {
        return distance;
    }

}
