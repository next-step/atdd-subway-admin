package nextstep.subway.line.application;

import nextstep.subway.common.exception.NotFoundException;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.line.dto.LineResponseList;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class LineService {

    private LineRepository lineRepository;

    public LineService(LineRepository lineRepository) {
        this.lineRepository = lineRepository;
    }

    @Transactional(readOnly = true)
    public LineResponse findOne(Long id) {
        Line persistLine = this.findById(id);
        return LineResponse.of(persistLine);
    }

    @Transactional(readOnly = true)
    public LineResponseList findAll() {
        List<Line> persistLines = lineRepository.findAll();
        return new LineResponseList(persistLines);
    }

    public LineResponse saveLine(LineRequest request) {
        validateDuplicatedLine(request.getName());
        Line persistLine = lineRepository.save(request.toLine());
        return LineResponse.of(persistLine);
    }

    private void validateDuplicatedLine(String name) {
        Optional<Line> exist = lineRepository.findByName(name);
        if (exist.isPresent()) {
            throw new DataIntegrityViolationException("중복된 노선을 추가할 수 없습니다.");
        }
    }

    public LineResponse updateLine(Long id, LineRequest request) {
        Line line = this.findById(id);
        line.update(request.toLine());
        Line persistLine = lineRepository.save(line);
        return LineResponse.of(persistLine);
    }

    private Line findById(Long id) {
        return lineRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("찾을 수 없는 노선입니다."));
    }

    public void deleteLine(Long id) {
        lineRepository.deleteById(id);
    }

}
