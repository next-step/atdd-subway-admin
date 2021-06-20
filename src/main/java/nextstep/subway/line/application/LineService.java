package nextstep.subway.line.application;

import nextstep.subway.exception.DuplicateNameException;
import nextstep.subway.exception.NoSuchDataException;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.line.dto.SectionRequest;
import nextstep.subway.section.domain.Section;
import nextstep.subway.section.service.SectionService;
import nextstep.subway.station.application.StationService;
import nextstep.subway.station.domain.Station;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class LineService {
    private LineRepository lineRepository;
    private StationService stationService;
    private SectionService sectionService;

    public LineService(LineRepository lineRepository, StationService stationService, SectionService sectionService) {
        this.lineRepository = lineRepository;
        this.stationService = stationService;
        this.sectionService = sectionService;
    }


    public LineResponse saveLine(LineRequest lineRequest, Section section) {
        Line line = lineRequest.toLine();
        line.addSection(section);
        Line persistLine = lineRepository.save(line);
        return LineResponse.of(persistLine);
    }

    @Transactional(readOnly = true)
    public List<LineResponse> findAllLine() {
        List<Line> lines = lineRepository.findAll();
        return lines.stream()
                .map(LineResponse::of)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Line findLine(Long lineId) {
        Line line = lineRepository.findById(lineId).orElseThrow(() -> new NoSuchDataException("존재하지 않는 노선 ID입니다."));
        return line;
    }

    public void updateLine(Long lineId, LineRequest lineRequest) {
        Line line = lineRepository.findById(lineId).orElseThrow(() -> new NoSuchDataException("존재하지 않는 노선 ID입니다."));
        line.update(lineRequest.toLine());
    }

    public void deleteLine(Long lineId) {
        lineRepository.deleteById(lineId);
    }

    @Transactional(readOnly = true)
    public void validateCheck(LineRequest lineRequest) {
        if (lineRepository.existsByName(lineRequest.getName())) {
            throw new DuplicateNameException("이미 존재하는 노선 이름입니다.");
        }
    }

    public Line saveLineSection(Long lineId, SectionRequest sectionRequest) {
        Line line = lineRepository.findById(lineId).orElseThrow(() -> new NoSuchDataException("존재하지 않는 노선 ID입니다."));
        Station upStation = stationService.findStation(sectionRequest.getUpStationId());
        Station downStation = stationService.findStation(sectionRequest.getDownStationId());
        Section section = Section.of(upStation, downStation, sectionRequest.getDistance());
        line.addAdditionalSection(section);
        sectionService.save(section);

        return line;
    }

    public Line deleteLineSectionByStationId(Long lineId, Long stationId) {
        Line line = lineRepository.findById(lineId).orElseThrow(() -> new NoSuchDataException("존재하지 않는 노선 ID입니다."));
        Station station = stationService.findStation(stationId);
        line.deleteSectioByStation(station);
        return line;
    }
}
