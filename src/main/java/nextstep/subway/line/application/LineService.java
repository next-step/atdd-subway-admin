package nextstep.subway.line.application;

import nextstep.subway.exception.CanNotFoundLineException;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.section.domain.Section;
import nextstep.subway.section.domain.SectionRepository;
import nextstep.subway.section.domain.Sections;
import nextstep.subway.section.dto.SectionRequest;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class LineService {
    private LineRepository lineRepository;
    private SectionRepository sectionRepository;
    private StationRepository stationRepository;


    public LineService(LineRepository lineRepository, SectionRepository sectionRepository, StationRepository stationRepository) {
        this.lineRepository = lineRepository;
        this.sectionRepository = sectionRepository;
        this.stationRepository = stationRepository;
    }

    public LineResponse saveLine(LineRequest request) {
        Line persistLine = lineRepository.save(request.toLine());
        Station upStation = stationRepository.findById(request.getUpStationId())
                .orElseThrow(RuntimeException::new);
        Station downStation = stationRepository.findById(request.getDownStationId())
                .orElseThrow(RuntimeException::new);
        Section section = sectionRepository.save(new Section(upStation, downStation, request.getDistance()));
        section.toLine(persistLine);
        return LineResponse.of(persistLine);
    }

    public LineResponse addSection(Long id, SectionRequest sectionRequest) {
        Station upStation = stationRepository.findById(sectionRequest.getUpStationId()).orElseThrow(RuntimeException::new);
        Station downStation = stationRepository.findById(sectionRequest.getDownStationId()).orElseThrow(RuntimeException::new);
        Section section = sectionRepository.save(new Section(upStation, downStation, sectionRequest.getDistance()));
        Line line = lineRepository.findById(id).orElseThrow(RuntimeException::new);
        section.addLine(line);
        return LineResponse.of(line);
    }

    @Transactional(readOnly = true)
    public List<LineResponse> findAllLines() {
        List<Line> lines = lineRepository.findAll();

        return lines.stream()
                .map(line -> LineResponse.of(line))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public LineResponse findLineId(Long id) {
        Line line = lineRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("해당 이름의 노선이 존재하지 않습니다."));

        return LineResponse.of(line);

    }

    @Transactional
    public LineResponse updateLine(Long id, LineRequest request) {
        Line line = lineRepository.findById(id).orElseThrow(() -> new RuntimeException("해당 이름의 노선이 존재하지 않습니다."));
        line.update(request.toLine());

        return LineResponse.of(line);
    }

    public void deleteLineById(Long id) {
        Sections sections = lineRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("삭제할 Sections가 없습니다.")).getSections();
        sections.forEach(section -> {
            sectionRepository.delete(section);
        });
        lineRepository.deleteById(id);
    }

    public void removeSectionByStationId(Long lineId, Station station) {
        Line line = lineRepository.findById(lineId).orElseThrow(() -> new CanNotFoundLineException("해당 이름의 노선이 존재하지 않습니다."));
        line.getSections().removeStation(station);
    }
}
