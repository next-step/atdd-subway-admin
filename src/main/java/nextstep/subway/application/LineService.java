package nextstep.subway.application;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.SectionRepository;
import nextstep.subway.domain.Station;
import nextstep.subway.dto.LineResponse;
import nextstep.subway.dto.LineUpdateRequest;
import nextstep.subway.dto.SectionCreateRequest;
import nextstep.subway.dto.SectionCreateResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.springframework.transaction.annotation.Isolation.READ_COMMITTED;

@Service
public class LineService {

    private LineRepository lineRepository;
    private StationService stationService;

    private SectionRepository sectionRepository;

    public LineService(LineRepository lineRepository, StationService stationService,
            SectionRepository sectionRepository) {
        this.lineRepository = lineRepository;
        this.stationService = stationService;
        this.sectionRepository = sectionRepository;
    }

    @Transactional(isolation = READ_COMMITTED)
    public LineResponse create(Line line) {
        line.initSectionLineUp(new Section(line));
        final Line resultLine = lineRepository.save(line);
        List<Station> stations = findStations(line);
        return LineResponse.fromLineStations(resultLine, stations);
    }

    @Transactional(readOnly = true)
    public List<LineResponse> findList() {
        return lineRepository.findAll().stream()
                .map(line -> LineResponse.fromLineStations(line, findStations(line)))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Optional<LineResponse> find(long id) {
        return lineRepository.findById(id)
                .map(line -> LineResponse.fromLineStations(line, findStations(line)));
    }

    @Transactional(isolation = READ_COMMITTED)
    public void update(long id, LineUpdateRequest request) {
        Optional<Line> line = lineRepository.findById(id);
        line.orElseThrow(IllegalArgumentException::new)
                .updateName(request.getName())
                .updateColor(request.getColor());
    }

    private List<Station> findStations(Line line) {
        return stationService.findAllById(line.getStationIds());
    }

    @Transactional(isolation = READ_COMMITTED)
    public SectionCreateResponse createSection(long id, SectionCreateRequest request) {
        Line line = lineRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("요청한 노선을 찾을 수 없습니다. 노선ID:" + id));
        line.addSection(request.toSection(line));
        refreshDistance(line);
        return SectionCreateResponse.of(line, line.getSectionList());
    }

    private void refreshDistance(Line line) {
        line.updateDistance(sectionRepository.findAllByLine(line).stream()
                .mapToInt(Section::getDistanceIntValue)
                .sum());
    }
}
