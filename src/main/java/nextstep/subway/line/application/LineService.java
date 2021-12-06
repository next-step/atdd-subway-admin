package nextstep.subway.line.application;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.line.dto.SectionRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LineService {

    private final SectionService sectionService;
    private final LineRepository lineRepository;

    public LineService(
        final SectionService sectionService,
        final LineRepository lineRepository
    ) {
        this.sectionService = sectionService;
        this.lineRepository = lineRepository;
    }

    @Transactional
    public LineResponse saveLine(final LineRequest request) {
        checkLineNameIsUnique(request.getName());
        final Line persistLine = lineRepository.save(request.toLine());
        sectionService.addSection(
            persistLine.getId(),
            new SectionRequest(
                request.getUpStationId(),
                request.getDownStationId(),
                request.getDistance()
            )
        );
        return LineResponse.of(persistLine);
    }

    @Transactional(readOnly = true)
    public List<LineResponse> findLines() {
        final List<Line> lines = lineRepository.findAll();
        return lines.stream()
            .map(LineResponse::of)
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public LineResponse findLineById(final Long id) {
        final Line line = getLineById(id);
        return LineResponse.of(line);
    }

    @Transactional
    public void updateLine(final Long id, final LineRequest request) {
        final Line line = getLineById(id);
        if (!line.nameEquals(request.getName())) {
            checkLineNameIsUnique(request.getName());
        }
        line.changeName(request.getName());
        line.changeColor(request.getColor());
    }

    @Transactional
    public void deleteStationById(final Long id) {
        final Line line = getLineById(id);
        lineRepository.delete(line);
    }

    private Line getLineById(final Long id) {
        return lineRepository.findById(id)
            .orElseThrow(NoSuchElementException::new);
    }

    private void checkLineNameIsUnique(final String requestedName) {
        if (lineRepository.existsByName(requestedName)) {
            throw new IllegalArgumentException("이미 존재하는 지하철 노선 이름으로 지하철 노선을 생성할 수 없습니다.");
        }
    }
}
