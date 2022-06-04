package nextstep.subway.line.apllication;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.line.exception.LineNotFoundException;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;
import nextstep.subway.station.exception.StationNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
@Service
public class LineService {
    private final LineRepository lineRepository;
    private final StationRepository stationRepository;

    public LineService(LineRepository lineRepository, StationRepository stationRepository) {
        this.lineRepository = lineRepository;
        this.stationRepository = stationRepository;
    }

    public LineResponse create(LineRequest lineRequest) {
        Station upStation = stationRepository.findById(lineRequest.getUpStationId())
                .orElseThrow(StationNotFoundException::new);

        Station downStation = stationRepository.findById(lineRequest.getDownStationId())
                .orElseThrow(StationNotFoundException::new);

        Line line = lineRepository.save(new Line(lineRequest.getName(), upStation, downStation,
                lineRequest.getDistance(), lineRequest.getColor()));

        return LineResponse.of(line);
    }

    @Transactional(readOnly = true)
    public List<LineResponse> getLines() {
        List<Line> lines = lineRepository.findAll();
        return LineResponse.of(lines);
    }

    @Transactional(readOnly = true)
    public LineResponse getLine(Long id) {
        Line line = findLineById(id);
        return LineResponse.of(line);
    }

    public LineResponse updateLine(Long id, LineRequest lineRequest) {
        Line line = findLineById(id);
        line.updateName(lineRequest.getName());
        return LineResponse.of(line);
    }

    public void deleteLine(Long id) {
        Line line = findLineById(id);
        lineRepository.delete(line);
    }

    private Line findLineById(Long id) {
        return lineRepository.findById(id).orElseThrow(LineNotFoundException::new);
    }
}
