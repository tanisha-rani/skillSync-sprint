package com.skillsync.sessionservice.service.impl;

import com.skillsync.sessionservice.client.MentorDto;
import com.skillsync.sessionservice.client.MentorFeignClient;
import com.skillsync.sessionservice.client.NotificationFeignClient;
import com.skillsync.sessionservice.client.NotificationRequestDto;
import com.skillsync.sessionservice.client.UserDto;
import com.skillsync.sessionservice.client.UserFeignClient;
import com.skillsync.sessionservice.dto.SessionRequestDto;
import com.skillsync.sessionservice.dto.SessionResponseDto;
import com.skillsync.sessionservice.entity.Session;
import com.skillsync.sessionservice.entity.SessionStatus;
import com.skillsync.sessionservice.exception.SessionNotFoundException;
import com.skillsync.sessionservice.repository.SessionRepository;
import com.skillsync.sessionservice.service.SessionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class SessionServiceImpl implements SessionService {

    private final SessionRepository sessionRepository;
    private final ModelMapper modelMapper;

    private final NotificationFeignClient notificationFeignClient;
    private final MentorFeignClient mentorFeignClient;
    private final UserFeignClient userFeignClient;

    @Override
    public SessionResponseDto bookSession(SessionRequestDto requestDto) {
        // Validate mentor exists and is available before booking
        MentorDto mentor = mentorFeignClient.getMentorById(requestDto.getMentorId());
        if (!mentor.isAvailable()) {
            String message = "Mentor " + requestDto.getMentorId() + " is not currently available";
            if (mentor.getStatus() != null && !"APPROVED".equalsIgnoreCase(mentor.getStatus())) {
                message = "Mentor " + requestDto.getMentorId()
                        + " cannot be booked because they are not approved yet (status: "
                        + mentor.getStatus() + ")";
            }
            throw new IllegalStateException(message);
        }

        String requiredSkill = requestDto.getRequiredSkill() == null ? "" : requestDto.getRequiredSkill().trim();
        boolean skillMatches = mentor.getSkills() != null && mentor.getSkills().stream()
                .filter(skill -> skill != null && !skill.isBlank())
                .anyMatch(skill -> skill.trim().toLowerCase(Locale.ROOT).equals(requiredSkill.toLowerCase(Locale.ROOT)));

        if (!skillMatches) {
            throw new IllegalStateException(
                    "This mentor cannot be booked for skill \"" + requiredSkill + "\". Please choose one of their listed skills."
            );
        }

        UserDto mentorUser = safeGetUserById(mentor.getUserId(), "Mentor");
        UserDto learnerUser = safeGetUserById(requestDto.getLearnerId(), "Learner");

        Session session = modelMapper.map(requestDto, Session.class);
        session.setStatus(SessionStatus.REQUESTED);
        Session saved = sessionRepository.save(session);

        // Notify mentor of new booking request
        notificationFeignClient.sendNotification(
                NotificationRequestDto.builder()
                        .userId(mentor.getUserId())
                        .recipientEmail(mentorUser.getEmail())
                        .subject("New session request")
                        .message("You have a new session request from "
                                + learnerUser.getName()
                                + " for "
                                + saved.getRequiredSkill()
                                + " on topic: "
                                + saved.getTopic())
                        .type("SESSION_BOOKED")
                        .referenceId(saved.getId())
                        .build()
        );

        return toResponseDto(saved);
    }



    @Override
    public SessionResponseDto acceptSession(Long id) {
        log.info("Accepting session id={}", id);
        Session session = findSessionOrThrow(id);

        if (session.getStatus() != SessionStatus.REQUESTED) {
            throw new IllegalStateException(
                    "Cannot accept a session that is in state: " + session.getStatus()
            );
        }

        session.setStatus(SessionStatus.ACCEPTED);
        Session updatedSession = sessionRepository.save(session);
        UserDto learnerUser = safeGetUserById(session.getLearnerId(), "Learner");
        MentorDto mentor = mentorFeignClient.getMentorById(session.getMentorId());
        UserDto mentorUser = safeGetUserById(mentor.getUserId(), "Mentor");

        notificationFeignClient.sendNotification(
                NotificationRequestDto.builder()
                        .userId(session.getLearnerId())
                        .recipientEmail(learnerUser.getEmail())
                        .subject("Session confirmed by mentor")
                        .message("Your session for "
                                + session.getRequiredSkill()
                                + " on topic "
                                + session.getTopic()
                                + " has been accepted by "
                                + mentorUser.getName()
                                + ".")
                        .type("SESSION_ACCEPTED")
                        .referenceId(session.getId())
                        .build()
        );

        return toResponseDto(updatedSession);
    }

    @Override
    public SessionResponseDto rejectSession(Long id) {
        log.info("Rejecting session id={}", id);
        Session session = findSessionOrThrow(id);

        if (session.getStatus() != SessionStatus.REQUESTED) {
            throw new IllegalStateException(
                    "Cannot reject a session that is in state: " + session.getStatus()
            );
        }

        session.setStatus(SessionStatus.REJECTED);
        Session updatedSession = sessionRepository.save(session);
        UserDto learnerUser = safeGetUserById(session.getLearnerId(), "Learner");
        MentorDto mentor = mentorFeignClient.getMentorById(session.getMentorId());
        UserDto mentorUser = safeGetUserById(mentor.getUserId(), "Mentor");

        notificationFeignClient.sendNotification(
                NotificationRequestDto.builder()
                        .userId(session.getLearnerId())
                        .recipientEmail(learnerUser.getEmail())
                        .subject("Session request declined")
                        .message("Your session request for "
                                + session.getRequiredSkill()
                                + " on topic "
                                + session.getTopic()
                                + " was declined by "
                                + mentorUser.getName()
                                + ".")
                        .type("SESSION_REJECTED")
                        .referenceId(session.getId())
                        .build()
        );

        return toResponseDto(updatedSession);
    }

    @Override
    public SessionResponseDto cancelSession(Long id) {
        log.info("Cancelling session id={}", id);
        Session session = findSessionOrThrow(id);

        if (session.getStatus() == SessionStatus.COMPLETED
                || session.getStatus() == SessionStatus.CANCELLED) {
            throw new IllegalStateException(
                    "Cannot cancel a session that is in state: " + session.getStatus()
            );
        }

        session.setStatus(SessionStatus.CANCELLED);
        return toResponseDto(sessionRepository.save(session));
    }

    @Override
    public SessionResponseDto completeSession(Long id) {
        log.info("Completing session id={}", id);
        Session session = findSessionOrThrow(id);

        if (session.getStatus() != SessionStatus.ACCEPTED) {
            throw new IllegalStateException(
                    "Cannot complete a session that is in state: " + session.getStatus()
            );
        }

        session.setStatus(SessionStatus.COMPLETED);
        return toResponseDto(sessionRepository.save(session));
    }

    // ─── Read Operations ──────────────────────────────────────────────────────────

    @Override
    @Transactional(readOnly = true)
    public SessionResponseDto getSessionById(Long id) {
        log.debug("Fetching session id={}", id);
        return toResponseDto(findSessionOrThrow(id));
    }

    /**
     * Treats userId as both learnerId and mentorId —
     * returns all sessions where the user participates in either role.
     * Uses the existing findByLearnerIdOrMentorId() repository method.
     */
    @Override
    @Transactional(readOnly = true)
    public List<SessionResponseDto> getSessionsByUserId(Long userId) {
        log.debug("Fetching sessions for userId={}", userId);
        return sessionRepository.findByLearnerIdOrMentorId(userId, userId)
                .stream()
                .map(this::toResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<SessionResponseDto> getSessionsByMentorId(Long mentorId) {
        log.debug("Fetching sessions for mentorId={}", mentorId);
        return sessionRepository.findByMentorId(mentorId)
                .stream()
                .map(this::toResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<SessionResponseDto> getAllSessions(int page, int size, String sortBy) {
        log.debug("Fetching all sessions: page={}, size={}, sortBy={}", page, size, sortBy);
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        return sessionRepository.findAll(pageable)
                .map(this::toResponseDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<SessionResponseDto> getSessionsByStatus(
            SessionStatus status, int page, int size, String sortBy) {
        log.debug("Fetching sessions by status={}: page={}, size={}", status, page, size);
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        return sessionRepository.findByStatus(status, pageable)
                .map(this::toResponseDto);
    }

    // ─── Private Helper ───────────────────────────────────────────────────────────

    private Session findSessionOrThrow(Long id) {
        return sessionRepository.findById(id)
                .orElseThrow(() -> new SessionNotFoundException(id));
    }

    private UserDto safeGetUserById(Long userId, String fallbackPrefix) {
        try {
            return userFeignClient.getUserById(userId);
        } catch (Exception exception) {
            log.warn("Unable to fetch user {} from user-service: {}", userId, exception.getMessage());
            UserDto fallback = new UserDto();
            fallback.setId(userId);
            fallback.setName(fallbackPrefix + " #" + userId);
            fallback.setEmail("");
            return fallback;
        }
    }

    private SessionResponseDto toResponseDto(Session session) {
        SessionResponseDto responseDto = modelMapper.map(session, SessionResponseDto.class);
        UserDto learnerUser = safeGetUserById(session.getLearnerId(), "Learner");
        responseDto.setLearnerName(learnerUser.getName());

        try {
            MentorDto mentor = mentorFeignClient.getMentorById(session.getMentorId());
            UserDto mentorUser = safeGetUserById(mentor.getUserId(), "Mentor");
            responseDto.setMentorName(mentorUser.getName());
        } catch (Exception exception) {
            log.warn("Unable to fetch mentor {} from mentor-service: {}", session.getMentorId(), exception.getMessage());
            responseDto.setMentorName("Mentor #" + session.getMentorId());
        }

        return responseDto;
    }
}
