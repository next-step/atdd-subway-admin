package nextstep.subway.line.application;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.domain.Name;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.station.application.StationService;
import nextstep.subway.station.domain.Station;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.toList;

@Service
@Transactional(readOnly = true)
public class LineService {
    private static final String EXIST_LINE = "이미 존재하는 노선입니다 :";
    private static final String LINE_NOT_EXISTED = "노선이 존재하지 않습니다 :";
    private final LineRepository lineRepository;
    private final StationService stationService;

    public LineService(LineRepository lineRepository, StationService stationService) {
        this.lineRepository = lineRepository;
        this.stationService = stationService;
    }

    /**
     * final Station upStation = findStation(dto.getUpStationId());
     * final Station downStation = findStation(dto.getDownStationId());
     * sectionRepository.save(new Section(dto.getLine(), upStation, downStation, dto.getDistance()));
     */

    @Transactional
    public LineResponse saveLine(LineRequest request) {
        Optional<Line> findLine = lineRepository.findByName(new Name(request.getName()));
        if (findLine.isPresent()) {
            throw new LineDuplicatedException(EXIST_LINE + request.getName());
        }
        Station upStation = stationService.findStation(request.getUpStationId());
        Station downStation = stationService.findStation(request.getDownStationId());

        Line persistLine = lineRepository.save(request.toLine(upStation, downStation));
        return LineResponse.of(persistLine);
    }

    public List<LineResponse> getLines() {
        return lineRepository.findAll()
                .stream()
                .map(LineResponse::of)
                .collect(toList());
    }

    public LineResponse getLine(Long lineId) {
        final Line line = lineRepository.findById(lineId)
                .orElseThrow(() -> new LineNotFoundException(LINE_NOT_EXISTED + lineId));
        return LineResponse.of(line);
    }

    @Transactional
    public void updateLine(Long lineId, LineRequest request) {
        final Line line = lineRepository.findById(lineId)
                .orElseThrow(() -> new LineNotFoundException(LINE_NOT_EXISTED + lineId));
        line.update(request.toLine());
    }

    @Transactional
    public void deleteLine(long lineId) {
        final Line line = lineRepository.findById(lineId)
                .orElseThrow(() -> new LineNotFoundException(LINE_NOT_EXISTED + lineId));
        lineRepository.delete(line);
    }
}
