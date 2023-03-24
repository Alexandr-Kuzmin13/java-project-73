package hexlet.code.service;

import hexlet.code.dto.LabelDto;
import hexlet.code.model.Label;

import java.util.List;

public interface LabelService {

    Label findById(long id);

    List<Label> findAll();

    Label create(LabelDto dto);

    Label update(long id, LabelDto dto);

    void deleteById(long id);
}
