package nextstep.subway.line.application;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class LineService {
    private LineRepository lineRepository;
    private StationRepository stationRepository;

    public LineService(LineRepository lineRepository, StationRepository stationRepository) {
        this.lineRepository = lineRepository;
        this.stationRepository = stationRepository;
    }

    public LineResponse saveLine(LineRequest request) {
        Station upStation = findStationById(request.getUpStationId());
        Station downStation = findStationById(request.getDownStationId());
        Line persistLine = lineRepository.save(request.toLine(upStation, downStation));
        return LineResponse.of(persistLine);
    }

    @Transactional(readOnly = true)
    public LineResponse findLine(Long id) {
        return LineResponse.of(findLineById(id));
    }

    @Transactional(readOnly = true)
    public List<LineResponse> findAllLines() {
        return lineRepository.findAll().stream()
                .map(LineResponse::of)
                .collect(Collectors.toList());
    }

    public LineResponse updateLine(Long id, LineRequest lineRequest) {
        Line line = findLineById(id);
        line.update(lineRequest.toLine());
        return LineResponse.of(line);
    }

    public void deleteLine(Long id) {
        Line line = findLineById(id);
        lineRepository.deleteById(line.getId());
    }

    public Station findStationById(Long stationId) {
        return stationRepository.findById(stationId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 역아이디 입니다."));
    }

    public Line findLineById(Long id) {
        return lineRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 노선아이디 입니다."));
    }
}
