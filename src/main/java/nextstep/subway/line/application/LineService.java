package nextstep.subway.line.application;

import nextstep.subway.exception.DataNotFoundException;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.line.dto.SectionRequest;
import nextstep.subway.station.application.StationService;
import nextstep.subway.station.domain.Station;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class LineService {
    public static final String LINE_NOT_FOUND_MESSAGE = "노선이 없습니다.";
    private LineRepository lineRepository;
    private StationService stationService;

    public LineService(LineRepository lineRepository, StationService stationService) {
        this.lineRepository = lineRepository;
        this.stationService = stationService;
    }

    public LineResponse saveLine(LineRequest request) {
        Station upStation = getStationById(request.getUpStationId());
        Station downStation = getStationById(request.getDownStationId());
        int distance = request.getDistance();

        Line line = request.toLine();
        line.addSection(upStation, downStation, distance);
        Line persistLine = lineRepository.save(line);
        return LineResponse.of(persistLine);
    }

    private Station getStationById(Long id) {
        return stationService.findById(id);
    }

    public List<LineResponse> findAllLines() {
        List<Line> lines = lineRepository.findAll();

        return lines.stream()
                .map(line -> LineResponse.of(line))
                .collect(Collectors.toList());
    }

    public LineResponse findById(Long id) {
        Line line = getLine(id);
        return LineResponse.of(line);
    }

    private Line getLine(Long id) {
        return lineRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException(LINE_NOT_FOUND_MESSAGE));
    }

    public LineResponse updateLine(LineRequest lineRequest, Long id) {
        Line line = getLine(id);
        line.update(lineRequest.toLine());
        return LineResponse.of(line);
    }

    public void deleteStationById(Long id) {
        lineRepository.deleteById(id);
    }

    public LineResponse addSection(SectionRequest sectionRequest, Long id) {
        Line line = getLine(id);
        Station upStation = getStationById(sectionRequest.getUpStationId());
        Station downStation = getStationById(sectionRequest.getDownStationId());
        int distance = sectionRequest.getDistance();
        line.addSection(upStation, downStation, distance);
        return LineResponse.of(line);
    }

    public void deleteSection(Long lineId, Long stationId) {
        Line line = getLine(lineId);
        Station station = getStationById(stationId);
        line.deleteSection(station);
    }
}
