package nextstep.subway.section.application;

import nextstep.subway.common.exception.BadRequestException;
import nextstep.subway.common.exception.NotExistsLineIdException;
import nextstep.subway.common.exception.NotLastStationException;
import nextstep.subway.common.exception.NotExistsStationIdException;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.section.domain.Section;
import nextstep.subway.section.domain.SectionRepository;
import nextstep.subway.section.dto.SectionRequest;
import nextstep.subway.section.dto.SectionResponse;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Transactional
public class SectionService {
    private static final int DELETE_THRESHOLD = 1;

    private final LineRepository lineRepository;
    private final SectionRepository sectionRepository;
    private final StationRepository stationRepository;

    public SectionService(LineRepository lineRepository, SectionRepository sectionRepository, StationRepository stationRepository) {
        this.lineRepository = lineRepository;
        this.sectionRepository = sectionRepository;
        this.stationRepository = stationRepository;
    }

    public SectionResponse saveSection(SectionRequest request) {
        request.validate();
        Line line = lineRepository.findById(request.getLineId())
                .orElseThrow(() -> new NotExistsLineIdException(request.getLineId()));
        Station upStation = stationRepository.findById(request.getUpStationId())
                .orElseThrow(() -> new NotExistsStationIdException(request.getUpStationId()));
        Station downStation = stationRepository.findById(request.getDownStationId())
                .orElseThrow(() -> new NotExistsStationIdException(request.getDownStationId()));

        Integer distance = request.getDistance();
        Integer sectionNumber = getSectionNumber(line.getId(), upStation, downStation, distance);
        Section persistSection = sectionRepository.save(new Section(line, upStation, downStation, distance, sectionNumber));
        return SectionResponse.of(persistSection);
    }

    public List<SectionResponse> findAll(Long lineId) {
        List<Section> sections = sectionRepository.findByLineId(lineId, Sort.by(Order.asc("sectionNumber")));
        return sections.stream()
                .map(SectionResponse::of)
                .collect(Collectors.toList());
    }

    public void delete(Long lineId, Long stationId) {
        List<Section> sections = sectionRepository.findByLineId(lineId, Sort.by(Order.asc("sectionNumber")));
        if ( sections.size() <= DELETE_THRESHOLD ) {
            throw new BadRequestException("section count must greater than 1");
        }

        Section deleteSection = findDeleteSection(sections, stationId);
        try {
            updateDistance(sections, stationId);
        } catch (NotLastStationException ignored){}

        decreaseGreaterThanSectionNumbers(sections, deleteSection.getSectionNumber());
        sectionRepository.deleteById(deleteSection.getId());
    }

    private Section findDeleteSection(List<Section> sections, Long stationId) {
        Section deleteSection = findDownSection(sections, stationId)
                .orElseGet(findUpSection(sections, stationId)::get);
        if ( deleteSection == null ) {
            throw new BadRequestException("Not found delete section, contains station : " + stationId);
        }
        return deleteSection;
    }

    private Optional<Section> findDownSection(List<Section> sections, Long stationId) {
        return sections.stream()
                .filter(section -> section.equalsUpStationId(stationId))
                .findFirst();
    }

    private Optional<Section> findUpSection(List<Section> sections, Long stationId) {
        return sections.stream()
                .filter(section -> section.equalsDownStationId(stationId))
                .findFirst();
    }

    private void updateDistance(List<Section> sections, Long stationId) {
        Section downSection = findDownSection(sections, stationId)
                .orElseThrow(() -> new NotLastStationException("is not last station : " + stationId));
        Section upSection = findUpSection(sections, stationId)
                .orElseThrow(() -> new NotLastStationException("is not last station : " + stationId));

        upSection.addDistance(downSection.getDistance());
        upSection.updateDownStation(downSection.getDownStation());
    }

    private void decreaseGreaterThanSectionNumbers(List<Section> sections, Integer sectionNumber) {
        sections.stream()
                .filter(section -> section.greaterThanSectionNumber(sectionNumber))
                .forEach(Section::decreaseSectionNumber);
    }

    private Integer getSectionNumber(Long lineId, Station upStation, Station downStation, Integer distance) {
        checkDuplicateAndThrow(lineId, upStation.getId(), downStation.getId());
        AddSectionType addSectionType = AddSectionType.select(sectionRepository, lineId, upStation.getId(), downStation.getId());
        return addSectionType.updateAndGetSectionNumber(sectionRepository, lineId, upStation, downStation, distance);
    }

    private void checkDuplicateAndThrow(Long lineId, Long upStationId, Long downStationId) {
        Optional<Section> duplicationSection = sectionRepository.findByLineIdAndUpStationIdAndDownStationId(lineId, upStationId, downStationId);
        if ( duplicationSection.isPresent() ) {
            throw new BadRequestException("Section is already exists");
        }
    }

