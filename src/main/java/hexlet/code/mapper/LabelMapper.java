package hexlet.code.mapper;

import hexlet.code.dto.LabelDto;
import hexlet.code.model.Label;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class LabelMapper {
    public Label mapToLabel(final LabelDto labelDto) {
        Label label = new Label();
        return mapToLabel(label, labelDto);
    }

    public Label mapToLabel(final Label label, LabelDto labelDto) {
        label.setName(labelDto.getName());
        return label;
    }
}
