package ru.netology.patient.service.medical;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import ru.netology.patient.entity.BloodPressure;
import ru.netology.patient.entity.HealthInfo;
import ru.netology.patient.entity.PatientInfo;
import ru.netology.patient.repository.PatientInfoRepository;
import ru.netology.patient.service.alert.SendAlertServiceImpl;

import java.math.BigDecimal;
import java.time.LocalDate;

public class MedicalServiceImplTest {

    private final PatientInfoRepository patientInfoFileRepository = Mockito.mock(PatientInfoRepository.class);
    private final SendAlertServiceImpl alertService = Mockito.spy(SendAlertServiceImpl.class);
    private final MedicalServiceImpl medicalService = new MedicalServiceImpl(patientInfoFileRepository, alertService);
    private final String id = "1";


    MedicalServiceImplTest(){
        Mockito.when(patientInfoFileRepository.getById(Mockito.any())).thenReturn(
                new PatientInfo(id, "Иван", "Петров", LocalDate.of(1980, 11, 26),
                        new HealthInfo(new BigDecimal("36.65"), new BloodPressure(120, 80))));
    }

    @Test
    void bloodPressureTest(){
        medicalService.checkBloodPressure( id, new BloodPressure(60, 120));

        ArgumentCaptor<String> argumentCaptor = ArgumentCaptor.forClass(String.class);
        Mockito.verify(alertService).send(argumentCaptor.capture());
        Assertions.assertEquals(String.format("Warning, patient with id: %s, need help", id), argumentCaptor.getValue());
    }

    @Test
    void lowTemperatureTest(){
        medicalService.checkTemperature(id, new BigDecimal("32.9"));

        ArgumentCaptor<String> argLowTemp = ArgumentCaptor.forClass(String.class);
        Mockito.verify(alertService).send(argLowTemp.capture());
        Assertions.assertEquals(String.format("Warning, patient with id: %s, need help", id), argLowTemp.getValue());
    }

    @Test
    void highTemperatureTest(){
        medicalService.checkTemperature(id, new BigDecimal("37.9"));

        ArgumentCaptor<String> argHighTemp = ArgumentCaptor.forClass(String.class);
        Mockito.verify(alertService).send(argHighTemp.capture()); // Ошикба, не обработан вариант когда температура выше 37. Не знаю нужно ли было это проверять)
        Assertions.assertEquals(String.format("Warning, patient with id: %s, need help", id), argHighTemp.getValue());
    }

    @Test
    void normalIndicatorsTest(){
        medicalService.checkBloodPressure(id, new BloodPressure(120, 80));
        medicalService.checkTemperature(id, new BigDecimal("36.8"));
        Mockito.verify(alertService, Mockito.never()).send(Mockito.any());
    }
}
