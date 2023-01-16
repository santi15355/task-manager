package hexlet.code.mapper;

import hexlet.code.dto.LabelDto;
import hexlet.code.model.Label;
import hexlet.code.repository.LabelRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class LabelMapper {

    private final LabelRepository labelRepository;

    public Label createNewLabel(final LabelDto labelDto) {
        final Label label = new Label();
        label.setName(labelDto.getName());
        return label;
    }

    public Label updateLabel(final long id, final LabelDto labelDto) {
        final Label labelToUpdate = labelRepository.findById(id).get();
        labelToUpdate.setName(labelDto.getName());
        return labelToUpdate;
    }
}
