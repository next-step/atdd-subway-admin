package nextstep.subway.line.application;

import nextstep.subway.common.exception.NotFoundException;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.line.dto.LineResponseList;
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

    @Transactional(readOnly = true)
    public LineResponse findOne(Long id) throws NotFoundException {
        Line persistLine = this.findById(id);
        return LineResponse.of(persistLine);
    }

    @Transactional(readOnly = true)
    public LineResponseList findAll() {
        List<Line> persistLines = lineRepository.findAll();
        return new LineResponseList(persistLines);
    }

    public LineResponse saveLine(LineRequest request) {
        Line persistLine = lineRepository.save(request.toLine());
        return LineResponse.of(persistLine);
    }

    public LineResponse updateLine(Long id, LineRequest request) throws NotFoundException {
        Line line = this.findById(id);
        line.update(request.toLine());
        Line persistLine = lineRepository.save(line);
        return LineResponse.of(persistLine);
    }

    public void deleteLine(Long id) {
        lineRepository.deleteById(id);
    }

    private Line findById(Long id) throws NotFoundException {
        return lineRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("찾을 수 없는 노선입니다."));
    }

}
