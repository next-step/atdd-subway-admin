package nextstep.subway.line.application;

import nextstep.subway.common.exception.DuplicateEntityException;
import nextstep.subway.common.exception.InvalidEntityRequiredException;
import nextstep.subway.common.exception.NotFoundEntityException;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.station.application.StationService;
import nextstep.subway.station.domain.Station;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class LineService {
    private final LineRepository lineRepository;
    private final StationService stationService;

    public LineService(LineRepository lineRepository, StationService stationService) {
        this.lineRepository = lineRepository;
        this.stationService = stationService;
    }

    public LineResponse saveLine(LineRequest request) {
        validateLine(request);

        Station upStation = stationService.findStationById(request.getUpStationId());
        Station downStation = stationService.findStationById(request.getDownStationId());
        Line persistLine = lineRepository.save(request.toLine(upStation, downStation));

        return LineResponse.of(persistLine);
    }

    @Transactional(readOnly = true)
    public List<LineResponse> findAllLines() {
        List<Line> lines = lineRepository.findAll();

        return LineResponse.listOf(lines);
    }

    @Transactional(readOnly = true)
    public Line findLineById(Long id) {
        return lineRepository.findById(id)
                .orElseThrow(() -> new NotFoundEntityException(id));
    }

    public void deleteLineById(Long id) {
        lineRepository.deleteById(id);
    }

    private void validateLine(LineRequest request) {
        if (request.getUpStationId() == null || request.getDownStationId() == null) {
            throw new InvalidEntityRequiredException(request.getName());
        }

        if (lineRepository.existsByName(request.getLineName())) {
            throw new DuplicateEntityException();
        }
    }
}
