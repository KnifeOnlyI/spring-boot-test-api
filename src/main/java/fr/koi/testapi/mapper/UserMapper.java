package fr.koi.testapi.mapper;

import fr.koi.testapi.domain.UserEntity;
import fr.koi.testapi.util.DateFormatter;
import fr.koi.testapi.web.model.user.UserModel;
import fr.koi.testapi.web.model.user.UserRegisterModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * The mapper for users
 */
@Mapper(componentModel = "spring", uses = {DateFormatter.class})
public interface UserMapper {
    /**
     * Convert a public model to entity
     *
     * @param model The public model to convert
     *
     * @return The corresponding entity
     */
    UserEntity toEntity(UserRegisterModel model);

    /**
     * Convert an entity to model
     *
     * @param entity The entity to convert
     *
     * @return The corresponding model
     */
    @Mapping(target = "createdAt", source = "createdAt", qualifiedBy = DateFormatter.String.class)
    UserModel toModel(UserEntity entity);
}
