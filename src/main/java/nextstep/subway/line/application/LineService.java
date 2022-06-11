package nextstep.subway.line.application;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.dto.LineAddRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.line.dto.LineUpdateRequest;
import nextstep.subway.line.exception.LineException;
import nextstep.subway.line.exception.LineExceptionType;
import nextstep.subway.station.application.StationService;
import nextstep.subway.station.domain.Station;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class LineService {
    private final LineRepository lineRepository;
    private final StationService stationService;

    public LineService(final LineRepository lineRepository, final StationService stationService) {
        this.lineRepository = lineRepository;
        this.stationService = stationService;
    }

    @Transactional
    public LineResponse createLine(final LineAddRequest lineAddRequest) {
        final Station upStation = stationService.findById(lineAddRequest.getUpStationId());
        final Station downStation = stationService.findById(lineAddRequest.getDownStationId());

        return LineResponse.from(lineRepository.save(
                lineAddRequest.toEntity(upStation, downStation)
        ));
    }

    public List<LineResponse> fetchLines() {
        return lineRepository.findAll()
                .stream()
                .map(LineResponse::from)
                .collect(Collectors.toList());
    }

    public LineResponse fetchLine(final Long id) {
        return LineResponse.from(findById(id));
    }

    @Transactional
    public void updateLine(final Long id, final LineUpdateRequest lineUpdateRequest) {
        final Line line = findById(id);
        line.updateNameAndColor(lineUpdateRequest);
    }

    @Transactional
    public void deleteLine(final Long id) {
        lineRepository.deleteById(id);
    }

    public Line findById(final Long id) {
        return lineRepository.findById(id)
                .orElseThrow(() -> new LineException(LineExceptionType.NOT_FOUND_LINE));
    }
}
