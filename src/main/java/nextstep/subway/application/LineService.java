package nextstep.subway.application;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import nextstep.subway.dto.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class LineService {
    private final LineRepository lineRepository;
    private final StationService stationService;

    public LineService(LineRepository lineRepository, StationService stationService) {
        this.lineRepository = lineRepository;
        this.stationService = stationService;
    }

    @Transactional
    public LineResponse saveLine(LineRequest lineRequest) {
        Station upStation = stationService.findById(lineRequest.getUpStationId());
        Station downStation = stationService.findById(lineRequest.getDownStationId());
        Section section = new Section(upStation, downStation, lineRequest.getDistance());
        Line line = Line.of(lineRequest.getName(), lineRequest.getColor(), section);
        return LineResponse.from(lineRepository.save(line));
    }

    public List<LineResponse> findAllLines() {
        List<Line> lines = lineRepository.findAll();

        return lines.stream()
                .map(LineResponse::from)
                .collect(Collectors.toList());
    }

    public LineResponse findLine(final Long lineId) {
        Line persistLine = findById(lineId);
        return LineResponse.from(persistLine);
    }

    @Transactional
    public void deleteLineById(Long id) {
        lineRepository.deleteById(id);
    }

    @Transactional
    public LineResponse modify(final Long lineId, final LineRequest lineRequest) {
        Line persistLine = findById(lineId);
        persistLine.changeName(lineRequest.getName());
        persistLine.changeColor(lineRequest.getColor());
        return LineResponse.from(persistLine);
    }

    public Line findById(final Long lineId) {
        return lineRepository.findById(lineId)
                .orElseThrow(() -> new IllegalArgumentException(lineId + "번 노선을 찾을 수 없습니다."));
    }

    @Transactional
    public SectionResponse addSection(final Long lineId, final SectionRequest sectionRequest) {
        Station upStation = stationService.findById(sectionRequest.getUpStationId());
        Station downStation = stationService.findById(sectionRequest.getDownStationId());
        Section section = new Section(upStation, downStation, sectionRequest.getDistance());
        Line line = findById(lineId);
        line.addSection(section);
        lineRepository.flush();
        return SectionResponse.from(section);
    }

    public List<SectionResponse> findAllSections(final Long lineId) {
        Line line = findById(lineId);

        return line.getSections().stream()
                .map(SectionResponse::from)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteSection(final Long lineId, final Long stationId) {
        Line line = findById(lineId);
        line.deleteSectionByStationId(stationId);
    }
}
