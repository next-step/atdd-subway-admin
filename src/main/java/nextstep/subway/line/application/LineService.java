package nextstep.subway.line.application;

import nextstep.subway.common.NotFoundException;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.section.domain.Section;
import nextstep.subway.section.domain.SectionRepository;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class LineService {
    public static final String THE_REQUESTED_INFORMATION_DOES_NOT_EXIST = "The requested information does not exist.";
    private final LineRepository lineRepository;
    private final StationRepository stationRepository;
    private final SectionRepository sectionRepository;

    public LineService(final LineRepository lineRepository, final StationRepository stationRepository, final SectionRepository sectionRepository) {
        this.lineRepository = lineRepository;
        this.stationRepository = stationRepository;
        this.sectionRepository = sectionRepository;
    }

    @Transactional
    public LineResponse saveLine(final LineRequest request) {
        final Line persistLine = lineRepository.save(request.toLine());
        final Station upStation = stationRepository.findById(request.getUpStationId()).get();
        final Station downStation = stationRepository.findById(request.getDownStationId()).get();
        upStation.addLine(persistLine);
        downStation.addLine(persistLine);
        sectionRepository.save(new Section(persistLine, upStation, downStation, request.getDistance()));
        final List<Station> stations = stationRepository.findByLine(persistLine);
        return LineResponse.of(persistLine, stations);
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
        final List<Station> stations = stationRepository.findByLine(line);
        return LineResponse.of(line, stations);
    }

    @Transactional
    public LineResponse updateLine(final Long id, final LineRequest lineRequest) {
        final Line line = lineRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(THE_REQUESTED_INFORMATION_DOES_NOT_EXIST));
        line.update(new Line(lineRequest.getName(), lineRequest.getColor()));
        return LineResponse.from(line);
    }
}
