package com.example.timetablemicroservice.service;

import com.example.timetablemicroservice.dto.AppointmentRequest;
import com.example.timetablemicroservice.dto.TimetableRequest;
import com.example.timetablemicroservice.dto.RoleValidationResponse;
import com.example.timetablemicroservice.dto.TimetableResponse;
import com.example.timetablemicroservice.exception.InvalidDataException;
import com.example.timetablemicroservice.exception.InvalidTokenException;
import com.example.timetablemicroservice.exception.TimetableNotFoundException;
import com.example.timetablemicroservice.model.AppointmentEntity;
import com.example.timetablemicroservice.model.TimetableEntity;
import com.example.timetablemicroservice.repository.AppointmentRepository;
import com.example.timetablemicroservice.repository.TimetableRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TimetableService {
    private final TimetableRepository timetableRepository;
    private final RabbitService rabbitService;
    private final AppointmentRepository appointmentRepository;

    private List<AppointmentEntity> generateAppointments(TimetableEntity timetable) {
        List<AppointmentEntity> appointments = new ArrayList<>();
        LocalDateTime currentTime = timetable.getStartTime();
        LocalDateTime endTime = timetable.getEndTime();

        while (currentTime.isBefore(endTime)) {
            AppointmentEntity appointment = AppointmentEntity.builder()
                    .timetableId(timetable.getId())
                    .appointmentTime(currentTime)
                    .build();
            appointments.add(appointment);
            currentTime = currentTime.plusMinutes(30);
        }

        return appointments;
    }


    private boolean isAllow(String token, List<String> roles) {
        RoleValidationResponse response = rabbitService.sendRoleValidationRequest(token.substring(7), roles);
        return response.isValid();
    }

    private boolean isDoctorExists(Long userId) {
        return rabbitService.isDoctorExists(userId);
    }

    private boolean isHospitalExists(Long hospitalId) {
        return rabbitService.isHospitalExists(hospitalId);
    }

    private boolean isRoomExists(Long hospitalId, String room) {
        return rabbitService.isRoomExists(room, hospitalId);
    }

    private Long getUserIdFromToken(String token){
        return rabbitService.getUserIdFromToken(token);
    }

    private boolean isTimetableExist(TimetableRequest request) {
        return timetableRepository.existsByDoctorIdAndStartTimeAndEndTime(
                request.getDoctorId(),
                request.getFrom(),
                request.getTo()
        );
    }

    public void createTimetable(TimetableRequest request, String token) {
        if (!isAllow(token, List.of("ADMIN", "MANAGER"))) {
            throw new InvalidTokenException("Invalid or expired token");
        }

        if (isTimetableExist(request)) {
            throw new InvalidDataException("Такая запись уже существует");
        }

        if (!isDoctorExists(request.getDoctorId())) {
            throw new InvalidDataException("Доктор с таким id не существует");
        }

        if (!isHospitalExists(request.getHospitalId())) {
            throw new InvalidDataException("Больница с таким id не существует");
        }

        if (!isRoomExists(request.getHospitalId(), request.getRoom())) {
            throw new InvalidDataException("В данной больнице нет комнаты с таким номером");
        }

        try {
            TimetableEntity timetable = TimetableEntity.builder()
                    .hospitalId(request.getHospitalId())
                    .doctorId(request.getDoctorId())
                    .room(request.getRoom())
                    .startTime(request.getFrom())
                    .endTime(request.getTo())
                    .build();

            timetableRepository.save(timetable);

            timetable = timetableRepository.save(timetable);


            List<AppointmentEntity> appointments = generateAppointments(timetable);
            appointmentRepository.saveAll(appointments);
        } catch (Exception e) {
            throw new InvalidDataException("Ошибка в данных");
        }
    }

    @Transactional
    public void updateTimetable(TimetableRequest request, String token, Long timetableId) {
        if (!isAllow(token, List.of("ADMIN", "MANAGER"))) {
            throw new InvalidTokenException("Invalid or expired token");
        }

        Optional<TimetableEntity> existingTimetableOpt = timetableRepository.findById(timetableId);
        if (!existingTimetableOpt.isPresent()) {
            throw new InvalidDataException("Запись расписания с таким ID не найдена");
        }
        TimetableEntity existingTimetable = existingTimetableOpt.get();


        boolean hasBookedAppointments = appointmentRepository.existsByTimetableIdAndPatientIdIsNotNull(timetableId);
        if (hasBookedAppointments) {
            throw new InvalidDataException("Нельзя обновить расписание, так как есть забронированные тайм-слоты");
        }

        if (!isDoctorExists(request.getDoctorId())) {
            throw new InvalidDataException("Доктор с таким id не существует");
        }

        if (!isHospitalExists(request.getHospitalId())) {
            throw new InvalidDataException("Больница с таким id не существует");
        }

        if (!isRoomExists(request.getHospitalId(), request.getRoom())) {
            throw new InvalidDataException("В данной больнице нет комнаты с таким номером");
        }

        if (timetableRepository.existsByDoctorIdAndStartTimeAndEndTimeAndIdNot(
                request.getDoctorId(),
                request.getFrom(),
                request.getTo(),
                timetableId)) {
            throw new InvalidDataException("Такая запись уже существует");
        }

        try {
            appointmentRepository.deleteByTimetableId(timetableId);

            existingTimetable.setHospitalId(request.getHospitalId());
            existingTimetable.setDoctorId(request.getDoctorId());
            existingTimetable.setRoom(request.getRoom());
            existingTimetable.setStartTime(request.getFrom());
            existingTimetable.setEndTime(request.getTo());

            timetableRepository.save(existingTimetable);

            List<AppointmentEntity> newAppointments = generateAppointments(existingTimetable);
            appointmentRepository.saveAll(newAppointments);
        } catch (Exception e) {
            throw new InvalidDataException("Ошибка при обновлении данных");
        }
    }

    public void deleteTimetable(Long timetableId, String token) {
        if (!isAllow(token, List.of("ADMIN", "MANAGER"))) {
            throw new InvalidTokenException("Invalid or expired token");
        }

        if (!timetableRepository.existsById(timetableId)) {
            throw new InvalidDataException("Запись расписания с таким ID не найдена");
        }

        boolean hasBookedAppointments = appointmentRepository.existsByTimetableIdAndPatientIdIsNotNull(timetableId);
        if (hasBookedAppointments) {
            throw new InvalidDataException("Нельзя удалить расписание, так как есть забронированные тайм-слоты");
        }

        appointmentRepository.deleteByTimetableId(timetableId);

        timetableRepository.deleteById(timetableId);
    }

    public void deleteTimetableByDoctor(Long doctorId, String token) {
        if (!isAllow(token, List.of("ADMIN", "MANAGER"))) {
            throw new InvalidTokenException("Invalid or expired token");
        }

        if (!isDoctorExists(doctorId)) {
            throw new InvalidDataException("Доктор с таким id не существует");
        }


        List<TimetableEntity> timetables = timetableRepository.findAllByDoctorId(doctorId);

        for (TimetableEntity timetable : timetables) {
            Long timetableId = timetable.getId();

            boolean hasBookedAppointments = appointmentRepository.existsByTimetableIdAndPatientIdIsNotNull(timetableId);
            if (hasBookedAppointments) {
                throw new InvalidDataException("Нельзя удалить расписание доктора, так как есть забронированные тайм-слоты");
            }

            appointmentRepository.deleteByTimetableId(timetableId);
        }

        timetableRepository.deleteByDoctorId(doctorId);
    }

    public void deleteTimetableByHospital(Long hospitalId, String token) {
        if (!isAllow(token, List.of("ADMIN", "MANAGER"))) {
            throw new InvalidTokenException("Invalid or expired token");
        }

        if (!isHospitalExists(hospitalId)) {
            throw new InvalidDataException("Больница с таким id не существует");
        }

        List<TimetableEntity> timetables = timetableRepository.findAllByHospitalId(hospitalId);

        for (TimetableEntity timetable : timetables) {
            Long timetableId = timetable.getId();

            boolean hasBookedAppointments = appointmentRepository.existsByTimetableIdAndPatientIdIsNotNull(timetableId);
            if (hasBookedAppointments) {
                throw new InvalidDataException("Нельзя удалить расписание больницы, так как есть забронированные тайм-слоты");
            }

            appointmentRepository.deleteByTimetableId(timetableId);
        }

        timetableRepository.deleteByHospitalId(hospitalId);
    }

    public List<TimetableResponse> getTimetableByHospital(Long hospitalId) {
       List<TimetableEntity> timetableEntityList = timetableRepository.findAllByHospitalId(hospitalId);

       if(timetableEntityList == null){
           throw new TimetableNotFoundException("Расписание для данной больницы не найдено");
       }

       return timetableEntityList.stream()
                .map(timetableEntity ->  TimetableResponse.builder()
                        .hospitalId(timetableEntity.getHospitalId())
                        .doctorId(timetableEntity.getDoctorId())
                        .to(timetableEntity.getEndTime())
                        .from(timetableEntity.getStartTime())
                        .room(timetableEntity.getRoom())
                        .build())
                .collect(Collectors.toList());
    }

    public List<TimetableResponse> getTimetableByDoctor(Long doctorId) {
        List<TimetableEntity> timetableEntityList = timetableRepository.findAllByDoctorId(doctorId);

        if(timetableEntityList == null){
            throw new TimetableNotFoundException("Расписание для данного доктора не найдено");
        }

        return timetableEntityList.stream()
                .map(timetableEntity ->  TimetableResponse.builder()
                        .hospitalId(timetableEntity.getHospitalId())
                        .doctorId(timetableEntity.getDoctorId())
                        .to(timetableEntity.getEndTime())
                        .from(timetableEntity.getStartTime())
                        .room(timetableEntity.getRoom())
                        .build())
                .collect(Collectors.toList());
    }

    public List<TimetableResponse> getTimetableByHospitalAndRoom(Long hospitalId, String room, String token) {
        if (!isAllow(token, List.of("ADMIN", "MANAGER", "DOCTOR"))) {
            throw new InvalidTokenException("Invalid or expired token");
        }

        List<TimetableEntity> timetableEntityList = timetableRepository.findAllByHospitalIdAndRoom(hospitalId, room);

        if(timetableEntityList == null){
            throw new TimetableNotFoundException("Расписание для данного кабинета не найдено");
        }

        return timetableEntityList.stream()
                .map(timetableEntity ->  TimetableResponse.builder()
                        .hospitalId(timetableEntity.getHospitalId())
                        .doctorId(timetableEntity.getDoctorId())
                        .to(timetableEntity.getEndTime())
                        .from(timetableEntity.getStartTime())
                        .room(timetableEntity.getRoom())
                        .build())
                .collect(Collectors.toList());
    }

    public List<LocalDateTime> getAvailableAppointments(Long timetableId) {
        TimetableEntity timetable = timetableRepository.findById(timetableId)
                .orElseThrow(() -> new TimetableNotFoundException("Расписание не найдено"));

        List<AppointmentEntity> availableAppointments = appointmentRepository.findByTimetableIdAndPatientIdIsNull(timetableId);

        return availableAppointments.stream()
                .map(AppointmentEntity::getAppointmentTime)
                .collect(Collectors.toList());
    }

    public void bookAppointment(Long timetableId, AppointmentRequest appointmentTime, String token) {
        TimetableEntity timetable = timetableRepository.findById(timetableId)
                .orElseThrow(() -> new TimetableNotFoundException("Расписание не найдено"));

        List<LocalDateTime> availableAppointments = getAvailableAppointments(timetableId);

        if (!availableAppointments.contains(appointmentTime.getTime())) {
            throw new InvalidDataException("Тайм-слот уже занят");
        }

        AppointmentEntity appointment = appointmentRepository.findByTimetableId(timetableId)
                .stream()
                .filter(a -> a.getAppointmentTime().equals(appointmentTime.getTime()))
                .findFirst()
                .orElseThrow(() -> new InvalidDataException("Тайм-слот не найден"));

        appointment.setPatientId(getUserIdFromToken(token));
        appointmentRepository.save(appointment);
    }

    @Transactional
    public void deleteAppointment(Long appointmentId, String token) {
        Optional<AppointmentEntity> appointmentOpt = appointmentRepository.findById(appointmentId);

        if(!appointmentOpt.isPresent()){
            throw new InvalidDataException("Тайм-слот не найден");
        }

        AppointmentEntity appointment = appointmentOpt.get();

        if(appointment.getPatientId() == null){
            throw new InvalidDataException("Тайм-слот не забронирован");
        }

        if(!isAllow(token, List.of("ADMIN", "MANAGER", "DOCTOR")) && !appointment.getPatientId().equals(getUserIdFromToken(token))){
            throw new InvalidTokenException("Invalid or expired token");
        }

        appointment.setPatientId(null);
        appointmentRepository.save(appointment);
    }
}
