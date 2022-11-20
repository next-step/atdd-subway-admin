package nextstep.subway.line.application;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.domain.Section;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.line.dto.LineUpdateRequest;
import nextstep.subway.line.dto.SectionRequest;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;
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
            .orElseThrow(() -> new IllegalArgumentException(String.format("역을 찾을 수 없습니다 id = %d", lineRequest.getDownStationId())));
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
        Line line = findLine(id);
        return LineResponse.from(line);
    }

    @Transactional
    public void updateLine(Long id, LineUpdateRequest lineUpdateRequest) {
        Line line = findLine(id);
        line.updateNameAndColor(lineUpdateRequest.getName(), lineUpdateRequest.getColor());
    }

    @Transactional
    public void deleteLineById(final Long id) {
        lineRepository.deleteById(id);
    }

    @Transactional
    public void addSection(Long lineId, SectionRequest request) {
        Line line = findLine(lineId);
        Station upStation = stationRepository.findById(request.getUpStationId())
            .orElseThrow(() -> new IllegalArgumentException(String.format("역을 찾을 수 없습니다 id = %d", request.getUpStationId())));
        Station downStation = stationRepository.findById(request.getDownStationId())
            .orElseThrow(() -> new IllegalArgumentException(String.format("역을 찾을 수 없습니다 id = %d", request.getDownStationId())));
        line.addSection(new Section(upStation, downStation, request.getDistance()));
    }

    private Line findLine(Long lineId) {
        return lineRepository.findById(lineId)
            .orElseThrow(() -> new IllegalArgumentException(String.format("노선을 찾을 수 없습니다 id = %d", lineId)));
    }
}
