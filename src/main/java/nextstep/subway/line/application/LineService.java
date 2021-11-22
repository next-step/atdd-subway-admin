package nextstep.subway.line.application;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import nextstep.subway.common.Messages;
import nextstep.subway.exception.NotFoundException;
import nextstep.subway.line.common.Constants;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.section.domain.Distance;
import nextstep.subway.section.domain.Section;
import nextstep.subway.section.domain.SectionRepository;
import nextstep.subway.section.domain.SectionType;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class LineService {

    private final LineRepository lineRepository;

    private final StationRepository stationRepository;

    private final SectionRepository sectionRepository;

    public LineService(LineRepository lineRepository, StationRepository stationRepository,
        SectionRepository sectionRepository) {
        this.lineRepository = lineRepository;
        this.stationRepository = stationRepository;
        this.sectionRepository = sectionRepository;
    }

    public LineResponse saveLine(LineRequest request) {
        Station upStation = findStationById(request.getUpStationId());
        Station downStation = findStationById(request.getDownStationId());

        List<Section> sections = saveUpDownSection(request, upStation, downStation);

        Line persistLine = lineRepository.save(request.toLine());
        persistLine.addSections(sections);

        return LineResponse.of(persistLine);
    }

    @Transactional(readOnly = true)
    public LineResponse findLine(Long lineId) {
        Line line = findById(lineId);
        return LineResponse.of(line);
    }

    @Transactional(readOnly = true)
    public List<LineResponse> findLines() {
        List<Line> lineList = lineRepository.findAll();
        return lineList
            .stream()
            .map(line -> LineResponse.of(line))
            .collect(Collectors.toList());
    }

    public Long updateLine(Long lineId, LineRequest lineRequest) {
        Line line = findById(lineId);

        line.update(lineRequest.toLine());
        Line save = lineRepository.save(line);
        return save.getId();
    }

    public void deleteLine(Long lineId) {
        Line line = findById(lineId);

        lineRepository.delete(line);
    }

    private Line findById(Long lineId) {
        return lineRepository.findById(lineId)
            .orElseThrow(() -> new NotFoundException(Messages.NO_LINE.getValues()));
    }

    private Station findStationById(Long stationId) {
        return stationRepository.findById(stationId)
            .orElseThrow(() -> new NotFoundException(Messages.NO_STATION.getValues()));
    }

    private Section saveSection(Section entity) {
        return sectionRepository.save(entity);
    }

    private List<Section> saveUpDownSection(LineRequest request, Station upStation, Station downStation) {
        Section up = saveSection(Section.of(new Distance(request.getDistance()), SectionType.UP, upStation, downStation));
        Section down = saveSection(Section.fromDownStation(downStation));
        return Arrays.asList(up, down);
    }
}
