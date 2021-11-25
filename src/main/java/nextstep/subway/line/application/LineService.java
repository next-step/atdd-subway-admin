package nextstep.subway.line.application;

import nextstep.subway.line.application.dto.LineUpdateRequest;
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
@Transactional(readOnly = true)
public class LineService {
    private final LineRepository lineRepository;
    private final StationRepository stationRepository;

    public LineService(LineRepository lineRepository, StationRepository stationRepository) {
        this.lineRepository = lineRepository;
        this.stationRepository = stationRepository;
    }

    @Transactional
    public LineResponse saveLine(LineRequest request) {
        Station upStation = findStation(request.getUpStationId());
        Station downStation = findStation(request.getDownStationId());

        Line persistLine = lineRepository.save(request.toLine(upStation, downStation));
        return LineResponse.of(persistLine);
    }

    public List<LineResponse> findLines() {
        List<Line> lines = lineRepository.findAll();
        return lines.stream()
                .map(line -> LineResponse.of(line))
                .collect(Collectors.toList());
    }

    public LineResponse findLine(Long lineId) {
        Line line = lineRepository.findById(lineId)
                .orElseThrow(() ->
                        new EntityNotFoundException("노선이 존재하지 않습니다."));

        return LineResponse.of(line);
    }

    @Transactional
    public LineResponse update(LineUpdateRequest lineUpdateRequest) {
        Line line = lineRepository.findById(lineUpdateRequest.getId())
                .orElseThrow(() ->
                        new EntityNotFoundException("노선이 존재하지 않습니다."));
        line.update(lineUpdateRequest.getName(), lineUpdateRequest.getColor());

        return LineResponse.of(line);
    }

    @Transactional
    public void delete(Long lineId) {
        lineRepository.deleteById(lineId);
    }

    private Station findStation(Long id) {
        return stationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException());
    }
}
