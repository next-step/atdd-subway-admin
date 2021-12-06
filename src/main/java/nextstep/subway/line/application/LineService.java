package nextstep.subway.line.application;

import nextstep.subway.line.constants.MessageConstants;
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
        Station upStation = stationRepository.findById(request.getUpStationId())
                                            .orElseThrow(() -> new IllegalArgumentException("no such upStation with primary key " + request.getUpStationId()));
        Station downStation = stationRepository.findById(request.getDownStationId())
                                            .orElseThrow(() -> new IllegalArgumentException("no such downStation with primary key " + request.getDownStationId()));;
        return LineResponse.of(lineRepository.save(request.toLine(upStation, downStation)));
    }

    public List<LineResponse> findAllLines() {
        List<Line> lines = lineRepository.findAll();

        return lines.stream()
                    .map(LineResponse::of)
                    .collect(Collectors.toList());
    }

    public LineResponse findById(Long id) {
        return LineResponse.of(lineRepository.findById(id)
                                        .orElseThrow(() -> new IllegalArgumentException(MessageConstants.NO_SUCH_LINE + id)));
    }

    public LineResponse updateLineById(Long id, LineRequest request) {
        Line line = lineRepository.findById(id)
                                    .orElseThrow(() -> new IllegalArgumentException(MessageConstants.NO_SUCH_LINE + id));
        line.update(request.toLine());
        return LineResponse.of(lineRepository.save(line));
    }

    public void deleteLineById(Long id) {
        lineRepository.deleteById(id);
    }
}
