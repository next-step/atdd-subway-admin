package nextstep.subway.line.application;

import javax.persistence.EntityNotFoundException;

import nextstep.subway.line.domain.*;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;

import nextstep.subway.line.dto.SectionRequest;
import nextstep.subway.line.dto.SectionResponse;
import nextstep.subway.station.application.StationService;
import nextstep.subway.station.domain.Station;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class LineService {

    private final LineRepository lineRepository;
    private final StationService stationService;

    public LineService(LineRepository lineRepository, StationService stationService) {
        this.lineRepository = lineRepository;
        this.stationService = stationService;
    }

    public LineResponse saveLine(LineRequest request) {
        Section section = createSectionFrom(request);
        Line line = getLineFrom(request, section);

        return LineResponse.of(lineRepository.save(line));
    }

    public SectionResponse saveSection(Long lineId, SectionRequest request) {
        Line line = getLineFrom(lineId);
        Section section = createSectionFrom(request);
        line.add(section);

        return SectionResponse.of(section);
    }

    private Section createSectionFrom(LineRequest request) {
        return new Section(
            stationService.getStation(request.getUpStationId()),
            stationService.getStation(request.getDownStationId()),
            new SectionDistance(request.getDistance())
        );
    }

    private Section createSectionFrom(SectionRequest request) {
        return new Section(
            stationService.getStation(request.getUpStationId()),
            stationService.getStation(request.getDownStationId()),
            new SectionDistance(request.getDistance())
        );
    }

    private Line getLineFrom(LineRequest request, Section section) {
        Line line = request.toLine();
        line.add(section);

        return line;
    }

    private Line getLineFrom(Long lineId) {
        return lineRepository.findById(lineId).orElseThrow(EntityNotFoundException::new);
    }

    private Station getStationFrom(Long stationId) {
        return stationService.getStation(stationId);
    }

    @Transactional(readOnly = true)
    public List<LineResponse> findAllLines() {
        return lineRepository.findAll()
            .stream()
            .map(LineResponse::of)
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public LineResponse findLineBy(Long id) {
        return lineRepository
            .findById(id)
            .map(LineResponse::of)
            .orElseThrow(EntityNotFoundException::new);
    }

    public LineResponse updateLineBy(Long id, LineRequest lineRequest) {
        return lineRepository
            .findById(id)
            .map(it -> it.getUpdatedLineBy(lineRequest))
            .map(LineResponse::of)
            .orElseThrow(EntityNotFoundException::new);
    }

    public void deleteStationBy(Long id) {
        lineRepository.deleteById(id);
    }

    public void removeSectionBy(Long lineId, Long stationId) {
        Line line = getLineFrom(lineId);
        Station station = getStationFrom(stationId);

        line.removeSectionBy(station);
    }
}
