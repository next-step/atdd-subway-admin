package nextstep.subway.line.application;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.dto.*;
import nextstep.subway.section.application.SectionService;
import nextstep.subway.section.domain.Section;
import nextstep.subway.section.dto.SectionAddRequest;
import nextstep.subway.section.dto.SectionAddResponse;
import nextstep.subway.station.application.StationService;
import nextstep.subway.station.domain.Station;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class LineService {
    private LineRepository lineRepository;
    private SectionService sectionService;
    private StationService stationService;

    public LineService(LineRepository lineRepository,
                       SectionService sectionService,
                       StationService stationService) {
        this.sectionService = sectionService;
        this.lineRepository = lineRepository;
        this.stationService = stationService;
    }

    public LineResponse saveLine(LineCreateRequest request) {
        Station upStation = stationService.findByStationId(request.getUpStationId());
        Station downStation = stationService.findByStationId(request.getDownStationId());
        Line savedLine = lineRepository.save(Line.of(request.getName(), request.getColor(), upStation, downStation, request.getDistance()));
        return LineResponse.of(savedLine);
    }

    public List<LineResponse> getLines() {
        return lineRepository.findAll()
                .stream()
                .map(LineResponse::of)
                .collect(Collectors.toList());
    }

    public LineStationResponse getOne(Long id) {
        Line line = lineRepository.getWithStations(id);
        return LineStationResponse.of(line, line.getStations());
    }


    public LineResponse modify(Long id, LineUpdateRequest lineRequest) {
        Line modifyingItem = lineRepository.getOne(id);
        modifyingItem.update(lineRequest.getName(), lineRequest.getColor());
        return LineResponse.of(modifyingItem);
    }

    public void delete(Long id) {
        Line deletingItem = lineRepository.getOne(id);
        sectionService.delete(deletingItem.getSections());
        lineRepository.delete(lineRepository.getOne(id));
    }

    public SectionAddResponse addSection(Long lineId, SectionAddRequest sectionAddRequest) {
        Line addingLine = lineRepository.getOne(lineId);
        SectionAddResponse response = sectionService.addSection(addingLine, sectionAddRequest);
        return response;
    }

    public void deleteStation(Long lineId, Long stationId) {
        Line deletingLine = lineRepository.getOne(lineId);
        Station deletingStation = stationService.getOne(stationId);
        List<Section> sections = deletingLine.getSections();
        if (deletingLine.getStations().stream().noneMatch(item -> item == deletingStation)) {
            throw new RuntimeException();
        }
        if (deletingLine.getSections().size() == 1) {
            throw new RuntimeException();
        }
        Optional<Section> upSection = sections.stream()
                .filter(item -> item.getDown() == (deletingStation))
                .findFirst();
        Optional<Section> downSection = sections.stream()
                .filter(item -> item.getUp() == (deletingStation))
                .findFirst();
        if (upSection.isPresent() && downSection.isPresent()) {
            downSection.get().mergeUp(upSection.get());
            deletingLine.getSections().remove(upSection.get());
            return;
        }
        if (downSection.isPresent()) {
            deletingLine.getSections().remove(downSection.get());
            deletingLine.getSections().get(0).setStart();
            return;
        }
        if (upSection.isPresent()) {
            deletingLine.getSections().remove(upSection.get());
            return;
        }
    }
}
