package nextstep.subway.application;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Station;
import nextstep.subway.dto.LineRequest;
import nextstep.subway.dto.LineResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class LineService {
    private final LineRepository lineRepository;
    private final StationService stationService;
    private final LineMapper lineMapper;

    public LineService(
        LineRepository lineRepository,
        StationService stationService,
        LineMapper lineMapper,
        StationMapper stationMapper
    ) {
        this.lineRepository = lineRepository;
        this.stationService = stationService;
        this.lineMapper = lineMapper;
    }

    @Transactional
    public LineResponse saveLine(LineRequest lineRequest) {
        Station upStation =  findStationById(lineRequest.getUpStationId());
        Station downStation = findStationById(lineRequest.getDownStationId());
        Line saved = lineRepository.save(lineMapper.mapToDomainEntity(lineRequest, upStation, downStation));
        return lineMapper.mapToResponse(saved);
    }

    @Transactional
    public LineResponse updateLine(Long id, LineRequest lineRequest) {
        final Line line = lineRepository.findById(id)
            .orElseThrow(()-> new NotFoundException(id));
        line.update(lineRequest.getName(), lineRequest.getColor());
        return lineMapper.mapToResponse(line);
    }

    public List<LineResponse> findAllLines() {
        final List<LineResponse> result = lineRepository.findAll()
            .stream()
            .map(lineMapper::mapToResponse)
            .collect(Collectors.toList());
        return Collections.unmodifiableList(result);
    }

    public LineResponse findLineById(Long id) {
        final Line line = lineRepository.findById(id)
            .orElseThrow(()-> new NotFoundException(id));
        return lineMapper.mapToResponse(line);
    }

    @Transactional
    public void deleteLineById(Long id) {
        lineRepository.deleteById(id);
    }

    private Station findStationById(Long id) {
        return stationService.findStationByIdAsDomainEntity(id);
    }
}
