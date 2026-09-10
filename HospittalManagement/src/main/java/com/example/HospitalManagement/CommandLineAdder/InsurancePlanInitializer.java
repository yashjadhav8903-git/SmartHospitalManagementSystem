package com.example.HospitalManagement.CommandLineAdder;

import com.example.HospitalManagement.Entity.InsurancePlan;
import com.example.HospitalManagement.Enums.InsuranceType;
import com.example.HospitalManagement.Repository.InsurancePlanRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class InsurancePlanInitializer implements CommandLineRunner {

    private final InsurancePlanRepository insurancePlanRepository;
    @Override
    public void run(String... args) throws Exception {

        if(insurancePlanRepository.count()==0){
            log.info("Seeding initial Insurance Plans into database...");


            List<InsurancePlan> insurancePlans = List.of(
                    InsurancePlan.builder()
                            .provider("Star Health")
                            .planName("Optima Care Premium")
                            .validateInMonths(12)
                            .insuranceType(InsuranceType.HEALTH_INSURANCE)
                            .build(),


                    InsurancePlan.builder()
                            .provider("HDFC Ergo")
                            .planName("Health Suraksha Gold")
                            .validateInMonths(12)
                            .insuranceType(InsuranceType.HEALTH_INSURANCE)
                            .build(),

                    InsurancePlan.builder()
                            .provider("Care Insurance")
                            .planName("Care Supreme Shield")
                            .validateInMonths(24)
                            .insuranceType(InsuranceType.HOME_INSURANCE)
                            .build(),

                    InsurancePlan.builder()
                            .provider("ICICI Lombard")
                            .planName("Complete Health Protect")
                            .validateInMonths(12)
                            .insuranceType(InsuranceType.LIFE_INSURANCE)
                            .build()

            );

            insurancePlanRepository.saveAll(insurancePlans);
            log.info("Successfully seeded {} Insurance Plans.", insurancePlans.size());

        }
    }
}
