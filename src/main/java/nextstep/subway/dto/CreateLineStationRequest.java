package nextstep.subway.dto;

public interface CreateLineStationRequest {
    Long getDistance();

    Long getUpStationId();

    Long getDownStationId();
}
