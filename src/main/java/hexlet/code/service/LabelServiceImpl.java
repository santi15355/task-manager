package hexlet.code.service;

import hexlet.code.dto.LabelDto;
import hexlet.code.mapper.LabelMapper;
import hexlet.code.model.Label;
import hexlet.code.repository.LabelRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@AllArgsConstructor
public class LabelServiceImpl implements LabelService {

    private final LabelRepository labelRepository;

    private final LabelMapper labelMapper;

    @Override
    public Label createNewLabel(final LabelDto labelDto) {
        return labelRepository.save(labelMapper.createNewLabel(labelDto));
    }

    @Override
    public Label updateLabel(final long id, final LabelDto labelDto) {
        return labelRepository.save(labelMapper.updateLabel(id, labelDto));
    }

}
