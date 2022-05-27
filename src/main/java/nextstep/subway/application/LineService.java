package nextstep.subway.application;

import javassist.NotFoundException;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Station;
import nextstep.subway.dto.LineRequest;
import nextstep.subway.dto.LineResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class LineService {
    public static final String LINE_NOT_FOUND = "노선을 찾을 수 없습니다.";
    private final LineRepository lineRepository;
    private final StationService stationService;

    public LineService(LineRepository lineRepository, StationService stationService) {
        this.lineRepository = lineRepository;
        this.stationService = stationService;
    }

    @Transactional
    public LineResponse saveLine(LineRequest lineRequest) throws NotFoundException {
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
    public LineResponse findByLine(Long id) throws NotFoundException {
        Line line = findById(id);

        return LineResponse.of(line);
    }

    @Transactional
    public void updateLine(Long id, LineRequest lineRequest) throws NotFoundException {
        Line line = findById(id);
        Station upStation = getStation(lineRequest.getUpStationId());
        Station downStation = getStation(lineRequest.getDownStationId());

        line.update(lineRequest, upStation, downStation);
    }

    private Station getStation(Long stationId) throws NotFoundException {
        if (stationId == null) {
            return null;
        }
        return stationService.findById(stationId);
    }

    private Line findById(Long id) throws NotFoundException {
        return lineRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(LINE_NOT_FOUND));
    }
}
