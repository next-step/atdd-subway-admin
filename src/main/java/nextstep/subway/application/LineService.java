package nextstep.subway.application;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import nextstep.subway.domain.Distance;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import nextstep.subway.dto.LineRequest;
import nextstep.subway.dto.LineResponse;
import nextstep.subway.dto.SectionRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class LineService {
    private final LineRepository lineRepository;
    private final StationService stationService;
    private final LineMapper lineMapper;

    public LineService(
        LineRepository lineRepository,
        StationService stationService,
        LineMapper lineMapper
    ) {
        this.lineRepository = lineRepository;
        this.stationService = stationService;
        this.lineMapper = lineMapper;
    }

    public LineResponse saveLine(LineRequest lineRequest) {
        Station upStation = stationService.findStationByIdAsDomainEntity(lineRequest.getUpStationId());
        Station downStation = stationService.findStationByIdAsDomainEntity(lineRequest.getDownStationId());
        Line saved = lineRepository.save(lineMapper.mapToDomainEntity(lineRequest, upStation, downStation));
        return lineMapper.mapToResponse(saved);
    }

    public LineResponse updateLine(Long id, LineRequest lineRequest) {
        final Line line = findLineByIdAsDomainEntity(id);
        line.update(lineRequest.getName(), lineRequest.getColor());
        return lineMapper.mapToResponse(line);
    }

    @Transactional(readOnly = true)
    public List<LineResponse> findAllLines() {
        final List<LineResponse> result = lineRepository.findAll()
            .stream()
            .map(lineMapper::mapToResponse)
            .collect(Collectors.toList());
        return Collections.unmodifiableList(result);
    }

    public LineResponse findLineById(Long id) {
        final Line line = findLineByIdAsDomainEntity(id);
        return lineMapper.mapToResponse(line);
    }

    @Transactional(readOnly = true)
    Line findLineByIdAsDomainEntity(Long id) {
        return lineRepository.findById(id).orElseThrow(() -> new NotFoundException(id));
    }

    public void deleteLineById(Long id) {
        lineRepository.deleteById(id);
    }

    public void addLineStation(Long lineId, SectionRequest sectionRequest) {
        final Line line = findLineByIdAsDomainEntity(lineId);
        final Station upStation = stationService.findStationByIdAsDomainEntity(sectionRequest.getUpStationId());
        final Station downStation = stationService.findStationByIdAsDomainEntity(sectionRequest.getDownStationId());
        line.addSection(new Section(line, upStation, downStation, Distance.from(sectionRequest.getDistance())));
    }

}
