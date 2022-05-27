package nextstep.subway.application;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Station;
import nextstep.subway.dto.LineRequest;
import nextstep.subway.dto.LineResponse;
import nextstep.subway.exception.CustomException;
import nextstep.subway.exception.ErrorCode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class LineService {
    private final LineRepository lineRepository;
    private final StationService stationService;

    public LineService(LineRepository lineRepository, StationService stationService) {
        this.lineRepository = lineRepository;
        this.stationService = stationService;
    }

    @Transactional
    public LineResponse saveLine(LineRequest lineRequest) {
        Station upStation = stationService.findById(lineRequest.getUpStationId());
        Station downStation = stationService.findById(lineRequest.getDownStationId());

        Line line = lineRepository.save(new Line(lineRequest, upStation, downStation));

        return LineResponse.of(line);
    }

    @Transactional(readOnly = true)
    public List<LineResponse> findAllLines() {
        List<Line> lines = lineRepository.findAll();

        return lines.stream()
                .map(LineResponse::of)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public LineResponse findByLine(Long id) {
        Line line = findById(id);

        return LineResponse.of(line);
    }

    @Transactional
    public void updateLine(Long id, LineRequest lineRequest) {
        Line line = findById(id);
        Station upStation = getStation(lineRequest.getUpStationId());
        Station downStation = getStation(lineRequest.getDownStationId());

        line.update(lineRequest, upStation, downStation);
    }

    private Station getStation(Long stationId) {
        if (stationId == null) {
            return null;
        }
        return stationService.findById(stationId);
    }

    private Line findById(Long id) {
        return lineRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.LINE_NOT_FOUND));
    }
}