    private enum AddSectionType {
        CREATED {
            @Override
            public Integer updateAndGetSectionNumber(SectionRepository sectionRepository, Long lineId,
                                                     Station upStation, Station downStation, Integer distance) {
                return INIT_SECTION_NUMBER;
            }

            @Override
            protected boolean checkType(SectionRepository sectionRepository, Long lineId, Long upStationId, Long downStationId) {
                return sectionRepository.findByLineId(lineId)
                        .isEmpty();
            }
        },
        UP_INSERTION {
            @Override
            public Integer updateAndGetSectionNumber(SectionRepository sectionRepository, Long lineId,
                                                     Station upStation, Station downStation, Integer distance) {
                Section expectedSection = findSectionThrows(sectionRepository, lineId, upStation.getId(), downStation.getId());
                expectedSection.subtractDistance(distance);
                return updateAndGetSectionNumberUpCase(sectionRepository, expectedSection, lineId, downStation);
            }

            @Override
            protected Optional<Section> findSection(SectionRepository sectionRepository, Long lineId, Long upStationId, Long downStationId) {
                return sectionRepository.findByLineIdAndUpStationId(lineId, upStationId);
            }
        },
        UP_LAST {
            @Override
            public Integer updateAndGetSectionNumber(SectionRepository sectionRepository, Long lineId,
                                                     Station upStation, Station downStation, Integer distance) {
                Section expectedSection = findSectionThrows(sectionRepository, lineId, upStation.getId(), downStation.getId());
                return updateAndGetSectionNumberUpCase(sectionRepository, expectedSection, lineId, downStation);
            }

            @Override
            protected Optional<Section> findSection(SectionRepository sectionRepository, Long lineId, Long upStationId, Long downStationId) {
                return sectionRepository.findByLineIdAndUpStationId(lineId, downStationId);
            }
        },
        DOWN_INSERTION {
            @Override
            public Integer updateAndGetSectionNumber(SectionRepository sectionRepository, Long lineId,
                                                     Station upStation, Station downStation, Integer distance) {
                Section expectedSection = findSectionThrows(sectionRepository, lineId, upStation.getId(), downStation.getId());
                expectedSection.subtractDistance(distance);
                return updateAndGetSectionNumberDownCase(sectionRepository, expectedSection, lineId, upStation);
            }

            @Override
            protected Optional<Section> findSection(SectionRepository sectionRepository, Long lineId, Long upStationId, Long downStationId) {
                return sectionRepository.findByLineIdAndDownStationId(lineId, downStationId);
            }
        },
        DOWN_LAST {
            @Override
            public Integer updateAndGetSectionNumber(SectionRepository sectionRepository, Long lineId,
                                                     Station upStation, Station downStation, Integer distance) {
                Section expectedSection = findSectionThrows(sectionRepository, lineId, upStation.getId(), downStation.getId());
                return updateAndGetSectionNumberDownCase(sectionRepository, expectedSection, lineId, upStation);
            }

            @Override
            protected Optional<Section> findSection(SectionRepository sectionRepository, Long lineId, Long upStationId, Long downStationId) {
                return sectionRepository.findByLineIdAndDownStationId(lineId, upStationId);
            }
        },
        NOT_RELATED,
        ;
        private static final Integer INIT_SECTION_NUMBER = 1;

        public Integer updateAndGetSectionNumber(SectionRepository sectionRepository, Long lineId, Station upStation, Station downStation, Integer distance) {
            throw new BadRequestException("Not found related station");
        }

        public static AddSectionType select(SectionRepository sectionRepository, Long lineId, Long upStationId, Long downStationId) {
            return Stream.of(AddSectionType.values())
                    .filter(type -> type != NOT_RELATED && type.checkType(sectionRepository, lineId, upStationId, downStationId))
                    .findFirst()
                    .orElse(NOT_RELATED);
        }

        protected void incrementSectionNumberGreaterThanEqual(SectionRepository sectionRepository, Long lineId, Integer sectionNumber) {
            List<Section> sections = sectionRepository.findByLineIdAndSectionNumberGreaterThanEqual(lineId, sectionNumber);
            sections.forEach(Section::incrementSectionNumber);
        }

        protected Section findSectionThrows(SectionRepository sectionRepository, Long lineId, Long upStationId, Long downStationId) {
            return findSection(sectionRepository, lineId, upStationId, downStationId)
                    .orElseThrow(() -> new BadRequestException("Not found section"));
        }

        protected Integer updateAndGetSectionNumberUpCase(SectionRepository sectionRepository, Section expectedSection,
                                                          Long lineId, Station downStation) {
            expectedSection.updateUpStation(downStation);
            Integer sectionNumber = expectedSection.getSectionNumber();
            incrementSectionNumberGreaterThanEqual(sectionRepository, lineId, sectionNumber);
            return sectionNumber;
        }

        protected Integer updateAndGetSectionNumberDownCase(SectionRepository sectionRepository, Section expectedSection,
                                                            Long lineId, Station upStation) {
            expectedSection.updateDownStation(upStation);
            Integer sectionNumber = expectedSection.getSectionNumber() + 1;
            incrementSectionNumberGreaterThanEqual(sectionRepository, lineId, sectionNumber);
            return sectionNumber;
        }


        protected boolean checkType(SectionRepository sectionRepository, Long lineId, Long upStationId, Long downStationId) {
            return findSection(sectionRepository, lineId, upStationId, downStationId)
                    .isPresent();
        }

        protected Optional<Section> findSection(SectionRepository sectionRepository, Long lineId, Long upStationId, Long downStationId) {
            return Optional.empty();
        }
    }
}
