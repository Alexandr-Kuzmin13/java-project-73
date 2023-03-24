package hexlet.code.service;

import hexlet.code.dto.UserDto;
import hexlet.code.model.User;

import java.util.List;

public interface UserService {

    User findById(long id);

    List<User> findAll();

    User create(UserDto userDto);

    User update(long id, UserDto userDto);

    void deleteById(long id);

    String getCurrentUserName();

    User getCurrentUser();
}
