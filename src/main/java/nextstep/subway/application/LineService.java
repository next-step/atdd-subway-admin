package nextstep.subway.application;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import nextstep.subway.dto.LineRequest;
import nextstep.subway.dto.LineResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class LineService {
    private final LineRepository lineRepository;
    private final StationService stationService;
    private final SectionService sectionService;

    public LineService(LineRepository lineRepository, StationService stationService, SectionService sectionService) {
        this.lineRepository = lineRepository;
        this.stationService = stationService;
        this.sectionService = sectionService;
    }

    @Transactional
    public LineResponse saveLine(LineRequest lineRequest) {
        Station upStation = stationService.findById(lineRequest.getUpStationId());
        Station downStation = stationService.findById(lineRequest.getDownStationId());

        Line persistLine = lineRepository.save(new Line(lineRequest.getName(), lineRequest.getColor()));

        persistLine.addSection(new Section(persistLine, upStation, downStation, lineRequest.getDistance()));

        return LineResponse.of(persistLine);
    }

    @Transactional(readOnly = true)
    public List<LineResponse> findAllLines() {
        List<Line> lines = lineRepository.findAll();

        return lines.stream()
                .map(LineResponse::of)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public LineResponse findLineById(Long id) {
        Line line = findById(id);
        return LineResponse.of(line);
    }

    @Transactional
    public void modifyLine(Long id, LineRequest lineRequest) {
        Line line = findById(id);
        line.modifyName(lineRequest.getName());
        line.modifyColor(lineRequest.getColor());
    }

    @Transactional(readOnly = true)
    public Line findById(Long id) {
        return lineRepository.findById(id).orElseThrow(() -> new NoSuchElementException("해당 지하철 노선을 찾을 수 없습니다."));
    }

    @Transactional
    public void deleteLineById(Long id) {
        lineRepository.deleteById(id);
    }

    @Transactional
    public void removeSectionByStationId(Long lineId, Long stationId) {
        Line line = findByIdWithSections(lineId);
        if (line.getSections().size() == 1) {
            throw new IllegalArgumentException("단일 구간인 노선입니다. 구간을 삭제할 수 없습니다.");
        }

        Optional<Section> prevSection = sectionService.findById(stationId);
        Optional<Section> nextSection = sectionService.findById(stationId);

        removeSection(line, prevSection);
        removeSection(line, nextSection);

        if (prevSection.isPresent() && nextSection.isPresent()) {
            line.addSection(sectionService.reappropriateSection(prevSection.get(), nextSection.get()));
        }
    }

    private void removeSection(Line line, Optional<Section> prevSection) {
        prevSection.ifPresent(section -> line.getSections().removeSection(section));
    }

    private Line findByIdWithSections(Long id) {
        return lineRepository.findByIdWithSections(id).orElseThrow(() -> new NoSuchElementException("해당 지하철 노선을 찾을 수 없습니다."));
    }
}
