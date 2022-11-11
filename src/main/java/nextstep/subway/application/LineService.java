package nextstep.subway.application;

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
        Station upStation = stationService.findById(lineRequest.getUpStationId());
        Station downStation = stationService.findById(lineRequest.getDownStationId());
        Line line = Line.of(lineRequest.getName(), lineRequest.getColor(), upStation, downStation);
        return LineResponse.of(lineRepository.save(line));
    }

    public List<LineResponse> findAllLines() {
        List<Line> lines = lineRepository.findAll();

        return lines.stream()
                .map(LineResponse::of)
                .collect(Collectors.toList());
    }

    public LineResponse findLine(final Long id) {
        Line persistLine = lineRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(id + "번 노선을 찾을 수 없습니다."));
        return LineResponse.of(persistLine);
    }

    @Transactional
    public void deleteLineById(Long id) {
        lineRepository.deleteById(id);
    }

    @Transactional
    public LineResponse modify(final Long id, final LineRequest lineRequest) {
        Line persistLine = lineRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(id + "번 노선을 찾을 수 없습니다."));
        persistLine.changeName(lineRequest.getName());
        persistLine.changeColor(lineRequest.getColor());
        return LineResponse.of(persistLine);
    }
}
