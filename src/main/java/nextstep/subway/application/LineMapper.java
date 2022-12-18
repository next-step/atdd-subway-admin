package nextstep.subway.application;

import java.util.stream.Collectors;
import nextstep.subway.domain.Distance;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import nextstep.subway.dto.LineRequest;
import nextstep.subway.dto.LineResponse;
import org.springframework.stereotype.Component;

@Component
class LineMapper {
    private final StationMapper stationMapper;

    LineMapper(StationMapper stationMapper) {
        this.stationMapper = stationMapper;
    }

    Line mapToDomainEntity(LineRequest request, Station upStation, Station downStation) {
        Section section = new Section(upStation, downStation, Distance.from(request.getDistance()));
        return new Line(request.getName(), request.getColor(), section);
    }

    LineResponse mapToResponse(Line line) {
        return new LineResponse(
            line.getId(),
            line.getName(),
            line.getColor(),
            line.getStations().stream().map(stationMapper::mapToResponse).collect(Collectors.toList()),
            line.getCreatedDate(),
            line.getModifiedDate()
        );
    }
}
