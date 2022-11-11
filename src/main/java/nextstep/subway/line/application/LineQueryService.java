package nextstep.subway.line.application;

import nextstep.subway.station.application.StationQueryService;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.station.domain.Station;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.common.exception.DataNotFoundException;
import nextstep.subway.common.message.ExceptionMessage;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class LineQueryService {

    private static final int UPWARD_LINE = 0;
    private static final int DOWNWARD_LINE = 1;
    private final LineRepository lineRepository;
    private final StationQueryService stationQueryService;

    public LineQueryService(LineRepository lineRepository, StationQueryService stationQueryService) {
        this.lineRepository = lineRepository;
        this.stationQueryService = stationQueryService;
    }

    public List<LineResponse> findAllLines() {
        List<Line> lines = lineRepository.findAll();
        Map<Long, List<Station>> stationsByLine = stationQueryService.findStationsByLine(toLineIds(lines));

        return lines.stream()
                .map(line ->
                        LineResponse.of(
                                line,
                                stationsByLine.get(line.getId()).get(UPWARD_LINE),
                                stationsByLine.get(line.getId()).get(DOWNWARD_LINE)
                        )
                )
                .collect(Collectors.toList());
    }

    public LineResponse findLine(Long id) {
        Line line = lineRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException(ExceptionMessage.NOT_FOUND_LINE));

        List<Station> stations = stationQueryService.findByLineId(id);

        return LineResponse.of(line, stations.get(UPWARD_LINE), stations.get(DOWNWARD_LINE));
    }

    private static List<Long> toLineIds(List<Line> lines) {
        return lines.stream()
                .map(Line::getId)
                .collect(Collectors.toList());
    }
}
