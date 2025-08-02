package org.example.project2.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.project2.client.NotificationClient;
import org.example.project2.config.Messages;
import org.example.project2.domain.Student;
import org.example.project2.dto.NotificationDto;
import org.example.project2.dto.StudentDto;
import org.example.project2.enumeration.NotificationType;
import org.example.project2.exception.CustomFeignClientException;
import org.example.project2.exception.FunctionalException;
import org.example.project2.exception.TechnicalException;
import org.example.project2.exception.EmailConflictException;
import org.example.project2.mapper.StudentMapper;
import org.example.project2.repository.StudentRepository;
import org.example.project2.util.consts.GlobalConstants;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;
import java.util.UUID;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@Service
@RequiredArgsConstructor
@Slf4j
public class StudentService {
    private final NotificationClient notificationClient;
    private final StudentRepository studentRepository;
    private final StudentMapper studentMapper;
    private final Messages messages;

    public List<StudentDto> getAll(int page, int size, String sort) {
        log.info("Start Service: getAll with page={}, size={}, sort={}", page, size, sort);
        
        // Create pageable for pagination
        Pageable pageable = PageRequest.of(page, size);
        
        // Apply sorting if provided
        if (sort != null && !sort.isEmpty()) {
            String[] sortParts = sort.split(",");
            if (sortParts.length == 2) {
                String field = sortParts[0];
                String direction = sortParts[1];
                
                Sort.Direction sortDirection = "desc".equalsIgnoreCase(direction) 
                    ? Sort.Direction.DESC 
                    : Sort.Direction.ASC;
                
                pageable = PageRequest.of(page, size, Sort.by(sortDirection, field));
            }
        }
        
        final var result = studentRepository.findAll(pageable)
                .getContent()
                .stream()
                .map(studentMapper::toDto)
                .toList();
        log.info("End Service: getAll with {} students", result.size());
        return result;
    }

    public StudentDto create(StudentDto studentDto) {
        log.info("Start Service: create");
        
        // Check if email already exists
        if (studentRepository.existsByEmail(studentDto.email())) {
            throw new EmailConflictException("email.conflict");
        }
        
        final var student = studentRepository.save(studentMapper.toEntity(studentDto));
        final var result = studentMapper.toDto(student);
        log.debug("Student DTO ID after save: {}", result.id());  // Add this

        // Send notification asynchronously - don't fail the operation if notification fails
        try {
            notificationClient.send(
                    NotificationDto.builder()
                            .studentId(result.id())
                            .message(messages.get(GlobalConstants.NOTIFICATION_STUDENT_CREATED, new Object[]{result.fullName()}, Locale.FRENCH))
                            .type(NotificationType.CREATE)
                            .build()
            );
        } catch (Exception e) {
            log.warn("Failed to send notification for student creation: {}", e.getMessage());
            // Don't throw exception - student was created successfully
        }
        
        log.info("End Service: create with student id {}", result.id());
        return result;
    }

    public StudentDto getById(UUID id) {
        log.info("Start Service: getById with student id {}", id);
        final var result = studentRepository.findById(id)
                .map(studentMapper::toDto)
                .orElseThrow(() -> new FunctionalException(
                        messages.get(GlobalConstants.ERROR_WS_STUDENT_NOT_FOUND, new Object[]{id.toString()}, Locale.FRENCH)
                ));
        log.info("End Service: getById with student id {}", result.id());
        return result;
    }

    public StudentDto update(UUID id, StudentDto studentDto) {
        log.info("Start Service: update with student id {}", id);
        if (!studentRepository.existsById(id)) {
            throw new FunctionalException(
                    messages.get(GlobalConstants.ERROR_WS_STUDENT_NOT_FOUND, new Object[]{id.toString()}, Locale.FRENCH)
            );
        }

        // Check if email already exists for another student
        var existingStudent = studentRepository.findByEmail(studentDto.email());
        if (existingStudent.isPresent() && !existingStudent.get().getId().equals(id)) {
            throw new EmailConflictException("email.conflict");
        }

        // Get the existing student and update its fields
        var existingStudentEntity = studentRepository.findById(id)
                .orElseThrow(() -> new FunctionalException(
                        messages.get(GlobalConstants.ERROR_WS_STUDENT_NOT_FOUND, new Object[]{id.toString()}, Locale.FRENCH)
                ));
        
        // Update the existing entity with new values
        existingStudentEntity.setFullName(studentDto.fullName());
        existingStudentEntity.setEmail(studentDto.email());
        existingStudentEntity.setPhoneNumber(studentDto.phoneNumber());
        existingStudentEntity.setAddress(studentDto.address());
        
        final var student = studentRepository.save(existingStudentEntity);
        final var result = studentMapper.toDto(student);

        // Send notification asynchronously - don't fail the operation if notification fails
        try {
            notificationClient.send(
                    NotificationDto.builder()
                            .studentId(result.id())
                            .message(messages.get(GlobalConstants.NOTIFICATION_STUDENT_UPDATED, new Object[]{result.fullName()}, Locale.FRENCH))
                            .type(NotificationType.UPDATE)
                            .build()
            );
        } catch (Exception e) {
            log.warn("Failed to send notification for student update: {}", e.getMessage());
            // Don't throw exception - student was updated successfully
        }
        
        log.info("End Service: update with student id {}", result.id());
        return result;
    }

    public void delete(UUID id) {
        log.info("Start Service: delete with student id {}", id);
        final var studentFullName = studentRepository.findById(id)
                .map(Student::getFullName)
                .orElseThrow(() -> new FunctionalException(
                        messages.get(GlobalConstants.ERROR_WS_STUDENT_NOT_FOUND, new Object[]{id.toString()}, Locale.FRENCH)
                ));

        // Send notification asynchronously - don't fail the operation if notification fails
        try {
            notificationClient.send(
                    NotificationDto.builder()
                            .studentId(id)
                            .message(messages.get(GlobalConstants.NOTIFICATION_STUDENT_DELETED, new Object[]{studentFullName}, Locale.FRENCH))
                            .type(NotificationType.DELETE)
                            .build()
            );
        } catch (Exception e) {
            log.warn("Failed to send notification for student deletion: {}", e.getMessage());
            // Don't throw exception - student was deleted successfully
        }

        // First, try to delete notifications for this student from the notification service
        try {
            notificationClient.deleteNotificationsByStudentId(id);
            log.info("Successfully deleted notifications for student id {}", id);
        } catch (Exception e) {
            log.warn("Failed to delete notifications for student id {}: {}", id, e.getMessage());
            // Continue with student deletion even if notification deletion fails
        }

        studentRepository.deleteById(id);

        log.info("End Service: delete with student id {}", id);
    }
}
