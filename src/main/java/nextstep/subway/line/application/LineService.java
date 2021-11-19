package nextstep.subway.line.application;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.domain.Lines;
import nextstep.subway.line.dto.LineFindResponse;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class LineService {

    private LineRepository lineRepository;

    public LineService(LineRepository lineRepository) {
        this.lineRepository = lineRepository;
    }

    public LineResponse saveLine(LineRequest request) {
        validateDuplicatedLineName(request);
        Line persistLine = lineRepository.save(request.toLine());
        return LineResponse.of(persistLine);
    }

    public List<LineFindResponse> findAll() {
        Lines lines = Lines.of(lineRepository.findAll());
        return lines.toLineFindResponses();
    }

    private void validateDuplicatedLineName(LineRequest request) {
        String requestLineName = request.getName();
        if (lineRepository.findByName(requestLineName).isPresent()) {
            throw new IllegalArgumentException("이미 존재하는 노선 이름입니다. (입력값: " + requestLineName + ")");
        }
    }
}
