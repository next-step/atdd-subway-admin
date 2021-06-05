package nextstep.subway.line.application;

import nextstep.subway.exception.DuplicateDataException;
import nextstep.subway.exception.NoSuchDataException;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.line.dto.LinesSubResponse;
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

    public LineResponse saveLine(LineRequest request) throws NoSuchFieldException {
        validateDuplicatedName(request);
        Line persistLine = lineRepository.save(request.toLine());
        return LineResponse.of(persistLine);
    }

    public void changeLine(Long lineId, LineRequest lineRequest) {
        Line line = findById(lineId);
        line.change(lineRequest.getName(), lineRequest.getColor());
    }

    @Transactional(readOnly = true)
    public void validateDuplicatedName(LineRequest lineRequest) throws NoSuchFieldException {
        if (lineRepository.existsByName(lineRequest.getName())) {
            throw new DuplicateDataException("이미 존재하는 노선 이름입니다.",
                    lineRequest.getClass().getDeclaredField("name").getName(),
                    lineRequest.getName(), lineRequest.getClass().getName());
        }
    }

    @Transactional(readOnly = true)
    public LinesSubResponse readLine(Long lineId) {
        Line line = findById(lineId);
        return LinesSubResponse.of(line);
    }

    @Transactional(readOnly = true)
    public List<LinesSubResponse> readLineAll() {
        List<Line> lines = lineRepository.findAll();
        return lines.stream()
                .map(LinesSubResponse::of)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    private Line findById(Long lineId) {
        return lineRepository.findById(lineId).orElseThrow(() -> new NoSuchDataException("존재하지 않는 노선입니다.",
                "lineId", String.valueOf(lineId), null));
    }
}
