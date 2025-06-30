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
import org.example.project2.mapper.StudentMapper;
import org.example.project2.repository.StudentRepository;
import org.example.project2.util.consts.GlobalConstants;
import org.springframework.stereotype.Service;

import java.util.Locale;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class StudentService {
    private final NotificationClient notificationClient;
    private final StudentRepository studentRepository;
    private final StudentMapper studentMapper;
    private final Messages messages;

    public StudentDto create(StudentDto studentDto) throws TechnicalException {
        log.info("Start Service: create");
        final var student = studentRepository.save(studentMapper.toEntity(studentDto));
        final var result = studentMapper.toDto(student);

        try {
            notificationClient.send(
                    NotificationDto.builder()
                            .studentId(result.id())
                            .message(messages.get(GlobalConstants.NOTIFICATION_STUDENT_CREATED, new Object[]{result.fullName()}, Locale.FRENCH))
                            .type(NotificationType.CREATE)
                            .build()
            );
        } catch (CustomFeignClientException e) {
            log.error("Error sending notification when creating a student: {}", e.getMessage());
            throw new TechnicalException(messages.get(GlobalConstants.ERROR_WS_NOTIFICATION_SEND, new Object[]{}, Locale.FRENCH));
        }
        log.info("End Service: create with student id {}", result.id());
        return result;
    }

    public StudentDto getById(UUID id) {
        log.info("Start Service: getById with student id {}", id);
        final var result = studentRepository.findById(id)
                .map(studentMapper::toDto)
                .orElseThrow(() -> new FunctionalException(
                        messages.get(GlobalConstants.ERROR_WS_STUDENT_NOT_FOUND, new Object[]{id}, Locale.FRENCH)
                ));
        log.info("End Service: getById with student id {}", result.id());
        return result;
    }

    public StudentDto update(UUID id, StudentDto studentDto) throws TechnicalException {
        log.info("Start Service: update with student id {}", id);
        if (!studentRepository.existsById(id)) {
            throw new FunctionalException(
                    messages.get(GlobalConstants.ERROR_WS_STUDENT_NOT_FOUND, new Object[]{id}, Locale.FRENCH)
            );
        }

        final var student = studentRepository.save(studentMapper.toEntity(studentDto.toBuilder().id(id).build()));
        final var result = studentMapper.toDto(student);

        try {
            notificationClient.send(
                    NotificationDto.builder()
                            .studentId(result.id())
                            .message(messages.get(GlobalConstants.NOTIFICATION_STUDENT_UPDATED, new Object[]{result.fullName()}, Locale.FRENCH))
                            .type(NotificationType.UPDATE)
                            .build()
            );
        } catch (CustomFeignClientException e) {
            log.error("Error sending notification when updating a student: {}", e.getMessage());
            throw new TechnicalException(messages.get(GlobalConstants.ERROR_WS_NOTIFICATION_SEND, new Object[]{}, Locale.FRENCH));
        }
        log.info("End Service: update with student id {}", result.id());
        return result;
    }

    public void delete(UUID id) throws TechnicalException {
        log.info("Start Service: delete with student id {}", id);
        final var studentFullName = studentRepository.findById(id)
                .map(Student::getFullName)
                .orElseThrow(() -> new FunctionalException(
                        messages.get(GlobalConstants.ERROR_WS_STUDENT_NOT_FOUND, new Object[]{id}, Locale.FRENCH)
                ));
        studentRepository.deleteById(id);

        try {
            notificationClient.send(
                    NotificationDto.builder()
                            .message(messages.get(GlobalConstants.NOTIFICATION_STUDENT_DELETED, new Object[]{studentFullName}, Locale.FRENCH))
                            .type(NotificationType.DELETE)
                            .build()
            );
        } catch (CustomFeignClientException e) {
            log.error("Error sending notification when deleting a student: {}", e.getMessage());
            throw new TechnicalException(messages.get(GlobalConstants.ERROR_WS_NOTIFICATION_SEND, new Object[]{}, Locale.FRENCH));
        }
        log.info("End Service: delete with student id {}", id);
    }
}
