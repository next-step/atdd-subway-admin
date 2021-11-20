package nextstep.subway.line.application;

import nextstep.subway.common.NotFoundException;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import org.springframework.beans.BeanUtils;
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
        Line persistLine = lineRepository.save(request.toLine());
        return LineResponse.ofCreation(persistLine);
    }

    @Transactional(readOnly = true)
    public List<LineResponse> findAllLines() {
        List<Line> lines = lineRepository.findAll();
        return LineResponse.ofList(lines);
    }

    public void deleteLineById(Long id) {
        lineRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public LineResponse findByLineId(Long id) {
        LineResponse lineResponse = new LineResponse();
        Line line = lineRepository.findById(id).orElseThrow(NotFoundException::new);
        BeanUtils.copyProperties(line, lineResponse);
        return lineResponse;
    }

    public LineResponse updateLine(Long id, LineRequest lineRequest) {
        LineResponse lineResponse = new LineResponse();
        Line line = lineRepository.findById(id).orElseThrow(NotFoundException::new);
        line.update(new Line(lineRequest.getName(), lineRequest.getColor()));
        BeanUtils.copyProperties(line, lineResponse);
        return lineResponse;
    }
}
