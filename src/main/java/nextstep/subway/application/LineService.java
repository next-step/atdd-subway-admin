package nextstep.subway.application;

import java.util.List;
import java.util.stream.Collectors;
import nextstep.subway.application.exception.NotFoundException;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.StationRepository;
import nextstep.subway.domain.line.Line;
import nextstep.subway.domain.line.LineRepository;
import nextstep.subway.dto.line.LineRequest;
import nextstep.subway.dto.line.LineResponse;
import nextstep.subway.dto.line.UpdateLineRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class LineService {
    private final LineRepository lineRepository;
    private final StationRepository stationRepository;

    public LineService(LineRepository lineRepository, StationRepository stationRepository) {
        this.lineRepository = lineRepository;
        this.stationRepository = stationRepository;
    }

    @Transactional
    public LineResponse createLine(LineRequest lineRequest) {
        Station upStation = findStationById(lineRequest.getUpStationId());
        Station downStation = findStationById(lineRequest.getDownStationId());

        return createLine(lineRequest, upStation, downStation);
    }

    private Station findStationById(Long stationId) {
        return stationRepository.findById(stationId)
                .orElseThrow(() -> new NotFoundException(String.format("입력한 역을 찾을 수 없습니다. (역: %d)", stationId)));
    }

    private LineResponse createLine(LineRequest lineRequest, Station upStation, Station downStation) {
        Line line = new Line(lineRequest.getName(), lineRequest.getColor(), upStation, downStation,
                lineRequest.getDistance());
        Line persistLine = lineRepository.save(line);

        return LineResponse.from(persistLine);
    }

    public List<LineResponse> findAllLines() {
        List<Line> lines = lineRepository.findAll();
        return lines.stream()
                .map(line -> LineResponse.from(line))
                .collect(Collectors.toList());
    }

    public LineResponse findLine(Long id) {
        Line line = findLineById(id);
        return LineResponse.from(line);
    }

    private Line findLineById(Long id) {
        return lineRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("입력한 아이디(%d)의 지하철 노선을 찾을 수 없습니다.", id)));
    }

    @Transactional
    public void updateLine(Long id, UpdateLineRequest updateLineRequest) {
        Line line = findLineById(id);
        line.update(updateLineRequest.getName(), updateLineRequest.getColor());
    }

    @Transactional
    public void deleteLine(Long id) {
        Line line = findLineById(id);
        lineRepository.delete(line);
    }
}
