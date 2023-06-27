package at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.UserCreateDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.UserDeleteDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.UserDetailDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.UserRegisterDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.UserUnBlockDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.ApplicationUser;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;

@Mapper
public interface UserMapper {


    @Named("user")
    @Mapping(target = "id", source = "id")
    UserRegisterDto entityToDto(ApplicationUser user);

    @Named("user")
    @Mapping(target = "id", source = "id")
    UserCreateDto entityToUserCreateDto(ApplicationUser user);

    @Named("user")
    @Mapping(target = "id", source = "id")
    UserDetailDto entityToUserDetailDto(ApplicationUser user);

    @Named("user")
    @Mapping(target = "email", source = "email")
    UserDeleteDto entityToUserDeleteDto(ApplicationUser user);

    @Mapping(target = "email", source = "email")
    @Mapping(target = "isLocked", source = "locked")
    UserUnBlockDto entityToUserUnBlockDto(ApplicationUser applicationUser);

    @Mapping(target = "email", source = "email")
    @Mapping(target = "isLocked", source = "locked")
    List<UserUnBlockDto> entityToStreamUserUnBlockDto(List<ApplicationUser> applicationUserStream);

    @Mapping(target = "id", source = "id")
    ApplicationUser dtoToEntity(UserRegisterDto userRegisterDto);

    @Mapping(target = "id", source = "id")
    ApplicationUser userCreateDtoToEntity(UserCreateDto userCreateDto);

    @Mapping(target = "id", source = "id")
    ApplicationUser userDetailDtoToEntity(UserDetailDto userDetailDto);

    @Mapping(target = "email", source = "email")
    @Mapping(target = "locked", source = "isLocked")
    ApplicationUser userUnBlockDtoToEntity(UserUnBlockDto userUnBlockDto);

    @Mapping(target = "email", source = "email")
    ApplicationUser userDeleteDtoToEntity(UserDeleteDto userDeleteDto);


}
