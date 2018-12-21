package com.nl.hotels.dto;


import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
public class HotelsDataResponse implements Serializable {
    List<HotelsData> hotels;
}
