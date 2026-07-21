package com.renewable.distribution.dto;

import com.renewable.distribution.entity.DeviceType;
import com.renewable.distribution.entity.FaultType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FaultResponse {
    private Long id;
    private DeviceType deviceType;
    private Long deviceId;
    private FaultType faultType;
    private String description;
    private LocalDateTime createdAt;
}
