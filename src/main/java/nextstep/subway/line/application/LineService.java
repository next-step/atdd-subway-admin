package nextstep.subway.line.application;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.section.domain.Section;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.section.domain.SectionRepository;
import nextstep.subway.section.dto.SectionRequest;
import nextstep.subway.station.application.StationService;
import nextstep.subway.station.dto.StationResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class LineService {
    private LineRepository lineRepository;
    private SectionRepository sectionRepository;
    private StationService stationService;

    public LineService(LineRepository lineRepository, SectionRepository sectionRepository, StationService stationService) {
        this.lineRepository = lineRepository;
        this.sectionRepository = sectionRepository;
        this.stationService = stationService;
    }

    public LineResponse saveLine(LineRequest request) {
        Line line = request.toLine();
        Section section = request.toSection(line);
        line.addSection(section);
        Line persistLine = lineRepository.save(line);

        List<StationResponse> stations = stationService.getStationsFromSection(section);

        return LineResponse.of(persistLine, stations);
    }

    @Transactional(readOnly = true)
    public LineResponse findLineById(Long id) {
        Optional<Line> line = lineRepository.findById(id);
        List<StationResponse> stations = getStations(line.get());

        return LineResponse.of(line.get(), stations);
    }

    @Transactional(readOnly = true)
    public List<LineResponse> findAllLines() {
        List<Line> lines = lineRepository.findAll();
        return lines.stream()
                .map(line -> LineResponse.of(line, getStations(line)))
                .collect(Collectors.toList());
    }

    public LineResponse updateLineById(Long id, LineRequest lineRequest) {
        Optional<Line> line = lineRepository.findById(id);
        Line updateLine = new Line(lineRequest.getName(), lineRequest.getColor());
        line.get().update(updateLine);
        List<StationResponse> stations = getStations(line.get());
        return LineResponse.of(line.get(), stations);
    }

    public void deleteLineById(Long id) {
        lineRepository.deleteById(id);
    }

    public List<StationResponse> getStations(Line line) {
        List<StationResponse> stations = new ArrayList<>();
        line.getSections().getSections().stream()
                .map(stationService::getStationsFromSection)
                .forEach(stations::addAll);
        return stations;
    }

    public LineResponse addSection(Long id, SectionRequest request) {
        Line line = lineRepository.findById(id).get();
        Section section = sectionRepository.save(request.toSection(line));
        line.addSection(section);

        List<StationResponse> stations = stationService.getStationsFromSection(section);
        return LineResponse.of(line, stations);
    }
}
