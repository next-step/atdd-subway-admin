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
@Transactional(readOnly = true)
public class LineService {
    private final LineRepository lineRepository;
    private final StationService stationService;

    public LineService(LineRepository lineRepository, StationService stationService) {
        this.lineRepository = lineRepository;
        this.stationService = stationService;
    }

    @Transactional
    public LineResponse saveLine(LineRequest lineRequest) {
        Station upStation = stationService.getOrElseThrow(lineRequest.getUpStationId());
        Station downStation = stationService.getOrElseThrow(lineRequest.getDownStationId());

        Line line = lineRepository.save(new Line(lineRequest, upStation, downStation));

        return LineResponse.of(line);
    }

    public List<LineResponse> findAllLines() {
        List<Line> lines = lineRepository.findAll();

        return lines.stream()
                .map(LineResponse::of)
                .collect(Collectors.toList());
    }

    public LineResponse findByLine(Long id) {
        Line line = getOrElseThrow(id);

        return LineResponse.of(line);
    }

    @Transactional
    public void updateLine(Long id, LineRequest lineRequest) {
        Line line = getOrElseThrow(id);
        Station upStation = stationService.getOrElseThrow(lineRequest.getUpStationId());
        Station downStation = stationService.getOrElseThrow(lineRequest.getDownStationId());

        line.update(lineRequest, upStation, downStation);
    }

    @Transactional
    public void deleteStationById(Long id) {
        lineRepository.deleteById(id);
    }

    private Line getOrElseThrow(Long id) {
        if (id == null) {
            return null;
        }
        return lineRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.LINE_NOT_FOUND));
    }
}
