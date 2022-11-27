package nextstep.subway.line;

import nextstep.subway.section.Distance;
import nextstep.subway.section.Section;
import nextstep.subway.section.CreateSectionRequest;
import nextstep.subway.station.Station;
import nextstep.subway.station.StationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
class LineService {

    private final LineRepository lineRepository;

    private final StationRepository stationRepository;

    public LineService(LineRepository lineRepository, StationRepository stationRepository) {
        this.lineRepository = lineRepository;
        this.stationRepository = stationRepository;
    }

    @Transactional
    public LineResponse registerSection(long id, CreateSectionRequest request) {
        Line line = lineRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 노선입니다."));
        Station upStation = findStationById(request.getUpStationId());
        Station downStation = findStationById(request.getDownStationId());
        Distance distance = new Distance(request.getDistance());
        Section section = new Section(upStation, downStation, distance);
        line.addSection(section);
        return LineResponse.of(line);
    }

    @Transactional
    public LineResponse register(CreateLineRequest request) {
        Line line = request.toLine();
        Station upStation = findStationById(request.getUpStationId());
        Station downStation = findStationById(request.getDownStationId());
        Distance distance = new Distance(request.getDistance());
        Section section = new Section(upStation, downStation, distance);
        line.addSection(section);
        return LineResponse.of(lineRepository.save(line));
    }

    @Transactional
    public void update(long id, UpdateLineRequest request) {
        Line line = findLineById(id);
        line.update(request.toLine());
    }

    @Transactional(readOnly = true)
    public List<LineResponse> fetchAllLines() {
        List<Line> lines = lineRepository.findAll();

        return lines.stream()
                .map(LineResponse::of)
                .collect(Collectors.toList());
    }

    private Station findStationById(long id) {
        return stationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 역은 존재하지 않습니다."));
    }

    @Transactional(readOnly = true)
    public LineResponse fetchLine(long id) {
        Line line = findLineById(id);
        return LineResponse.of(line);
    }

    private Line findLineById(long id) {
        return lineRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 노선은 존재하지 않습니다."));
    }

    @Transactional
    public void delete(long id) {
        lineRepository.deleteById(id);
    }
}
