package nextstep.subway.line.application;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.dto.StationResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class LineService {
    private LineRepository lineRepository;

    public LineService(LineRepository lineRepository) {
        this.lineRepository = lineRepository;
    }

    public LineResponse saveLine(LineRequest request) {
        Line persistLine = lineRepository.save(request.toLine());
        return LineResponse.of(persistLine);
    }

    public List<LineResponse> findAllLines() {
        List<Line> lines = lineRepository.findAll();
        return lines.stream()
                .map(line -> LineResponse.of(line))
                .collect(Collectors.toList());
    }

    public LineResponse findLineById(Long id) throws EntityNotFoundException {
        Line foundLine = lineRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("해당하는 지하철 노선을 찾을 수 없습니다."));
        return LineResponse.of(foundLine);
    }

    public LineResponse updateLine(Long id, LineRequest lineRequest) throws EntityNotFoundException {
        Line foundLine = lineRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("해당하는 지하철 노선을 찾을 수 없습니다."));

        foundLine.update(lineRequest.toLine());

        return LineResponse.of(foundLine);
    }
}
