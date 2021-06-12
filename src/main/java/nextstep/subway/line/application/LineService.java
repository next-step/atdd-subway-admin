package nextstep.subway.line.application;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import nextstep.subway.NotFoundException;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.section.domain.Section;
import nextstep.subway.section.domain.SectionRepository;
import nextstep.subway.section.dto.SectionRequest;
import nextstep.subway.section.dto.SectionResponse;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;

@Service
@Transactional
public class LineService {
    private final LineRepository lineRepository;
    private final StationRepository stationRepository;
    private final SectionRepository sectionRepository;

    public LineService(final LineRepository lineRepository,
        final StationRepository stationRepository, final SectionRepository sectionRepository,
        final SectionRepository sectionRepository1) {

        this.lineRepository = lineRepository;
        this.stationRepository = stationRepository;
        this.sectionRepository = sectionRepository1;
    }

    public LineResponse saveLine(final LineRequest request) {
        return lineResponse(persistLine(initializeLine(request)));
    }

    private Line initializeLine(final LineRequest request) {
        final Line line = request.toLine();
        line.addSection(new Section(stations(ids(request)), request.getDistance(), line));

        return line;
    }

    private Line persistLine(final Line line) {
        return lineRepository.save(line);
    }

    private List<Long> ids(final LineRequest request) {
        return Arrays.asList(request.getUpStationId(), request.getDownStationId());
    }

    private List<Station> stations(final List<Long> ids) {
        return stationRepository.findByIdIn(ids);
    }

    private LineResponse lineResponse(final Line line) {
        return LineResponse.of(line);
    }

    @Transactional(readOnly = true)
    public List<LineResponse> findLines() {
        final List<Line> lines = findAllLine();
        final List<LineResponse> lineResponses = new ArrayList<>(lines.size());
        lines.forEach(line -> lineResponses.add(lineResponse(line)));

        return lineResponses;
    }

    private List<Line> findAllLine() {
        return lineRepository.findAll();
    }

    @Transactional(readOnly = true)
    public LineResponse findLine(final Long id) {
        return LineResponse.of(findById(id));
    }

    private Line findById(final Long id) {
        return lineRepository.findById(id)
            .orElseThrow(NotFoundException::new);
    }

    public LineResponse updateLine(final Long id, final LineRequest lineRequest) {
        final Line line = findById(id);
        line.update(lineRequest.toLine());

        return LineResponse.of(persistLine(line));
    }

    public void deleteLineById(final Long id) {
        lineRepository.deleteById(id);
    }

    public SectionResponse addSection(final Long lineId, final SectionRequest sectionRequest) {
        return SectionResponse.of(persistSection(lineId, sectionRequest));
    }

    private Section persistSection(final Long lineId, final SectionRequest sectionRequest) {
        return sectionRepository.save(section(sectionRequest, findById(lineId)));
    }

    private Section section(final SectionRequest sectionRequest, final Line line) {
        final List<Long> ids = Arrays.asList(sectionRequest.getUpStationId(), sectionRequest.getDownStationId());
        final List<Station> stations = stationRepository.findByIdIn(ids);

        return Section.of(stations, sectionRequest.getDistance(), line);
    }
}
