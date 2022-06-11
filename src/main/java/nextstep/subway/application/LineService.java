package nextstep.subway.application;

import java.util.List;
import java.util.stream.Collectors;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.StationRepository;
import nextstep.subway.dto.LineRequest;
import nextstep.subway.dto.LineResponse;
import nextstep.subway.exception.InvalidLineException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class LineService {

    private static final String INVALID_LINE = "%d : 유효하지 않은 지하철 노선입니다.";
    private final LineRepository lineRepository;
    private final StationService stationService;

    public LineService(LineRepository lineRepository, StationService stationService) {
        this.lineRepository = lineRepository;
        this.stationService = stationService;
    }

    @Transactional
    public LineResponse saveLine(LineRequest lineRequest) {
        Station upStation = stationService.findStationById(lineRequest.getUpStationId());
        Station downStation = stationService.findStationById(lineRequest.getDownStationId());
        Line newLine = lineRepository.save(lineRequest.toLine(upStation, downStation));
        return LineResponse.of(newLine);
    }

    @Transactional
    public void updateLine(Long id, LineRequest lineRequest) {
        String lineName = lineRequest.getName();
        String lineColor = lineRequest.getColor();
        Line foundLine = lineRepository.findById(id).orElseThrow(
            () -> new InvalidLineException(String.format(INVALID_LINE, id))
        );
        foundLine.update(lineName, lineColor);
    }

    @Transactional(readOnly = true)
    public List<LineResponse> findAllLines() {
        List<Line> lines = lineRepository.findAll();

        return lines.stream()
            .map(line -> LineResponse.of(line))
            .collect(Collectors.toList());
    }

    @Transactional
    public void deleteLineById(Long id) {
        lineRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public LineResponse findLineById(Long id) {
        return LineResponse.of(findById(id));
    }

    @Transactional(readOnly = true)
    public Line findById(Long id) {
        return lineRepository.findById(id)
            .orElseThrow(() -> new InvalidLineException(String.format(INVALID_LINE, id)));
    }

    @Transactional
    public void deleteSection(long lineId, long stationId){
        Line findLine = lineRepository.findById(lineId)
            .orElseThrow(() -> new InvalidLineException(String.format(INVALID_LINE, lineId)));
        Station station = stationService.findStationById(stationId);
        findLine.deleteSection(station);
        lineRepository.save(findLine);
    }

}
