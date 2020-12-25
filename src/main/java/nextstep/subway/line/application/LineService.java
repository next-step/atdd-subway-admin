package nextstep.subway.line.application;

import nextstep.subway.common.exception.NotExistsIdException;
import nextstep.subway.common.exception.NotExistsLineIdException;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.domain.Section;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.line.dto.SectionRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class LineService {
    private LineRepository lineRepository;
    private SectionService sectionService;

    public LineService(LineRepository lineRepository, SectionService sectionService) {
        this.lineRepository = lineRepository;
        this.sectionService = sectionService;
    }

    public LineResponse saveLine(LineRequest request) {
        Line persistLine = lineRepository.save(request.toLine());
        sectionService.saveSection(SectionRequest.of(persistLine.getId(), request));
        return LineResponse.of(persistLine);
    }

    public List<LineResponse> findAllLines() {
        List<Line> lines = lineRepository.findAll();
        return lines.stream()
                .map(LineResponse::of)
                .collect(Collectors.toList());
    }

    public LineResponse findById(Long id) {
        Line persistLine = lineRepository.findById(id)
                .orElseThrow(() -> new NotExistsLineIdException(id));
        return LineResponse.of(persistLine);
    }

    public LineResponse updateLine(Long id, LineRequest lineRequest) {
        Line persistLine = lineRepository.findById(id)
                .orElseThrow(() -> new NotExistsLineIdException(id));
        persistLine.update(lineRequest.toLine());
        return LineResponse.of(persistLine);
    }

    public void deleteLine(Long lineId) {
        sectionService.deleteAllByLineId(lineId);
        lineRepository.deleteById(lineId);
    }

    private void findStationById(Long stationId) {

    }
}
