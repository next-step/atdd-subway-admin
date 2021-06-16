package nextstep.subway.line.application;

import nextstep.subway.common.exceptionAdvice.exception.LineNotFoundException;
import nextstep.subway.common.exceptionAdvice.exception.StationNotFoundException;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class LineService {
    private LineRepository lineRepository;
    private StationRepository stationRepository;

    public LineService(LineRepository lineRepository, StationRepository stationRepository) {
        this.lineRepository = lineRepository;
        this.stationRepository = stationRepository;
    }

    public LineResponse saveLine(LineRequest lineRequest) {
        Station upStation = stationRepository.findById(lineRequest.getUpStationId()).orElseThrow(() -> new StationNotFoundException(lineRequest.getUpStationId()));
        Station downStation = stationRepository.findById(lineRequest.getDownStationId()).orElseThrow(() -> new StationNotFoundException(lineRequest.getDownStationId()));
        Line persistLine = lineRepository.save(lineRequest.toLine(upStation, downStation));
        return LineResponse.of(persistLine);
    }

    @Transactional(readOnly = true)
    public List<LineResponse> findAllLines() {
        List<Line> lines = lineRepository.findAll();
        return lines.stream()
                .map(line -> LineResponse.of(line))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public LineResponse findById(Long id) {
        Line line = lineRepository.findById(id).orElseThrow(() -> new LineNotFoundException(id));
        return LineResponse.of(line);
    }

    public LineResponse updateLine(LineRequest lineRequest, Long id) {
        Station upStation = stationRepository.findById(lineRequest.getUpStationId()).orElseThrow(() -> new StationNotFoundException(lineRequest.getUpStationId()));
        Station downStation = stationRepository.findById(lineRequest.getDownStationId()).orElseThrow(() -> new StationNotFoundException(lineRequest.getDownStationId()));
        Line line = lineRepository.findById(id).orElseThrow(() -> new LineNotFoundException(id));
        line.updateLine(lineRequest.toLine(upStation, downStation));
        return LineResponse.of(line);
    }

    public void deleteLineById(Long id) {
        lineRepository.deleteById(id);
    }
}
