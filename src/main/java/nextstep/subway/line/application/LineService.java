package nextstep.subway.line.application;

import java.util.List;
import java.util.stream.Collectors;
import nextstep.subway.common.domain.Name;
import nextstep.subway.common.exception.DuplicateDataException;
import nextstep.subway.common.exception.NotFoundException;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.domain.Section;
import nextstep.subway.line.domain.Sections;
import nextstep.subway.line.dto.LineCreateRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.line.dto.LineUpdateRequest;
import nextstep.subway.line.dto.SectionRequest;
import nextstep.subway.station.application.StationService;
import nextstep.subway.station.domain.Station;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class LineService {

    private final LineRepository repository;
    private final StationService stationService;

    public LineService(LineRepository repository,
        StationService stationService) {
        this.repository = repository;
        this.stationService = stationService;
    }

    public LineResponse saveLine(LineCreateRequest request) {
        validateDuplicateName(request.name());
        return LineResponse.from(savedLine(request));
    }

    @Transactional(readOnly = true)
    public List<LineResponse> findAll() {
        return repository.findAll()
            .stream()
            .map(LineResponse::from)
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public LineResponse findOne(Long id) {
        return LineResponse.from(line(id));
    }

    public void update(Long id, LineUpdateRequest request) {
        validateDuplicateName(request.name());
        line(id)
            .update(request.name(), request.color());
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }

    public LineResponse addSection(long id, SectionRequest request) {
        Line line = line(id);
        line.addSection(section(request));
        return LineResponse.from(line);
    }

    private Line savedLine(LineCreateRequest request) {
        return repository.save(
            Line.of(request.name(), request.color(), sections(request.getSection()))
        );
    }

    private void validateDuplicateName(Name name) {
        if (repository.existsByName(name)) {
            throw new DuplicateDataException(String.format("Name(%s) already exists", name));
        }
    }

    private Sections sections(SectionRequest request) {
        return Sections.from(section(request));
    }

    private Section section(SectionRequest request) {
        return Section.of(
            station(request.getUpStationId()),
            station(request.getDownStationId()),
            request.distance()
        );
    }

    private Station station(Long id) {
        return stationService.findStation(id);
    }

    private Line line(Long id) {
        return repository.findById(id)
            .orElseThrow(() ->
                new NotFoundException(String.format("line id(%d) does not exist", id)));
    }
}
