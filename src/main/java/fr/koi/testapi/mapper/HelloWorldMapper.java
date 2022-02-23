package fr.koi.testapi.mapper;

import fr.koi.testapi.domain.HelloWorld;
import fr.koi.testapi.resources.model.HelloWorldDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface HelloWorldMapper {
    HelloWorldDTO toDTO(HelloWorld entity);

    HelloWorld toEntity(HelloWorldDTO dto);
}
