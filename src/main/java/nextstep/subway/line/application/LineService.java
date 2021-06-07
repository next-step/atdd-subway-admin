package nextstep.subway.line.application;

import java.util.List;

import nextstep.subway.line.application.exceptions.AlreadyExistsLineNameException;
import nextstep.subway.line.application.exceptions.NotFoundLineException;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.station.application.StationService;
import nextstep.subway.station.domain.Station;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class LineService {
    private final LineRepository lineRepository;
    private final StationService stationService;

    public LineService(LineRepository lineRepository, StationService stationService) {
        this.lineRepository = lineRepository;
        this.stationService = stationService;
    }

    public LineResponse saveLine(LineRequest lineRequest) {
        lineRequest.validateSaveRequest();
        validateNonDuplicate(lineRequest.getName());
        Station upStation = stationService.findById(lineRequest.getUpStationId());
        Station downStation = stationService.findById(lineRequest.getDownStationId());
        Line line = Line.of(upStation, downStation, lineRequest.getName(), lineRequest.getColor(),
            lineRequest.getDistance());
        return LineResponse.of(lineRepository.save(line));
    }

    @Transactional(readOnly = true)
    public List<Line> findAllLines() {
        return lineRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Line findLineById(Long lineId) {
        return lineRepository.findWithSectionsById(lineId)
            .orElseThrow(() -> new NotFoundLineException(String.format("노선이 존재하지 않습니다.[%s]", lineId)));
    }

    public void updateLineById(Long lineId, LineRequest lineRequest) {
        Line line = findLineById(lineId);
        line.update(lineRequest.getName(), lineRequest.getColor());
    }

    private void validateNonDuplicate(String name) {
        if (lineRepository.existsByName(name)) {
            throw new AlreadyExistsLineNameException(String.format("노선 이름이 이미 존재합니다.[%s]", name));
        }
    }

    public void deleteLineById(Long lineId) {
        lineRepository.deleteById(lineId);
    }
}
