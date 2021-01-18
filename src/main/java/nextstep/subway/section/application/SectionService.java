package nextstep.subway.section.application;

import nextstep.subway.line.domain.Line;
import nextstep.subway.section.domain.Section;
import nextstep.subway.section.domain.SectionRepository;
import nextstep.subway.line.dto.LineSectionCreateRequest;
import nextstep.subway.section.dto.SectionAddRequest;
import nextstep.subway.section.dto.SectionAddResponse;
import nextstep.subway.station.application.StationService;
import nextstep.subway.station.domain.Station;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@Transactional
public class SectionService {


    private StationService stationService;
    private SectionRepository sectionRepository;

    public SectionService(StationService stationService, SectionRepository sectionRepository) {
        this.stationService = stationService;
        this.sectionRepository = sectionRepository;
    }

    public void delete(List<Section> sections) {
        sectionRepository.deleteAll(sections);

    }

    public void delete(Section section){
        sectionRepository.delete(section);

    }

    public SectionAddResponse addSection(Line addingLine, SectionAddRequest sectionAddRequest) {
        Station up = stationService.getOne(sectionAddRequest.getUpStationId());
        Station down = stationService.getOne(sectionAddRequest.getDownStationId());
        int distance = sectionAddRequest.getDistance();
        List<Section> sections = addingLine.getOrderedSections();
        if (down.isTopEnd(sections)) {
            return SectionAddResponse.of(sectionRepository.save(new Section(addingLine, up, down, distance, true)));
        }
        if (up.isBottomEnd(sections)) {
            return SectionAddResponse.of(sectionRepository.save(new Section(addingLine, up, down, distance)));
        }

        return sections
                .stream()
                .map(item -> item.createAndChange(up, down, distance))
                .filter(Objects::nonNull)
                .findFirst()
                .map(value -> SectionAddResponse.of(sectionRepository.save(value)))
                .orElseGet(() -> {
                    throw new IllegalArgumentException("Station is invalid");
                });
    }


}