package org.example.project2.mapper;

import org.example.project2.domain.Student;
import org.example.project2.dto.StudentDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface StudentMapper {
    StudentDto toDto(Student student);
    Student toEntity(StudentDto studentDto);
}
