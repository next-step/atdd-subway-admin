package nextstep.subway.generic.domain.distance;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter(autoApply = true)
public class DistanceConverter implements AttributeConverter<Distance, Long> {

    @Override
    public Long convertToDatabaseColumn(final Distance attribute) {
        return attribute.getValue();
    }

    @Override
    public Distance convertToEntityAttribute(final Long dbData) {
        return Distance.valueOf(dbData);
    }
}
