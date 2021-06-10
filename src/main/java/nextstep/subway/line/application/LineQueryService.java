package nextstep.subway.line.application;

import nextstep.subway.line.application.exception.LineDuplicatedException;
import nextstep.subway.line.application.exception.LineNotFoundException;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.dto.LineResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class LineQueryService implements LineQueryUseCase {
    public static final String LINE_ID_NOT_FOUND_EXCEPTION_MESSAGE = "해당 ID로 된 지하철 노선이 존재하지 않습니다.";
    public static final String LINE_NAME_DUPLICATED_EXCEPTION_MESSAGE = "해당 이름으로 등록된 지하철 노선이 있습니다.";

    private final LineRepository lineRepository;

    public LineQueryService(LineRepository lineRepository) {
        this.lineRepository = lineRepository;
    }

    @Override
    public List<LineResponse> findAllLines() {
        return lineRepository.findAll()
                .stream()
                .map(line -> LineResponse.of(line, line.getStations()))
                .collect(Collectors.toList());
    }

    @Override
    public LineResponse findLine(Long id) {
        Line line = findById(id);
        return LineResponse.of(line, line.getStations());
    }

    @Override
    public Line findById(Long id) {
        return lineRepository.findById(id)
                .orElseThrow(() -> new LineNotFoundException(LINE_ID_NOT_FOUND_EXCEPTION_MESSAGE));
    }

    @Override
    public void checkDuplicatedLineName(String name) {
        if (lineRepository.findByName(name).isPresent()) {
            throw new LineDuplicatedException(LINE_NAME_DUPLICATED_EXCEPTION_MESSAGE);
        }
    }
}
