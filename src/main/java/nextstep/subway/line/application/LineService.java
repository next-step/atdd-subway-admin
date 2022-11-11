package nextstep.subway.line.application;

import nextstep.subway.station.application.StationService;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.station.domain.Station;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.line.dto.LineUpdateRequest;
import nextstep.subway.common.exception.DataNotFoundException;
import nextstep.subway.common.message.ExceptionMessage;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class LineService {

    private static final int UPWARD_LINE = 0;
    private static final int DOWNWARD_LINE = 1;
    private final LineRepository lineRepository;
    private final StationService stationService;

    public LineService(LineRepository lineRepository, StationService stationService) {
        this.lineRepository = lineRepository;
        this.stationService = stationService;
    }

    @Transactional
    public LineResponse saveLine(LineRequest lineRequest) {
        Line line = lineRepository.save(lineRequest.toLine());

        Station upStation = stationService.addToLine(lineRequest.getUpStationId(), line);
        Station downStation = stationService.addToLine(lineRequest.getDownStationId(), line);

        return LineResponse.from(line, upStation, downStation);
    }

    public List<LineResponse> findAllLines() {
        List<Line> lines = lineRepository.findAll();
        Map<Long, List<Station>> stationsByLine = stationService.findStationsByLine(toLineIds(lines));

        return lines.stream()
                .map(line ->
                        LineResponse.from(
                                line,
                                stationsByLine.get(line.getId()).get(UPWARD_LINE),
                                stationsByLine.get(line.getId()).get(DOWNWARD_LINE)
                        )
                )
                .collect(Collectors.toList());
    }

    public LineResponse findLine(Long id) {
        Line line = findById(id);
        List<Station> stations = stationService.findByLineId(id);
        return LineResponse.from(line, stations.get(UPWARD_LINE), stations.get(DOWNWARD_LINE));
    }

    @Transactional
    public void updateLine(Long id, LineUpdateRequest lineRequest) {
        Line line = findById(id);
        line.update(lineRequest.getName(), line.getColor());
    }

    @Transactional
    public void deleteLine(Long id) {
        stationService.removeFromLine(id);
        lineRepository.deleteById(id);
    }

    private Line findById(Long id) {
        return lineRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException(ExceptionMessage.NOT_FOUND_LINE));
    }

    private static List<Long> toLineIds(List<Line> lines) {
        return lines.stream()
                .map(Line::getId)
                .collect(Collectors.toList());
    }
}
