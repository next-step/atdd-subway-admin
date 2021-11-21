package nextstep.subway.line.application;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class LineService {
    private final LineRepository lineRepository;

    public LineService(LineRepository lineRepository) {
        this.lineRepository = lineRepository;
    }

    public LineResponse saveLine(LineRequest request) {
        Line persistLine = lineRepository.save(request.toLine());
        return LineResponse.of(persistLine);
    }

    public List<LineResponse> findAllLines() {
        List<Line> lines = lineRepository.findAll();

        List<LineResponse> response = new ArrayList<>();
        for (Line line : lines) {
            response.add(LineResponse.of(line));
        }
        return response;
    }

    public LineResponse findById(Long lineId) {
        Line line = lineRepository.findById(lineId).orElseThrow(() -> new RuntimeException("존재하지 않는 노선ID 입니다."));
        return LineResponse.of(line);
    }
}
