package nextstep.subway.application;

import java.util.List;
import java.util.stream.Collectors;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.LineStation;
import nextstep.subway.domain.StationRepository;
import nextstep.subway.dto.LineRequest;
import nextstep.subway.dto.LineResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class LineService {
    private final LineRepository lineRepository;
    private final StationRepository stationRepository;

    public LineService(final LineRepository lineRepository,
                       final StationRepository stationRepository) {
        this.lineRepository = lineRepository;
        this.stationRepository = stationRepository;
    }

    @Transactional
    public LineResponse saveLine(final LineRequest request) {
        final Line line = request.toLine();
        line.relateToStation(new LineStation(line, stationRepository.getById(request.getUpStationId())));
        line.relateToStation(new LineStation(line, stationRepository.getById(request.getDownStationId())));
        lineRepository.save(line);
        return LineResponse.of(line);
    }

    public List<LineResponse> findAllLines() {
        return lineRepository.findAll().stream()
                .map(line -> LineResponse.of(line))
                .collect(Collectors.toList());
    }

    public LineResponse findLineById(final Long id) {
        final Line line = validateAndFind(id);
        return LineResponse.of(line);
    }

    @Transactional
    public void modifyLine(final Long id, final LineRequest lineRequest) {
        final Line line = validateAndFind(id);
        line.update(lineRequest.getName(), lineRequest.getColor());
        lineRepository.save(line);
    }

    private Line validateAndFind(Long id) {
        return lineRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("노선 아이디가 존재하지 않습니다."));
    }
}
