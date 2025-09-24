package mate.academy.carsharing.mapper;

import mate.academy.carsharing.config.MapperConfig;
import mate.academy.carsharing.dto.UserRequestDto;
import mate.academy.carsharing.dto.UserResponseDto;
import mate.academy.carsharing.model.User;
import org.mapstruct.Mapper;

@Mapper(config = MapperConfig.class)
public interface UserMapper {
    UserResponseDto userToUserDto(User user);

    User requestDtoToUser(UserRequestDto userRequestDto);
}
