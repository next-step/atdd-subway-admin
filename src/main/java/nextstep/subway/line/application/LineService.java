package nextstep.subway.line.application;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class LineService {

    private StationRepository stationRepository;
    private LineRepository lineRepository;

    public LineService(StationRepository stationRepository, LineRepository lineRepository) {
        this.stationRepository = stationRepository;
        this.lineRepository = lineRepository;
    }

    public LineResponse saveLine(LineRequest request) {
        // 상행 조회, 하행 조회
        Station upStation = stationRepository.findById(request.getUpStationId())
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 역입니다."));
        Station downStation = stationRepository.findById(request.getDownStationId())
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 역입니다."));

        Line persistLine = lineRepository.save(request.toLineWithStation(upStation, downStation));

        return LineResponse.of(persistLine);
    }

    @Transactional(readOnly = true)
    public List<LineResponse> findAllLines() {
        List<Line> lines = lineRepository.findAll();

        return lines.stream()
                .map(line -> LineResponse.of(line))
                .collect(Collectors.toList());
    }

    public LineResponse findById(Long id) {
        Line line = lineRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("존재하지 않는 LINE 입니다."));

        return LineResponse.of(line);
    }

    public LineResponse updateLine(Long lineId, LineRequest lineRequest) {
        Line line = lineRepository.findById(lineId).orElseThrow(() -> new EntityNotFoundException("존재하지 않는 라인입니다."));
        line.update(lineRequest.toLine());
        return LineResponse.of(line);
    }

    public void deleteLine(Long lineId) {
        lineRepository.findById(lineId).orElseThrow(() -> new EntityNotFoundException("존재하지 않는 LINE 입니다."));
        lineRepository.deleteById(lineId);
    }
}
