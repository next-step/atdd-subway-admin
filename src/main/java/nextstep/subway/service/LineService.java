package nextstep.subway.service;

import java.util.List;
import java.util.stream.Collectors;
import javassist.NotFoundException;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import nextstep.subway.repository.LineRepository;
import nextstep.subway.repository.StationRepository;
import nextstep.subway.dto.LineRequest;
import nextstep.subway.dto.LineResponse;
import nextstep.subway.dto.SectionRequest;
import nextstep.subway.dto.SectionResponse;
import nextstep.subway.dto.StationResponse;
import nextstep.subway.value.ErrMsg;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@Transactional(readOnly = true)
public class LineService {
    private final LineRepository lineRepository;
    private final StationRepository stationRepository;


    public LineService(LineRepository lineRepository, StationRepository stationRepository) {
        this.lineRepository = lineRepository;
        this.stationRepository = stationRepository;
    }

    @Transactional
    public LineResponse saveLine(LineRequest lineRequest) {
        Line persistLine = lineRepository.save(lineRequest.toLine());
        return LineResponse.of(persistLine);
    }

    public List<LineResponse> findAllLines() {
        List<Line> lines = lineRepository.findAll();

        return lines.stream()
                .map(LineResponse::of)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteLineById(Long id) throws NotFoundException {
        Line line = findLineById(id);
        lineRepository.delete(line);
    }

    public LineResponse findLine(Long id) throws NotFoundException {
        return LineResponse.of(findLineById(id));
    }

    @Transactional
    public LineResponse updateLine(Long id, LineRequest lineRequest) throws NotFoundException {
        Line line =  findLineById(id);
        line.update(lineRequest);
        return LineResponse.of(line);
    }

    private Line findLineById(Long id) throws NotFoundException {
        return lineRepository.findById(id).orElseThrow(
                () -> new NotFoundException(ErrMsg.notFoundLine(id))
        );
    }
    public Station findStationById(Long id) throws NotFoundException {
        return stationRepository.findById(id).orElseThrow(
                () -> new NotFoundException(ErrMsg.notFoundStation(id))
        );
    }

    public List<StationResponse> findLineStations(Long id) throws NotFoundException {
        Line line = findLineById(id);
        return line.getOrderedStations().stream().map(StationResponse::of).collect(Collectors.toList());
    }
    @Transactional
    public SectionResponse addSection(Long id, SectionRequest sectionRequest) throws NotFoundException {
        Station upStation = findStationById(sectionRequest.getUpStationId());
        Station downStation = findStationById(sectionRequest.getDownStationId());
        Line line = findLineById(id);
        Section section = new Section(upStation, downStation, sectionRequest.getDistance());
        line.addSection(section);
        lineRepository.flush();
        return SectionResponse.of(section);
    }

    public List<SectionResponse> findLineSections(Long id) throws NotFoundException {
        Line line = findLineById(id);
        return line.getSections().getAll().stream().map(SectionResponse::of).collect(Collectors.toList());
    }

    public SectionResponse findLineSection(Long lineId, Long sectionId) throws NotFoundException {
        Line line = findLineById(lineId);
        return SectionResponse.of(line.getSections().getSectionById(sectionId).orElseThrow(
                ()-> new NotFoundException(ErrMsg.notFoundSection(sectionId))
        ));
    }
    @Transactional
    public void removeSectionByStationId(Long lineId, Long stationId) throws NotFoundException {
        Line line = findLineById(lineId);
        Station station = findStationById(stationId);
        line.removeStation(station);
    }
}
