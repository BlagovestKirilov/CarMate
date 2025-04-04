package com.carmate.service;

import com.carmate.entity.vehicleSupport.VehicleSupport;
import com.carmate.entity.vehicleSupport.VehicleSupportDTO;
import com.carmate.enums.VehicleSupportType;
import com.carmate.repository.VehicleSupportRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class VehicleSupportService {

    private final VehicleSupportRepository vehicleSupportRepository;

    private static final Logger LOGGER = LoggerFactory.getLogger(VehicleSupportService.class);

    @Autowired
    public VehicleSupportService(VehicleSupportRepository vehicleSupportRepository) {
        this.vehicleSupportRepository = vehicleSupportRepository;
    }

    public List<VehicleSupportDTO> getVehicleSupportDTOs() {
        return vehicleSupportRepository.findAll()
                .stream()
                .map(vehicleSupport -> VehicleSupportDTO.builder()
                        .id(vehicleSupport.getId())
                        .name(vehicleSupport.getName())
                        .address(vehicleSupport.getAddress())
                        .type(vehicleSupport.getType().toString())
                        .town(vehicleSupport.getTown())
                        .phoneNumber(vehicleSupport.getPhoneNumber())
                        .build()
                )       .collect(Collectors.toList());
    }

    public void saveVehicleSupport(VehicleSupportDTO vehicleSupportDTO) {
        VehicleSupport vehicleSupport = VehicleSupport.builder()
                .name(vehicleSupportDTO.getName())
                .address(vehicleSupportDTO.getAddress())
                .type(VehicleSupportType.valueOf(vehicleSupportDTO.getType()))
                .town(vehicleSupportDTO.getTown())
                .phoneNumber(vehicleSupportDTO.getPhoneNumber())
                .build();
        vehicleSupportRepository.save(vehicleSupport);
    }


    public void deleteVehicleSupport(Long vehicleSupportID) {
        VehicleSupport vehicleSupport = vehicleSupportRepository.findById(vehicleSupportID).orElseThrow();
        vehicleSupportRepository.delete(vehicleSupport);
        LOGGER.info("Deleting vehicle support with ID: {}", vehicleSupportID);
    }
}
