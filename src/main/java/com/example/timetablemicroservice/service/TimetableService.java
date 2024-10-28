package com.example.timetablemicroservice.service;

import com.example.timetablemicroservice.dto.TimetableRequest;
import com.example.timetablemicroservice.dto.RoleValidationResponse;
import com.example.timetablemicroservice.exception.InvalidDataException;
import com.example.timetablemicroservice.exception.InvalidTokenException;
import com.example.timetablemicroservice.model.TimetableEntity;
import com.example.timetablemicroservice.repository.TimetableRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TimetableService {
    private final TimetableRepository appointmentRepository;
    private final RabbitService rabbitService;


    private boolean isAllow(String token, List<String> roles){
        RoleValidationResponse response = rabbitService.sendRoleValidationRequest(token.substring(7), roles);
        return response.isValid();
    }

    private boolean isDoctorExists(Long userId){
        return rabbitService.isDoctorExists(userId);
    }

    private boolean isHospitalExists(Long hospitalId){
        return rabbitService.isHospitalExists(hospitalId);
    }

    private boolean isRoomExists(Long hospitalId, String room){
        return rabbitService.isRoomExists(room, hospitalId );
    }

    private boolean isAppointmentExist(TimetableRequest request) {
        return appointmentRepository.existsByDoctorIdAndStartTimeAndEndTime(
                request.getDoctorId(),
                request.getFrom(),
                request.getTo()
        );
    }

    public void createTimetable(TimetableRequest request, String token) {
        if(!isAllow(token, List.of("ADMIN","MANAGER"))){
            throw new InvalidTokenException("Invalid or expired token");
        }

        if(isAppointmentExist(request)){
            throw new InvalidDataException("Такая запись уже существует");
        }

        if(!isDoctorExists(request.getDoctorId())){
            throw new InvalidDataException("Доктор с таким id не существует");
        }

        if(!isHospitalExists(request.getHospitalId())){
            throw new InvalidDataException("Больница с таким id не существует");
        }

        if(!isRoomExists(request.getHospitalId(), request.getRoom())){
            throw new InvalidDataException("В данной больнице нет комнаты с таким номером");
        }

        try {
            TimetableEntity appointment =  TimetableEntity.builder()
                    .hospitalId(request.getHospitalId())
                    .doctorId(request.getDoctorId())
                    .room(request.getRoom())
                    .startTime(request.getFrom())
                    .endTime(request.getTo())
                    .build();

            appointmentRepository.save(appointment);
        } catch (Exception e) {
            throw new InvalidDataException("Ошибка в данных");
        }
    }


    public void updateTimetable(TimetableRequest request, String token, Long timetableId ) {
        if(!isAllow(token, List.of("ADMIN","MANAGER"))){
            throw new InvalidTokenException("Invalid or expired token");
        }

        Optional<TimetableEntity> existingTimetableOpt = appointmentRepository.findById(timetableId);
        if (!existingTimetableOpt.isPresent()) {
            throw new InvalidDataException("Запись расписания с таким ID не найдена");
        }
        TimetableEntity existingTimetable = existingTimetableOpt.get();

        if(!isDoctorExists(request.getDoctorId())){
            throw new InvalidDataException("Доктор с таким id не существует");
        }

        if(!isHospitalExists(request.getHospitalId())){
            throw new InvalidDataException("Больница с таким id не существует");
        }

        if(!isRoomExists(request.getHospitalId(), request.getRoom())){
            throw new InvalidDataException("В данной больнице нет комнаты с таким номером");
        }

        if (appointmentRepository.existsByDoctorIdAndStartTimeAndEndTimeAndIdNot(
                request.getDoctorId(),
                request.getFrom(),
                request.getTo(),
                timetableId)) {
            throw new InvalidDataException("Такая запись уже существует");
        }

        try {
            existingTimetable.setHospitalId(request.getHospitalId());
            existingTimetable.setDoctorId(request.getDoctorId());
            existingTimetable.setRoom(request.getRoom());
            existingTimetable.setStartTime(request.getFrom());
            existingTimetable.setEndTime(request.getTo());

            appointmentRepository.save(existingTimetable);
        } catch (Exception e) {
            throw new InvalidDataException("Ошибка при обновлении данных");
        }
    }
}
