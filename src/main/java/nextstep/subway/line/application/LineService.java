package nextstep.subway.line.application;

import nextstep.subway.common.Message;
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
    private final LineRepository lineRepository;
    private final StationRepository stationRepository;

    public LineService(final LineRepository lineRepository, final StationRepository stationRepository) {
        this.lineRepository = lineRepository;
        this.stationRepository = stationRepository;
    }

    @Transactional
    public LineResponse saveLine(final LineRequest request) {
        final Station upStation = getStation(request.getUpStationId());
        final Station downStation = getStation(request.getDownStationId());
        final Sections sections = Sections.from(Section.of(upStation, downStation, request.getDistance()));
        final Line persistLine = lineRepository.save(Line.of(request.getName(), request.getColor(), sections));
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
        final Line line = getLine(id);
        return LineResponse.from(line);
    }

    @Transactional
    public LineResponse updateLine(final Long id, final LineRequest lineRequest) {
        final Line line = getLine(id);
        line.update(lineRequest.toLine());
        return LineResponse.from(line);
    }

    public LineResponse addSection(Long id, SectionRequest sectionRequest) {
        final Line line = getLine(id);
        Station upStation = getStation(sectionRequest.getUpStationId());
        Station downStation = getStation(sectionRequest.getDownStationId());
        Section section = Section.of(upStation, downStation, sectionRequest.getDistance());
        line.addSection(section);
        return LineResponse.from(line);
    }

    private Line getLine(Long lineid) {
        return lineRepository.findById(lineid)
                .orElseThrow(() -> new IllegalArgumentException(Message.NOT_FIND_LINE.getMessage()));
    }

    private Station getStation(Long stationId) {
        return stationRepository.findById(stationId)
                .orElseThrow(() -> new IllegalArgumentException(Message.NOT_FIND_STATION.getMessage()));
    }
}
