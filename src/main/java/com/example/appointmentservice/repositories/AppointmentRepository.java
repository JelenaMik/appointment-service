package com.example.appointmentservice.repositories;

import com.example.appointmentservice.repositories.model.AppointmentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface AppointmentRepository extends JpaRepository<AppointmentEntity, Long> {

    List<AppointmentEntity> findAllByClientIdAndStartTimeGreaterThanEqualAndStartTimeLessThanEqualOrderByStartTimeAsc(Long clientId, LocalDateTime from, LocalDateTime to);
    List<AppointmentEntity> findAllByProviderIdAndStartTimeGreaterThanEqualAndStartTimeLessThanEqualOrderByStartTimeAsc(Long providerId, LocalDateTime from, LocalDateTime to);

//    findAllByTransactionTypeAndReceiverIdAndTransactionCreateDateGreaterThanEqualAndTransactionCreateDateLessThanEqualOrderByStartTimeAsc(TransactionType.valueOf(transactionType.toUpperCase()), accountId, from, to);

}
