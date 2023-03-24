package hexlet.code.service;

import hexlet.code.dto.LabelDto;
import hexlet.code.model.Label;
import hexlet.code.repository.LabelRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@AllArgsConstructor
public class LabelServiceImpl implements LabelService {

    private final LabelRepository labelRepository;

    @Override
    public Label findById(long id) {
        return labelRepository.findById(id).get();
    }

    @Override
    public List<Label> findAll() {
        return labelRepository.findAll();
    }

    @Override
    public Label create(LabelDto dto) {
        final Label label = new Label();
        label.setName(dto.getName());
        return labelRepository.save(label);
    }

    @Override
    public Label update(long id, LabelDto dto) {
        final Label labelToUpdate = labelRepository.findById(id).get();
        labelToUpdate.setName(dto.getName());
        return labelRepository.save(labelToUpdate);
    }

    @Override
    public void deleteById(long id) {
        labelRepository.deleteById(id);
    }
}
