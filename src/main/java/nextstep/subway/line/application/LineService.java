package nextstep.subway.line.application;

import nextstep.subway.NotFoundException;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.section.dto.SectionRequest;
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
        return LineResponse.of(persistLine);
    }

    @Transactional(readOnly = true)
    public List<LineResponse> readAllLines() {
        final List<Line> lines = lineRepository.findAllWithSections();
        return LineResponse.of(lines);
    }

    @Transactional(readOnly = true)
    public LineResponse readLine(Long id) {
        final Line line = readById(id);
        return LineResponse.of(line);
    }

    public LineResponse updateLine(Long id, LineRequest lineRequest) {
        final Line line = readById(id);
        line.update(lineRequest.toLineForUpdate());
        return LineResponse.of(line);
    }

    public void deleteLine(Long id) {
        lineRepository.deleteById(id);
    }

    public void addSection(Long lineId, SectionRequest sectionRequest) {
        Line line = readWithSectionsById(lineId);
        line.addSection(sectionRequest.toSectionWith(line));
    }

    private Line readById(Long id) {
        return lineRepository.findById(id)
                             .orElseThrow(() -> new NotFoundException("해당하는 Line이 없습니다. id = " + id));
    }

    public void removeSectionByStationId(Long lineId, Long stationId) {
        Line line = readWithSectionsById(lineId);
        line.removeSection(stationId);
    }

    private Line readWithSectionsById(Long id) {
        return lineRepository.findWithSectionsById(id)
            .orElseThrow(() -> new NotFoundException("해당하는 Line이 없습니다. id = " + id));
    }
}
