package org.example.project2.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.project2.dto.StudentDto;
import org.example.project2.exception.TechnicalException;
import org.example.project2.service.StudentService;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/students")
@RequiredArgsConstructor
@Slf4j
public class StudentApi {
    private final StudentService studentService;

    @Operation(
            summary = "Create Student",
            description = "Create a new student record",
            responses = {
                    @ApiResponse(
                            description = "Student created successfully",
                            responseCode = "201",
                            content = @Content(
                                    schema = @Schema(implementation = StudentDto.class)
                            )
                    ),
                    @ApiResponse(
                            description = "Bad Request",
                            responseCode = "400"
                    )
            }
    )
    @PostMapping
    public StudentDto create(@RequestBody StudentDto studentDto) throws TechnicalException {
        log.info("Start Api: create");
        final var result = studentService.create(studentDto);
        log.info("End Api: create with student id {}", studentDto.id());
        return result;
    }

    @Operation(
            summary = "Get Student by ID",
            description = "Retrieve a student record by its ID",
            responses = {
                    @ApiResponse(
                            description = "Student found",
                            responseCode = "200",
                            content = @Content(
                                    schema = @Schema(implementation = StudentDto.class)
                            )
                    ),
                    @ApiResponse(
                            description = "Student not found",
                            responseCode = "404"
                    )
            }
    )
    @GetMapping("{id}")
    public StudentDto getById(@PathVariable UUID id) {
        log.info("Start Api: getById with student id {}", id);
        final var result = studentService.getById(id);
        log.info("End Api: getById with student id {}", id);
        return result;
    }

    @Operation(
            summary = "Delete Student",
            description = "Delete a student record by its ID",
            responses = {
                    @ApiResponse(
                            description = "Student deleted successfully",
                            responseCode = "204"
                    ),
                    @ApiResponse(
                            description = "Student not found",
                            responseCode = "404"
                    )
            }
    )
    @DeleteMapping("{id}")
    public void delete(@PathVariable UUID id) throws TechnicalException {
        log.info("Start Api: delete with student id {}", id);
        studentService.delete(id);
        log.info("End Api: delete with student id {}", id);
    }

    @Operation(
            summary = "Update Student",
            description = "Update an existing student record",
            responses = {
                    @ApiResponse(
                            description = "Student updated successfully",
                            responseCode = "200",
                            content = @Content(
                                    schema = @Schema(implementation = StudentDto.class)
                            )
                    ),
                    @ApiResponse(
                            description = "Student not found",
                            responseCode = "404"
                    ),
                    @ApiResponse(
                            description = "Bad Request",
                            responseCode = "400"
                    )
            }
    )
    @PutMapping("{id}")
    public StudentDto update(@PathVariable UUID id, @RequestBody StudentDto studentDto) throws TechnicalException {
        log.info("Start Api: update with student id {}", id);
        final var result = studentService.update(id, studentDto);
        log.info("End Api: update with student id {}", id);
        return result;
    }
}
