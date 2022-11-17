package nextstep.subway.application;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.StationRepository;
import nextstep.subway.dto.LineRequest;
import nextstep.subway.dto.LineResponse;
import nextstep.subway.dto.LineUpdateRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class LineService {

    private final LineRepository lineRepository;
    private final StationRepository stationRepository;

    public LineService(LineRepository lineRepository, StationRepository stationRepository) {
        this.lineRepository = lineRepository;
        this.stationRepository = stationRepository;
    }

    @Transactional
    public LineResponse createLine(LineRequest lineRequest) {
        Station upStation = stationRepository.findById(lineRequest.getUpStationId())
            .orElseThrow(() -> new IllegalArgumentException(String.format("역을 찾을 수 없습니다 id = %d", lineRequest.getUpStationId())));
        Station downStation = stationRepository.findById(lineRequest.getDownStationId())
            .orElseThrow(() -> new IllegalArgumentException(String.format("역을 찾을 수 없습니다 id = %d", lineRequest.getUpStationId())));
        Line saveLine = lineRepository.save(lineRequest.toLine(upStation, downStation));
        return LineResponse.from(saveLine);
    }

    public List<LineResponse> findAllLines() {
        List<Line> lines = lineRepository.findAll();

        return lines.stream()
            .map(line -> LineResponse.from(line))
            .collect(Collectors.toList());
    }

    public LineResponse findById(Long id) {
        Line line = lineRepository.findById(id).orElseThrow(() -> new IllegalArgumentException(String.format("노선을 찾을 수 없습니다 id = %d", id)));
        return LineResponse.from(line);
    }

    @Transactional
    public void updateLine(Long id, LineUpdateRequest lineUpdateRequest) {
        Line line = lineRepository.findById(id).orElseThrow(() -> new IllegalArgumentException(String.format("노선을 찾을 수 없습니다 id = %d", id)));
        line.updateNameAndColor(lineUpdateRequest.getName(), lineUpdateRequest.getColor());
    }

    @Transactional
    public void deleteLineById(final Long id) {
        lineRepository.deleteById(id);
    }
}
