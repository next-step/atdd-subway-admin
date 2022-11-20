package nextstep.subway.application;

import java.util.List;
import java.util.stream.Collectors;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.StationRepository;
import nextstep.subway.dto.LineRequest;
import nextstep.subway.dto.LineResponse;
import nextstep.subway.dto.LineStationRequest;
import nextstep.subway.exception.NotFoundException;
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
    public LineResponse saveLine(LineRequest lineRequest) {
        Station upStation = stationRepository.findById(lineRequest.getUpStationId())
                .orElseThrow(NotFoundException::new);
        Station downStation = stationRepository.findById(lineRequest.getDownStationId())
                .orElseThrow(NotFoundException::new);
        Line persistLine = lineRepository.save(lineRequest.toLine(upStation, downStation));
        return LineResponse.from(persistLine);
    }

    public List<LineResponse> findAllLines() {
        List<Line> lines = lineRepository.findAll();
        return lines.stream()
                .map(LineResponse::from)
                .collect(Collectors.toList());
    }

    public LineResponse findById(Long lineId) {
        Line line = lineRepository.findById(lineId).orElseThrow(NotFoundException::new);
        return LineResponse.from(line);
    }

    @Transactional
    public void updateLineById(Long lineId, LineRequest lineRequest) {
        Line line = lineRepository.findById(lineId).orElseThrow(NotFoundException::new);
        line.updateLine(lineRequest);
    }

    @Transactional
    public void delete(Long lineId) {
        lineRepository.delete(lineRepository.findById(lineId).orElseThrow(NotFoundException::new));
    }

    @Transactional
    public void addLineStation(Long lineId, LineStationRequest lineStationRequest) {
        Station upStation = stationRepository.findById(lineStationRequest.getUpStationId())
                .orElseThrow(NotFoundException::new);
        Station downStation = stationRepository.findById(lineStationRequest.getDownStationId())
                .orElseThrow(NotFoundException::new);
        Line line = lineRepository.findById(lineId).orElseThrow(NotFoundException::new);
        line.addLineStation(
                lineStationRequest.toLineStation(line, upStation, downStation, lineStationRequest.getDistance()));
    }
}