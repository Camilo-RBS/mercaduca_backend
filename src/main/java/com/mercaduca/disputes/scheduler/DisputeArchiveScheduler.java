package com.mercaduca.disputes.scheduler;

import com.mercaduca.disputes.entity.Dispute;
import com.mercaduca.disputes.repository.DisputeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Tarea programada que archiva automáticamente las disputas resueltas.
 *
 * Política de archivo:
 * - Las disputas con estado RESOLVED_BUYER, RESOLVED_SELLER o CLOSED
 *   se archivan automáticamente después de 48 horas de su resolución.
 * - Las disputas archivadas cambian a estado ARCHIVED.
 * - Se mantienen en la base de datos para auditoría.
 * - Las vistas activas filtran por estado != ARCHIVED.
 *
 * Se ejecuta cada hora para garantizar puntualidad sin sobrecarga.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DisputeArchiveScheduler {

    private final DisputeRepository disputeRepository;

    private static final List<Dispute.DisputeStatus> RESOLVABLE_STATUSES = List.of(
            Dispute.DisputeStatus.RESOLVED_BUYER,
            Dispute.DisputeStatus.RESOLVED_SELLER,
            Dispute.DisputeStatus.CLOSED
    );

    /** Ejecutar cada hora */
    @Scheduled(fixedRate = 3_600_000)
    @Transactional
    public void archiveResolvedDisputes() {
        LocalDateTime cutoff = LocalDateTime.now().minusHours(48);
        List<Dispute> toArchive = disputeRepository
                .findByStatusInAndUpdatedAtBefore(RESOLVABLE_STATUSES, cutoff);

        if (toArchive.isEmpty()) return;

        toArchive.forEach(d -> d.setStatus(Dispute.DisputeStatus.ARCHIVED));
        disputeRepository.saveAll(toArchive);
        log.info("Archivadas {} disputas resueltas con más de 48 horas", toArchive.size());
    }
}
