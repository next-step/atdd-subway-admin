package nextstep.subway.application;

import java.util.List;
import java.util.stream.Collectors;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.LineStation;
import nextstep.subway.domain.Station;
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
        final Station upStation = getStationOrElseThrow(request.getUpStationId());
        final Station downStation = getStationOrElseThrow(request.getDownStationId());
        line.relateToStation(new LineStation(line, upStation));
        line.relateToStation(new LineStation(line, downStation, upStation));
        lineRepository.save(line);
        return LineResponse.of(line);
    }

    public List<LineResponse> findAllLines() {
        return lineRepository.findAll().stream()
                .map(line -> LineResponse.of(line))
                .collect(Collectors.toList());
    }

    public LineResponse findLineById(final Long id) {
        final Line line = getLineOrElseThrow(id);
        return LineResponse.of(line);
    }

    @Transactional
    public void modifyLine(final Long id, final LineRequest lineRequest) {
        final Line line = getLineOrElseThrow(id);
        line.update(lineRequest.getName(), lineRequest.getColor());
        lineRepository.save(line);
    }

    @Transactional
    public void deleteLineById(final Long id) {
        final Line line = getLineOrElseThrow(id);
        lineRepository.delete(line);
    }

    private Line getLineOrElseThrow(final Long id) {
        return lineRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(String.format("지하철 노선 아이디가 유효하지 않습니다: %d}", id)));
    }

    private Station getStationOrElseThrow(final Long id) {
        return stationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(String.format("지하철역 아이디가 유효하지 않습니다: %d}", id)));
    }
}
