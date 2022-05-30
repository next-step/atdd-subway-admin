package nextstep.subway.application;

import nextstep.subway.domain.*;
import nextstep.subway.dto.LineRequest;
import nextstep.subway.dto.LineResponse;
import nextstep.subway.dto.SectionRequest;
import nextstep.subway.exception.LineNotFoundException;
import nextstep.subway.exception.StationNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class LineService {
    private LineRepository lineRepository;
    private StationRepository stationRepository;

    public LineService(LineRepository lineRepository, StationRepository stationRepository) {
        this.lineRepository = lineRepository;
        this.stationRepository = stationRepository;
    }

    @Transactional
    public LineResponse saveLine(LineRequest lineRequest) {
        Station upStation = getStation(lineRequest.getUpStationId());
        Station downStation = getStation(lineRequest.getDownStationId());
        Line persistLine = lineRepository.save(lineRequest.toLine(upStation, downStation));
        return LineResponse.of(persistLine);
    }

    public List<LineResponse> findAllLines() {
        List<Line> lines = lineRepository.findAll();

        return lines.stream()
                .map(line -> LineResponse.of(line))
                .collect(Collectors.toList());
    }

    public LineResponse findLine(Long lineId) {
        return lineRepository.findById(lineId)
                .map(line -> LineResponse.of(line))
                .orElseThrow(() -> new LineNotFoundException(lineId));
    }

    @Transactional
    public void updateLine(Long lineId, LineRequest lineRequest) {
        Line line = getLine(lineId);
        line.update(lineRequest.getName(), lineRequest.getColor());
    }

    @Transactional
    public void deleteLine(Long lineId) {
        lineRepository.deleteById(lineId);
    }

    @Transactional
    public Line addSection(Long lineId, SectionRequest request) {
        Line line = getLine(lineId);
        Station upStation = getStation(request.getUpStationId());
        Station downStation = getStation(request.getDownStationId());
        line.addSection(request.toSection(upStation, downStation));
        lineRepository.save(line);
        return line;
    }

    @Transactional
    public void removeSectionByStationId(Long lineId, Long stationId) {
        Line line = getLine(lineId);
        Station station = getStation(stationId);
        line.removeSection(station);
    }

    private Station getStation(Long stationId) {
        return stationRepository.findById(stationId)
        .orElseThrow(() -> new StationNotFoundException(stationId));
    }

    private Line getLine(Long lineId) {
        return lineRepository.findById(lineId)
                .orElseThrow(() -> new LineNotFoundException(lineId));
    }
}
