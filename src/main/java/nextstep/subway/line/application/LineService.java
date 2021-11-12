package nextstep.subway.line.application;

import nextstep.subway.exception.InputDataErrorCode;
import nextstep.subway.exception.InputDataErrorException;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class LineService {
    private LineRepository lineRepository;

    public LineService(LineRepository lineRepository) {
        this.lineRepository = lineRepository;
    }

    public LineResponse saveLine(LineRequest lineRequest) {
        checkDuplicateLineName(lineRequest.getName());
        Line persistLine = lineRepository.save(lineRequest.toLine());
        return LineResponse.of(persistLine);
    }

    @Transactional(readOnly = true)
    public List<LineResponse> findAllLines() {
        List<Line> lines = lineRepository.findAll();
        return lines.stream()
                .map(line -> LineResponse.of(line))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public LineResponse findOneLine(Long id) {
        Line findLine = lineRepository.findById(id)
                .orElseThrow(() -> new InputDataErrorException(InputDataErrorCode.THERE_IS_NOT_SEARCHED_LINE));
        return LineResponse.of(findLine);
    }

    public void deleteLineById(Long id) {
        lineRepository.deleteById(id);
    }

    public LineResponse updateLine(Long id, LineRequest lineRequest) {
        Line modifyLine = lineRepository.getOne(id);
        modifyLine.update(lineRequest.toLine());
        return LineResponse.of(modifyLine);
    }

    private void checkDuplicateLineName(String name) {
        if (lineRepository.existsByName(name)) {
            throw new InputDataErrorException(InputDataErrorCode.THERE_IS_A_DUPLICATE_NAME);
        }
    }
}
