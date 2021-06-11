package nextstep.subway.line.application;

import nextstep.subway.exception.NotFoundEntityException;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.section.domain.Section;
import nextstep.subway.section.domain.Sections;
import nextstep.subway.section.dto.SectionRequest;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class LineService {
    private final LineRepository lineRepository;
    private final StationRepository stationRepository;

    public LineService(LineRepository lineRepository, StationRepository stationRepository) {
        this.lineRepository = lineRepository;
        this.stationRepository = stationRepository;
    }

    @Transactional(readOnly = true)
    public List<LineResponse> findAllLines() {
        return lineRepository.findAll().stream()
                .map(LineResponse::of).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public LineResponse findById(final Long lineId) {
        return LineResponse.of(getLine(lineId));
    }

    public LineResponse saveLine(final LineRequest request) {

        Section section = Section.of(
                getStation(request.getUpStationId()),
                getStation(request.getDownStationId()),
                request.getDistance()
        );
        section.setSequence(0);
        LinkedList<Section> sections = new LinkedList<>();
        sections.add(section);

        Line save = lineRepository.save(new Line(request.getName(), request.getColor(),
                Sections.of(sections)));
        return LineResponse.of(save);
    }

    public LineResponse updateById(final Long lineId, final LineRequest lineRequest) {
        Line originLine = getLine(lineId);

        Section section = Section.of(getStation(lineRequest.getUpStationId()), getStation(lineRequest.getDownStationId()),
                lineRequest.getDistance());
        originLine.update(lineRequest.getName(), lineRequest.getColor(), section);

        return LineResponse.of(originLine);
    }

    public Line addSection(Long lineId, SectionRequest request) {
        Line line = getLine(lineId);
        Section section = Section.of(getStation(request.getUpStationId()), getStation(request.getDownStationId()),
                request.getDistance());
        line.addSection(section);
        return line;
    }

    public void deleteLineById(final Long id) {
        lineRepository.deleteById(id);
    }

    private Line getLine(Long lineId) {
        return lineRepository.findById(lineId)
                .orElseThrow(() -> new NotFoundEntityException("Not Found lineId" + lineId));
    }

    private Station getStation(Long stationId) {
        return stationRepository.findById(stationId)
                .orElseThrow(() -> new NotFoundEntityException("Not Fount downStationId" + stationId));
    }

    public void removeStation(Long lineId, Long stationId) {
        Line line = getLine(lineId);
        line.removeStation(getStation(stationId));
    }
}
