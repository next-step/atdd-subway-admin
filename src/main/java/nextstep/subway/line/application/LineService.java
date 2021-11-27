package nextstep.subway.line.application;

import nextstep.subway.common.exception.NotFoundException;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.line.dto.SectionRequest;
import nextstep.subway.section.domain.Section;
import nextstep.subway.section.domain.Sections;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class LineService {
    public static final String THE_REQUESTED_INFORMATION_DOES_NOT_EXIST = "The requested information does not exist.";
    private final LineRepository lineRepository;
    private final StationRepository stationRepository;

    public LineService(final LineRepository lineRepository, final StationRepository stationRepository) {
        this.lineRepository = lineRepository;
        this.stationRepository = stationRepository;
    }

    @Transactional
    public LineResponse saveLine(final LineRequest request) {
        final Station upStation = stationRepository.findById(request.getUpStationId()).get();
        final Station downStation = stationRepository.findById(request.getDownStationId()).get();
        final Section section = Section.of(upStation, downStation, request.getDistance());
        final List<Section> sectionList = new ArrayList<>();
        sectionList.add(section);
        final Line persistLine = lineRepository.save(Line.of(request.getName(), request.getColor(), Sections.of(sectionList)));
        return LineResponse.from(persistLine);
    }

    public List<LineResponse> findAllLines() {
        final List<Line> lines = lineRepository.findAll();
        return LineResponse.from(lines);
    }

    public void deleteLineById(final Long id) {
        lineRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public LineResponse findByLineId(final Long id) {
        final Line line = lineRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(THE_REQUESTED_INFORMATION_DOES_NOT_EXIST));
        return LineResponse.from(line);
    }

    @Transactional
    public LineResponse updateLine(final Long id, final LineRequest lineRequest) {
        final Line line = lineRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(THE_REQUESTED_INFORMATION_DOES_NOT_EXIST));
        line.update(Line.of(lineRequest.getName(), lineRequest.getColor()));
        return LineResponse.from(line);
    }

    public LineResponse addSection(Long id, SectionRequest sectionRequest) {
        final Line line = lineRepository.findById(id).get();
        Station upStation = stationRepository.findById(sectionRequest.getUpStationId()).get();
        Station downStation = stationRepository.findById(sectionRequest.getDownStationId()).get();
        Section section = Section.of(upStation, downStation, sectionRequest.getDistance());
        line.addSection(section);
        return LineResponse.from(line);
    }
}
