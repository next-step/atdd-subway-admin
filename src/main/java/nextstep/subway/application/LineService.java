package nextstep.subway.application;

import nextstep.subway.domain.Distance;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.SectionRepository;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.Stations;
import nextstep.subway.dto.LineCreateRequest;
import nextstep.subway.dto.LineResponse;
import nextstep.subway.dto.LineUpdateRequest;
import nextstep.subway.dto.SectionCreateRequest;
import nextstep.subway.dto.SectionCreateResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
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
    public LineResponse create(LineCreateRequest request) {
        Line line = request.toLine();
        long upStationId = request.getUpStationId();
        long downStationId = request.getDownStationId();

        Stations upDownStations = findStations(Arrays.asList(upStationId, downStationId));
        line.addSection(new Section(upDownStations.findById(upStationId),
                upDownStations.findById(downStationId),
                new Distance(request.getDistance()), line));

        final Line resultLine = lineRepository.save(line);
        return LineResponse.fromLineStations(resultLine, line.getStationsInOrder());
    }

    @Transactional(readOnly = true)
    public List<LineResponse> findList() {
        return lineRepository.findAll().stream()
                .map(line -> LineResponse.fromLineStations(line, line.getStationsInOrder()))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Optional<LineResponse> find(long id) {
        return lineRepository.findById(id)
                .map(line -> LineResponse.fromLineStations(line, line.getStationsInOrder()));
    }

    @Transactional(isolation = READ_COMMITTED)
    public void update(long id, LineUpdateRequest request) {
        Optional<Line> line = lineRepository.findById(id);
        line.orElseThrow(IllegalArgumentException::new)
                .updateName(request.getName())
                .updateColor(request.getColor());
    }

    @Transactional(isolation = READ_COMMITTED)
    public SectionCreateResponse createSection(long id, SectionCreateRequest request) {
        Line line = lineRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("요청한 노선을 찾을 수 없습니다. 노선ID:" + id));
        long upStationId = request.getUpStationId();
        long downStationId = request.getDownStationId();

        Stations upDownStations = findStations(Arrays.asList(upStationId, downStationId));

        line.addSection(new Section(upDownStations.findById(upStationId),
                upDownStations.findById(downStationId),
                new Distance(request.getDistance()), line));

        return SectionCreateResponse.of(line, line.getSectionLineUpInOrder());
    }

    public SectionCreateResponse deleteSection(long lineId, long stationId) {
        Line line = lineRepository.findById(lineId)
                .orElseThrow(() -> new IllegalArgumentException("요청한 노선을 찾을 수 없습니다. 노선ID:" + lineId));
        Station station = stationService.findById(stationId);
        line.deleteSection(station);
        stationService.deleteIfNotContainsAnySection(station);
        return SectionCreateResponse.of(line, line.getSectionLineUpInOrder());
    }

    private Stations findStations(List<Long> stationIds) {
        return stationService.findAllById(stationIds);
    }
}
