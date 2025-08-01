package org.example.project2.mapper;

import org.example.project2.domain.Admin;
import org.example.project2.dto.AdminDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface AdminMapper {
    
    AdminMapper INSTANCE = Mappers.getMapper(AdminMapper.class);
    
    AdminDto toDto(Admin admin);
    
    Admin toEntity(AdminDto adminDto);
} 