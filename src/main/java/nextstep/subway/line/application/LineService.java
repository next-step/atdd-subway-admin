package nextstep.subway.line.application;

import nextstep.subway.common.exception.NotFoundResourceException;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
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
        validateDuplicatedLineName(request.getName());
        Line persistLine = lineRepository.save(request.toLine());
        return LineResponse.of(persistLine);
    }

    @Transactional(readOnly = true)
    public List<LineFindResponse> findAll() {
        List<Line> lines = lineRepository.findAll();
        return LineFindResponse.ofList(lines);
    }

    @Transactional(readOnly = true)
    public LineFindResponse findLine(Long id) {
        Line line = lineRepository.findById(id).orElseThrow(() -> new NotFoundResourceException("존재하지 않는 노선입니다. (입력값: " + id + ")"));
        return LineFindResponse.of(line);
    }

    public void updateLine(Long id, LineRequest request) {
        Line line = lineRepository.findById(id).orElseThrow(() -> new NotFoundResourceException("존재하지 않는 노선입니다. (입력값: " + id + ")"));
        validateDuplicatedLineName(request.getName());
        line.update(request.toLine());
    }

    public void deleteLine(Long id) {
        lineRepository.findById(id).orElseThrow(() -> new NotFoundResourceException("존재하지 않는 노선입니다. (입력값: " + id + ")"));
        lineRepository.deleteById(id);
    }

    private void validateDuplicatedLineName(String name) {
        if (lineRepository.findByName(name).isPresent()) {
            throw new IllegalArgumentException("이미 존재하는 노선 이름입니다. (입력값: " + name + ")");
        }
    }
}
