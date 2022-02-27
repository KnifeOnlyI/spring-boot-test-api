package fr.koi.testapi.mapper;

import fr.koi.testapi.domain.UserEntity;
import fr.koi.testapi.model.user.UserRegister;
import org.mapstruct.Mapper;
import org.springframework.stereotype.Service;

/**
 * The mapper for users
 */
@Service
@Mapper(componentModel = "spring")
public interface UserMapper {
    /**
     * Convert a public model to entity
     *
     * @param model The public model to convert
     *
     * @return The corresponding entity
     */
    UserEntity toEntity(UserRegister model);
}
