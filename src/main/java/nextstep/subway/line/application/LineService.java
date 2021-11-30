package nextstep.subway.line.application;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.domain.Section;
import nextstep.subway.line.dto.*;
import nextstep.subway.station.application.StationService;
import nextstep.subway.station.domain.Station;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static nextstep.subway.line.application.exception.LineNotFoundException.error;

@Service
@Transactional(readOnly = true)
public class LineService {

    public static final String NOT_FOUND_LINE = "지하철 노선을 찾을 수 없습니다.";

    private final LineRepository lineRepository;
    private final StationService stationService;

    public LineService(LineRepository lineRepository, StationService stationService) {
        this.lineRepository = lineRepository;
        this.stationService = stationService;
    }

    @Transactional
    public LineCreateResponse saveLine(LineRequest request) {
        Station upStation = stationService.findById(request.getUpStationId());
        Station downStation = stationService.findById(request.getDownStationId());

        Line persistLine = lineRepository.save(request.toLine(upStation, downStation));
        return LineCreateResponse.of(persistLine);
    }

    public List<LineResponse> findAllLines() {
        List<Line> lines = lineRepository.findAll();
        return LineResponse.ofList(lines);
    }

    public LineResponse findOne(Long id) {
        return LineResponse.of(findLine(id));
    }

    @Transactional
    public void updateLine(Long id, LineRequest request) {
        Line line = findLine(id);
        line.update(request.toLine());
    }

    @Transactional
    public void deleteLind(Long id) {
        lineRepository.deleteById(id);
    }

    @Transactional
    public LineCreateResponse addSection(Long id, SectionRequest request) {
        Line line = findLine(id);
        Station upStation = stationService.findById(request.getUpStationId());
        Station downStation = stationService.findById(request.getDownStationId());

        Section section = request.toSection(upStation, downStation);
        line.addSection(section);
        return LineCreateResponse.of(line);
    }

    public List<SectionResponse> findSection(Long id) {
        Line line = findLine(id);
        List<Section> sections = line.getSectionsInOrder();
        return sections.stream()
                .map(section -> SectionResponse.of(section.getUpStation(), section.getDownStation(), section.getDistance()))
                .collect(Collectors.toList());
    }

    private Line findLine(Long id) {
        return lineRepository.findById(id)
                .orElseThrow(() -> error(NOT_FOUND_LINE));
    }
}