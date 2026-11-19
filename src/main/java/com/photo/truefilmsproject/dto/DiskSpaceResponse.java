package com.photo.truefilmsproject.dto;

public record DiskSpaceResponse(
        long totalSpaceBytes,
        long freeSpaceBytes,
        long usableSpaceBytes,
        long usedSpaceBytes,
        double usagePercentage
) {}