package nextstep.subway.line.application;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.dto.LineCreateResponse;
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
    private final LineRepository lineRepository;

    private final StationRepository stationRepository;

    public LineService(LineRepository lineRepository, StationRepository stationRepository) {
        this.lineRepository = lineRepository;
        this.stationRepository = stationRepository;
    }

    public LineCreateResponse saveLine(LineRequest request) {
        Line line = request.toLine();

        Long upStationId = request.getUpStationId();
        if (upStationId != null) {
            Station upStation = stationRepository.findById(upStationId)
                    .orElseThrow(EntityNotFoundException::new);
            line.changeUpStation(upStation);
        }

        Long downStationId = request.getDownStationId();
        if (downStationId != null) {
            Station downStation = stationRepository.findById(downStationId)
                    .orElseThrow(EntityNotFoundException::new);
            line.changeDownStation(downStation);
        }

        Line persistLine = lineRepository.save(line);
        return LineCreateResponse.of(persistLine);
    }

    @Transactional(readOnly = true)
    public List<LineResponse> findAllLines() {
        List<Line> lines = lineRepository.findAll();
        return lines.stream()
                .map(LineResponse::of)
                .collect(Collectors.toList());
    }

    public void updateLine(Long lineId, LineRequest request) {
        Line line = lineRepository.findById(lineId)
                .orElseThrow(EntityNotFoundException::new);
        line.update(request.toLine());
    }

    @Transactional(readOnly = true)
    public LineResponse findById(Long lineId) {
        return LineResponse.of(lineRepository.getOne(lineId));
    }

    public void deleteLineById(Long lineId) {
        Line line = lineRepository.findById(lineId)
                .orElseThrow(EntityNotFoundException::new);
        lineRepository.delete(line);
    }
}
