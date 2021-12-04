package nextstep.subway.line.application;

import nextstep.subway.exception.NotFoundException;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.line.dto.LinesResponse;
import nextstep.subway.section.domain.Section;
import nextstep.subway.section.domain.SectionRepository;
import nextstep.subway.section.dto.SectionRequest;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;
import nextstep.subway.station.dto.StationResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Transactional
public class LineService {
    private final LineRepository lineRepository;
    private final SectionRepository sectionRepository;
    private final StationRepository stationRepository;

    public LineService(LineRepository lineRepository, SectionRepository sectionRepository, StationRepository stationRepository) {
        this.lineRepository = lineRepository;
        this.sectionRepository = sectionRepository;
        this.stationRepository = stationRepository;
    }

    public LineResponse saveLine(LineRequest request) {
        Line persistLine = lineRepository.save(request.toLine());
        Section section = sectionRepository.save(request.toSectionRequest().toSection());
        persistLine.addSection(section);

        return getLine(persistLine.getId());
    }

    public LinesResponse getLines() {
        List<Line> lines = lineRepository.findAll();
        return new LinesResponse(lines);
    }

    public LineResponse getLine(Long id) throws NotFoundException {
        Line line = lineRepository.findById(id)
                .orElseThrow(NotFoundException::new);

        Map<Long, Station> stationMap = stationRepository.findAllById(line.getStations()).stream()
                .collect(Collectors.toMap(s -> s.getId(), Function.identity()));

        return LineResponse.of(line, sortStationResponse(line, stationMap));
    }

    private List<StationResponse> sortStationResponse(Line line, Map<Long, Station> stationMap) {
        return line.getStations().stream()
                .map(s -> StationResponse.of(stationMap.get(s)))
                .collect(Collectors.toList());

    }

    public LineResponse updateLine(Long id, LineRequest request) throws NotFoundException {
        return lineRepository.findById(id)
                .map(line -> line.update(request.toLine()))
                .map(LineResponse::of)
                .orElseThrow(NotFoundException::new);
    }

    public void deleteLine(Long id) {
        lineRepository.deleteById(id);
    }

    public LineResponse addSection(Long id, SectionRequest sectionRequest) {
        Line line = lineRepository.findById(id).orElseThrow(NotFoundException::new);
        Section section = sectionRepository.save(sectionRequest.toSection());
        line.addSection(section);
        return getLine(id);
    }
}
