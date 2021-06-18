package nextstep.subway.line.application;

import lombok.RequiredArgsConstructor;
import nextstep.subway.line.LineNotFoundException;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.section.SectionNotFoundException;
import nextstep.subway.section.domain.Section;
import nextstep.subway.section.domain.SectionRepository;
import nextstep.subway.section.domain.Sections;
import nextstep.subway.section.dto.SectionRequest;
import nextstep.subway.section.dto.SectionResponse;
import nextstep.subway.station.StationNotFoundException;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class LineService {
    private final LineRepository lineRepository;
    private final SectionRepository sectionRepository;
    private final StationRepository stationRepository;

    public LineResponse saveLine(final LineRequest request) {
        Line persistLine = lineRepository.save(request.toLine());

        registerSection(request.toSectionRequest(), persistLine);

        return LineResponse.of(persistLine);
    }

    public SectionResponse appendSection(final Long id, final SectionRequest request) {
        Line line = lineRepository.findById(id).orElseThrow(() -> new LineNotFoundException());

        Section section = registerSection(request, line);

        return SectionResponse.of(section);
    }

    private Section registerSection(final SectionRequest request, final Line line) {
        Station upStation = stationRepository.findById(request.getUpStationId())
                .orElseThrow(() -> new StationNotFoundException());

        Station downStation = stationRepository.findById(request.getDownStationId())
                .orElseThrow(() -> new StationNotFoundException());

        Section section = Section.builder()
                .upStation(upStation)
                .downStation(downStation)
                .distance(request.getDistance())
                .build();

        section.registerLine(line);

        return sectionRepository.save(section);
    }

    @Transactional(readOnly = true)
    public List<LineResponse> findAllLines() {
        List<Line> Lines = lineRepository.findAll();

        return Lines.stream()
                .map(LineResponse::of)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public LineResponse findLine(final Long id) {
         Line line = lineRepository.findById(id)
                 .orElseThrow(() -> new LineNotFoundException());

        return LineResponse.of(line);
    }

    @Transactional(readOnly = true)
    public SectionResponse findSection(final Long id) {
        Section section = sectionRepository.findById(id)
                .orElseThrow(() -> new SectionNotFoundException());

        return SectionResponse.of(section);
    }

    @Transactional(readOnly = true)
    public List<SectionResponse> findAllSection() {
        List<Section> sections = sectionRepository.findAll();

        return sections.stream()
                .map(SectionResponse::of)
                .collect(Collectors.toList());
    }

    public void updateLine(final Long id, final LineRequest request) {
        Line line = lineRepository.findById(id)
                .orElseThrow(() -> new LineNotFoundException());

        line.update(request.toLine());
    }

    public void deleteLineById(final Long id) {
        lineRepository.deleteById(id);
    }

    public void removeSectionByStationId(final Long lindId, final Long stationId) {
        Line line = lineRepository.findById(lindId)
                .orElseThrow(() -> new LineNotFoundException());

        Station station = stationRepository.findById(stationId)
                .orElseThrow(() -> new StationNotFoundException());

        Sections sections = line.getSections();
        sections.remove(station);
    }
}
