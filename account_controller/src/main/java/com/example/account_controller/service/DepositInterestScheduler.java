package com.example.account_controller.service;

import com.example.account_controller.model.ClientDeposit;
import com.example.account_controller.repository.ClientDepositRepository;
import jakarta.transaction.Transactional;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
@Service
public class DepositInterestScheduler {

    private static final Logger logger = LoggerFactory.getLogger(DepositInterestScheduler.class);
    private final ClientDepositRepository depositRepository;
    private final ReentrantLock lock = new ReentrantLock();

    public DepositInterestScheduler(ClientDepositRepository depositRepository) {
        this.depositRepository = depositRepository;
    }

    @Scheduled(cron = "0 0 1 * * *") // каждый день в 1:00 ночи
//@Scheduled(cron = "*/30 * * * * *") // каждые 30 секунд для теста
    @Transactional
    public void accrueDailyInterest() {
        final int batchSize = 5;
        int pageNumber = 0;

        while (true) {
            Pageable pageable = PageRequest.of(pageNumber, batchSize);
            Page<ClientDeposit> page = depositRepository.findByClosedFalse(pageable);

            if (page.isEmpty()) {
                logger.info("Обработка завершена. Все депозиты обработаны.");
                break;
            }

            logger.info("Обрабатывается партия {} ({} депозитов)", pageNumber + 1, page.getNumberOfElements());

            for (ClientDeposit deposit : page.getContent()) {
                BigDecimal interestRate = deposit.getDepositType().getInterestRate(); // годовая
                BigDecimal dailyRate = interestRate.divide(BigDecimal.valueOf(365), 10, RoundingMode.HALF_UP);

                BigDecimal currentAmount = deposit.getAmount();
                BigDecimal interest = currentAmount.multiply(dailyRate).setScale(2, RoundingMode.HALF_UP);

                deposit.setAmount(currentAmount.add(interest));
                logger.info("Начислены проценты по вкладу ID {}: +{}, новая сумма: {}",
                        deposit.getId(), interest, deposit.getAmount());
            }

            depositRepository.saveAll(page.getContent());

            pageNumber++;
            if (!page.hasNext()) break;
        }
    }
}
