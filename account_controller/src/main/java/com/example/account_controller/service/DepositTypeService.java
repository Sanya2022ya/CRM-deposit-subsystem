package com.example.account_controller.service;

import com.example.account_controller.model.Account;
import com.example.account_controller.model.ClientDeposit;
import com.example.account_controller.model.DepositType;
import com.example.account_controller.repository.AccountRepository;
import com.example.account_controller.repository.ClientDepositRepository;
import com.example.account_controller.repository.DepositTypeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.springframework.transaction.annotation.Isolation.REPEATABLE_READ;

@Service
@Transactional
public class DepositTypeService {

    private final AccountRepository accountRepo;
    private final DepositTypeRepository typeRepo;
    private final DepositTypeRepository depositTypeRepo;
    private final ClientDepositRepository depositRepo;

    public DepositTypeService(AccountRepository accountRepo,DepositTypeRepository typeRepo,
                          DepositTypeRepository depositTypeRepo,
                          ClientDepositRepository depositRepo) {
        this.accountRepo = accountRepo;
        this.typeRepo = typeRepo;
        this.depositTypeRepo = depositTypeRepo;
        this.depositRepo = depositRepo;
    }

    // можно добавить: пополнение, закрытие, начисление процентов
    public List<DepositType> getAllDepositTypes() {
        return typeRepo.findAll();
    }

    public Optional<DepositType> getDepositTypeById(Long id) {
        return typeRepo.findById(id);
    }

    public DepositType createDepositType(DepositType type) {
        return typeRepo.save(type);
    }

    public Optional<DepositType> updateDepositType(Long id, DepositType updatedType) {
        return typeRepo.findById(id).map(existing -> {
            existing.setName(updatedType.getName());
            existing.setInterestRate(updatedType.getInterestRate());
            existing.setTermMonths(updatedType.getTermMonths());
            existing.setMinAmount(updatedType.getMinAmount());
            existing.setAllowTopUp(updatedType.isAllowTopUp());
            existing.setAllowWithdrawal(updatedType.isAllowWithdrawal());
            existing.setAutoProlongation(updatedType.isAutoProlongation());
            existing.setAutoClose(updatedType.isAllowAutoClose());
            return typeRepo.save(existing);
        });
    }

    public boolean deleteDepositType(Long id) {
        if (typeRepo.existsById(id)) {
            typeRepo.deleteById(id);
            return true;
        }
        return false;
    }

}

