package si.uni.prpo.group03.paymentservice.repository;

import si.uni.prpo.group03.paymentservice.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Payment findByPaypalOrderId(String paypalOrderId);
    Payment findByUserId(Long userId);
}