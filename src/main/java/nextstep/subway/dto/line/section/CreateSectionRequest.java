package nextstep.subway.dto.line.section;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import nextstep.subway.domain.line.Line;
import nextstep.subway.domain.line.Section;
import nextstep.subway.domain.station.Station;

public class CreateSectionRequest {

    private Long upStationId;
    private Long downStationId;
    private int distance;

    private CreateSectionRequest() {

    }

    public CreateSectionRequest(Long upStationId, Long downStationId, int distance) {
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }

    public Long getUpStationId() {
        return upStationId;
    }

    public Long getDownStationId() {
        return downStationId;
    }

    public int getDistance() {
        return distance;
    }

    public Section toEntity(Line line, Station upStation, Station downStation) {
        return Section.of(line, upStation, downStation, distance);
    }

    public List<Long> getQueryParams() {
        return new ArrayList<>(Arrays.asList(upStationId, downStationId));
    }

    public Station findUpStationById(List<Station> stations) {
        return stations.stream()
            .filter(it -> it.getId().equals(upStationId))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("요청에 해당하는 상행역을 찾을 수 없습니다."));
    }

    public Station findDownStationById(List<Station> stations) {
        return stations.stream()
            .filter(it -> it.getId().equals(downStationId))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("요청에 해당하는 하행역을 찾을 수 없습니다."));
    }
}
